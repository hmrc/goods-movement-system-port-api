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

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import org.mockito.ArgumentMatchers.{any, eq as mEq}
import org.mockito.Mockito.*
import play.api.test.FutureAwaits
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.GetControlledArrivalsGmrResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.Future

class GoodsMovementSystemConnectorSpec extends BaseSpec with FutureAwaits {

  trait Setup {
    val baseUrl = "http://localhost:0000"
    val mockHttpClientV2: HttpClientV2                 = mock[HttpClientV2]
    val connector:        GoodsMovementSystemConnector = new GoodsMovementSystemConnector(mockHttpClient, baseUrl)
  }

  "getControlledArrivalsGmr" when {
    "get controlled arrivals is successful" should {
      "retrieve the response" in new Setup {
        val portId = "abc"
        val url    = url"$baseUrl/goods-movement-system/$portId/arrivals/controlled"

        when(mockHttpClient.get(mEq(url))(any()))
          .thenReturn(mockRequestBuilder)
        when(mockRequestBuilder.execute(using any(), any()))
          .thenReturn(Future(arrivalsGmrResponse))

        val result = await(connector.getControlledArrivalsGmr(portId)(hc))

        result shouldBe arrivalsGmrResponse

        verify(mockHttpClient).get(mEq(url"http://localhost:0000/goods-movement-system/$portId/arrivals/controlled"))(any())
        verify(mockRequestBuilder).execute(using any(), any())
      }
    }
  }
}
