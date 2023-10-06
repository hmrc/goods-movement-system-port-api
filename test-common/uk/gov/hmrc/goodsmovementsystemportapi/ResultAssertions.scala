/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi

import akka.stream.Materializer
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.goodsmovementsystemportapi.TestClasses.JsonApiErrorResponse

import scala.concurrent.Future

trait ResultAssertions { me: Matchers with ScalaFutures with Inside =>

  def assertJsonBodyOf[T](result: Future[Result])(block: T => Assertion)(implicit reads: Reads[T], mat: Materializer): Assertion =
    inside(contentAsJson(result).validate[T]) {
      case JsSuccess(model, _) => block(model)
      case JsError(errs) =>
        val errors = errs.map(_._1).map(_.toString().replaceAll(".*/(.*)", "$1")).mkString(", ")
        fail(s"${contentAsString(result)} \n\nFailed JSON validation. Missing fields: $errors\n")

    }

  def assertErrorResponse(result: Future[Result], code: String, message: String, nestedErrorCount: Int)(implicit mat: Materializer): Assertion =
    assertJsonBodyOf[JsonApiErrorResponse](result) { error =>
      error.code                  shouldBe code
      error.message               should fullyMatch regex s"$message".r
      error.errors.toList.flatten should have size nestedErrorCount
    }
}
