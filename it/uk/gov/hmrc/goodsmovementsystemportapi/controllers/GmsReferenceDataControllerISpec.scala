/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import com.github.tomakehurst.wiremock.client.WireMock.{urlEqualTo, _}
import play.api.libs.json.Json
import uk.gov.hmrc.goodsmovementsystemportapi.controllers.behaviours.{AuthorisedBehaviours, HeaderValidationBehaviours, SchemaContractBehaviours}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseISpec

class GmsReferenceDataControllerISpec extends BaseISpec with AuthorisedBehaviours with HeaderValidationBehaviours with SchemaContractBehaviours {

  "GET /reference-data" when {

    behave like headerValidatedEndpoint(routes.GmsReferenceDataController.getReferenceData)

    "the Reference data exists" should {
      "return 200 OK " in {
        stubGet(s"/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gmsReferenceDataSummary)))

        val result   = callRoute(fakeRequest(routes.GmsReferenceDataController.getReferenceData))
        val expected = Json.toJson(gmsReferenceDataSummary)
        status(result)        shouldBe OK
        contentAsJson(result) shouldBe expected

        verify(getRequestedFor(urlEqualTo(s"/goods-movement-system-reference-data/reference-data")))
      }
    }

  }
}
