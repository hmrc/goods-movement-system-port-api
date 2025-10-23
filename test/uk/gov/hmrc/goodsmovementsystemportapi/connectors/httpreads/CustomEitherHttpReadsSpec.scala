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

package uk.gov.hmrc.goodsmovementsystemportapi.connectors.httpreads

import org.scalatest.EitherValues
import play.api.libs.json.Json
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.GetDepartureErrors
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.PortErrors.{InvalidDateCombinationError, TooManyGmrsError}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.http.*

class CustomEitherHttpReadsSpec extends BaseSpec with EitherValues {

  trait Setup {
    val reads: CustomEitherHttpReads = new CustomEitherHttpReads {}
  }

  "readsEitherOf" when {

    "getDeparturesPartialFunction" should {
      "read successful 200 response as successful right" in new Setup {
        val response: HttpResponse = HttpResponse(200, "")

        val result: Either[GetDepartureErrors, HttpResponse] =
          reads.readEitherOf(reads.getDeparturesPartialFunction, HttpReadsInstances.readRaw).read("GET", "http://localhost", response)

        result.value shouldBe response
      }

      "read unsuccessful responses and transform custom errors" in new Setup {
        Map(
          HttpResponse(200, Json.obj("code" -> "INVALID_DATE_COMBINATION"), Map.empty[String, Seq[String]]) -> InvalidDateCombinationError,
          HttpResponse(200, Json.obj("code" -> "TOO_MANY_RESULTS"), Map.empty[String, Seq[String]])         -> TooManyGmrsError
        ).foreachEntry { case (response, expected) =>
          val result = reads
            .readEitherOf(
              reads.getDeparturesPartialFunction,
              HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(using HttpReadsInstances.readRaw))
            )
            .read("GET", "http://localhost", response)

          result.left.value shouldBe expected
        }
      }

      // This test only covers a limited amount of 5xx and 4xx cases.
      "read unsuccessful response and transform to base errors" in new Setup {
        List(
          HttpResponse(400, ""),
          HttpResponse(401, ""),
          HttpResponse(403, ""),
          HttpResponse(404, ""),
          HttpResponse(409, ""),
          HttpResponse(413, ""),
          HttpResponse(500, ""),
          HttpResponse(501, ""),
          HttpResponse(502, ""),
          HttpResponse(503, ""),
          HttpResponse(504, "")
        ).foreach { response =>
          intercept[UpstreamErrorResponse](
            reads
              .readEitherOf(
                reads.getDeparturesPartialFunction,
                HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(using HttpReadsInstances.readRaw))
              )
              .read("GET", "http://localhost", response)
          )
        }
      }
    }

  }
}
