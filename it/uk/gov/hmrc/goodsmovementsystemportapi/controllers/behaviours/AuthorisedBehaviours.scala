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

package uk.gov.hmrc.goodsmovementsystemportapi.controllers.behaviours

import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseISpec
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.stubs.AuthStubs

trait AuthorisedBehaviours {
  this: BaseISpec =>

  def authorisedEndpoint(call: Call): Unit = {
    "should return 401 UNAUTHORIZED when the user isn't logged in" in {
      AuthStubs.missingBearerToken()
      val result = callRoute(fakeRequest(call).withHeaders(CONTENT_TYPE -> JSON))

      status(result)          shouldBe UNAUTHORIZED
      contentAsString(result) shouldBe ""
    }

    "should return 401 UNAUTHORIZED when the users session has expired" in {
      AuthStubs.sessionHasExpired()
      val result = callRoute(fakeRequest(call).withHeaders(CONTENT_TYPE -> JSON))

      status(result)          shouldBe UNAUTHORIZED
      contentAsString(result) shouldBe ""
    }

    "should return 401 UNAUTHORIZED when the users bearer token is invalid" in {
      AuthStubs.invalidBearerToken()
      val result = callRoute(fakeRequest(call).withHeaders(CONTENT_TYPE -> JSON))

      status(result)          shouldBe UNAUTHORIZED
      contentAsString(result) shouldBe ""
    }
  }
}
