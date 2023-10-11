/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
