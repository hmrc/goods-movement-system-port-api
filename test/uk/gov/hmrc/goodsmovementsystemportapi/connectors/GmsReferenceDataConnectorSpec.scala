/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.test.FutureAwaits
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataConnectorSpec extends BaseSpec with FutureAwaits {

  trait Setup {
    val connector: GmsReferenceDataConnector = new GmsReferenceDataConnector(mockHttpClient, "http://localhost:0000")
  }
  "getReferenceData" when {
    "the GET was successful" should {
      "return the http response" in new Setup {

        when(
          mockHttpClient
            .GET[GvmsReferenceData](mEq("http://localhost:0000/goods-movement-system-reference-data/reference-data"), any(), any())(
              any(),
              any(),
              any()))
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: GvmsReferenceData = await(connector.getReferenceData(hc))

        result shouldBe gmsReferenceDataSummary

        verify(mockHttpClient)
          .GET[GvmsReferenceData](mEq("http://localhost:0000/goods-movement-system-reference-data/reference-data"), any(), any())(any(), any(), any())
      }
    }

  }

}
