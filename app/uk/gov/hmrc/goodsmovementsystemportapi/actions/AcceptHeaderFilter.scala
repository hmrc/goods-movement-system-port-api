/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import cats.implicits.catsSyntaxEq
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

@Singleton
class AcceptHeaderFilter @Inject()()(implicit val executionContext: ExecutionContext) extends ActionFilter[Request] {

  val validateVersion: String => Boolean = _ === "1.0"

  val validateContentType: String => Boolean = _ === "json"

  val matchHeader
    : String => Option[Match] = new Regex("""^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype") findFirstMatchIn _

  val acceptHeaderValidationRules: Option[String] => Boolean =
    _ flatMap (a => matchHeader(a) map (res => validateContentType(res.group("contenttype")) && validateVersion(res.group("version")))) getOrElse false

  override protected def filter[A](request: Request[A]): Future[Option[Result]] =
    Future {
      if (acceptHeaderValidationRules(request.headers.get("Accept")))
        None
      else
        Some(Status(InvalidAcceptHeader.httpStatusCode)(Json.toJson[ErrorResponse](InvalidAcceptHeader)))
    }
}
