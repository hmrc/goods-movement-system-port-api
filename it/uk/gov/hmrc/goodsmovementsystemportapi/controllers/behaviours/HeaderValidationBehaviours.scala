/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers.behaviours

import org.scalatest.Assertion
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseISpec
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.stubs.AuthStubs

trait HeaderValidationBehaviours {
  this: BaseISpec =>

  def headerValidatedEndpoint(call: Call): Unit = {

    "should return 400 BAD REQUEST when accept header is missing" in {
      AuthStubs.userIsAuthenticated
      val header  = "a" -> "b"
      val message = "The accept header is missing or invalid"

      assertHeaderErrors(header, message)
    }

    "should return 400 BAD REQUEST when accept header is invalid" in {
      AuthStubs.userIsAuthenticated
      val header  = "Accept" -> "invalid"
      val message = "The accept header is missing or invalid"

      assertHeaderErrors(header, message)
    }

    "should return 400 BAD REQUEST when version number is invalid" in {
      AuthStubs.userIsAuthenticated
      val header  = "Accept" -> "application/vnd.hmrc.0.9+json"
      val message = "The accept header is missing or invalid"

      assertHeaderErrors(header, message)
    }

    "should return 400 BAD REQUEST when content-type is invalid" in {
      AuthStubs.userIsAuthenticated
      val header  = "Accept" -> "application/vnd.hmrc.1.2+svg"
      val message = "The accept header is missing or invalid"

      assertHeaderErrors(header, message)
    }

    def assertHeaderErrors(header: (String, String), message: String): Assertion = {
      val result = callRoute(FakeRequest(call).withHeaders(header, "Content-Type" -> "application/json").withBody(Json.obj()))

      status(result) shouldBe NOT_ACCEPTABLE
      assertErrorResponse(result, "ACCEPT_HEADER_INVALID", message, 0)
    }
  }
}
