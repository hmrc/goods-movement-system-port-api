/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.mvc.Result
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ControllerBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataControllerSpec extends ControllerBaseSpec {

  trait Setup {
    val controller = new GmsReferenceDataController(preAuthenticatedActionBuilders, mockGmsReferenceDataService, stubControllerComponents())
  }

  "getReferenceData" when {
    "getReferenceData is successful" should {
      "return 200 OK" in new Setup {

        when(mockGmsReferenceDataService.getReferenceData(any()))
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: Future[Result] = controller.getReferenceData(fakeRequest)

        status(result)                              shouldBe OK
        contentAsJson(result).as[GvmsReferenceData] shouldBe gmsReferenceDataSummary

        verify(mockGmsReferenceDataService).getReferenceData(any())
      }
    }
  }
}
