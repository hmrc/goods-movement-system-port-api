/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.connectors.httpreads

import cats.implicits._
import play.api.libs.json.JsValue
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.GetDepartureErrors
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.PortErrors.{InvalidDateCombinationError, TooManyGmrsError}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.Try
import uk.gov.hmrc.http.HttpReads.Implicits._

trait CustomEitherHttpReads {

  private def isCustomError(json: => JsValue)(predicate: String => Boolean): Boolean =
    Try((json \ "code").asOpt[String].exists(predicate)).getOrElse(false)

  implicit def readEitherOf[E, P](implicit customErrors: PartialFunction[HttpResponse, E], rds: HttpReads[P]): HttpReads[Either[E, P]] =
    for {
      response <- HttpReads[HttpResponse]
      result <- customErrors
                 .andThen(_.asLeft[P])
                 .andThen(HttpReads.pure _)
                 .applyOrElse[HttpResponse, HttpReads[Either[E, P]]](response, _ => rds.map(_.asRight[E]))
    } yield result

  implicit def getDeparturesPartialFunction[P]: PartialFunction[HttpResponse, GetDepartureErrors] = {

    object InvalidCombinationExtractor {

      def unapply(response: HttpResponse): Boolean =
        isCustomError(response.json)(_ === "INVALID_DATE_COMBINATION")
    }

    object TooManyResultsExtractor {

      def unapply(response: HttpResponse): Boolean =
        isCustomError(response.json)(_ === "TOO_MANY_RESULTS")
    }

    {
      case InvalidCombinationExtractor() => InvalidDateCombinationError
      case TooManyResultsExtractor()     => TooManyGmrsError
    }
  }

}
