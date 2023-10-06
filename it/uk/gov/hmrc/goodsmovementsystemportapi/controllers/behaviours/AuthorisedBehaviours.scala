/*
 * Copyright 2023 HM Revenue & Customs
 *
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
