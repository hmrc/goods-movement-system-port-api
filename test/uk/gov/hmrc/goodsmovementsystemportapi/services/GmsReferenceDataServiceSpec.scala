/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.services

import org.mockito.Mockito._
import play.api.test.Helpers.await
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataServiceSpec extends BaseSpec {

  trait Setup {
    val service = new GmsReferenceDataService(mockGmsReferenceDataConnector)
  }

  "getReferenceData" when {
    "the getReferenceData is successful" should {
      "return the 200(OK)" in new Setup {

        when(mockGmsReferenceDataConnector.getReferenceData)
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: GvmsReferenceData = await(service.getReferenceData)

        result shouldBe gmsReferenceDataSummary

        verify(mockGmsReferenceDataConnector).getReferenceData
      }
    }

  }

}
