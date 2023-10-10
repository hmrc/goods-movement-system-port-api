/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import cats.implicits._
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc.{ActionRefiner, Request, Result}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.VersionedRequest
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse.InvalidAcceptHeader
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.InvalidApiVersion.{InvalidAcceptHeaderFormat, MissingAcceptHeader, UnsupportedVersion}
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

@Singleton
class ApiVersionRefiner @Inject()()(implicit val executionContext: ExecutionContext) extends ActionRefiner[Request, VersionedRequest] {

  val matchHeader
    : String => Option[Match] = new Regex("""^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype") findFirstMatchIn _

  override protected def refine[A](request: Request[A]): Future[Either[Result, VersionedRequest[A]]] =
    Future {
      val eitherResultApiVersion = for {
        accept     <- request.headers.get("Accept").toRight(MissingAcceptHeader).right
        version    <- matchHeader(accept).map(_.group("version")).toRight(InvalidAcceptHeaderFormat).right
        apiVersion <- ApiVersion.of(version).toRight(UnsupportedVersion).right
      } yield apiVersion

      eitherResultApiVersion.fold({
        case MissingAcceptHeader       => errorResponse
        case InvalidAcceptHeaderFormat => errorResponse
        case UnsupportedVersion        => errorResponse
      }, requestWithVersion(request))
    }

  def errorResponse[A]: Either[Result, VersionedRequest[A]] =
    Status(InvalidAcceptHeader.httpStatusCode)(Json.toJson[ErrorResponse](InvalidAcceptHeader)).asLeft[VersionedRequest[A]]

  def requestWithVersion[A](request: Request[A])(version: ApiVersion): Either[Result, VersionedRequest[A]] =
    VersionedRequest(version, request).asRight[Result]
}