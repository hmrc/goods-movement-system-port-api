/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.test.FutureAwaits
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.GetControlledArrivalsGmrResponse
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.Future

class GoodsMovementSystemConnectorSpec extends BaseSpec with FutureAwaits {

  trait Setup {
    val baseUrl = "http://localhost:0000"
    val mockHttpClient: HttpClient                   = mock[HttpClient]
    val connector:      GoodsMovementSystemConnector = new GoodsMovementSystemConnector(mockHttpClient, baseUrl)
  }

  "getControlledArrivalsGmr" when {
    "get controlled arrivals is successful" should {
      "retrieve the response" in new Setup {
        val portId = "abc"
        val url    = s"$baseUrl/goods-movement-system/$portId/arrivals/controlled"

        when(mockHttpClient.GET[List[GetControlledArrivalsGmrResponse]](mEq(url), any(), any())(any(), any(), any()))
          .thenReturn(Future(arrivalsGmrResponse))

        val result = await(connector.getControlledArrivalsGmr(portId)(hc))

        result shouldBe arrivalsGmrResponse

        verify(mockHttpClient).GET(mEq(s"http://localhost:0000/goods-movement-system/$portId/arrivals/controlled"), any(), any())(any(), any(), any())
      }
    }
  }
}
