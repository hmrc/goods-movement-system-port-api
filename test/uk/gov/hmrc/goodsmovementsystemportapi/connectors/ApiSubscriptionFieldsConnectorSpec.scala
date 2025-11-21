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
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.http.StringContextOps

import scala.concurrent.Future

class ApiSubscriptionFieldsConnectorSpec extends BaseSpec with FutureAwaits {

  trait setup {
    val baseUrl    = "http://localhost:0000"
    val apiContext = "apiContext"
    val apiVersion = "1.0"
    when(mockAppConfig.apiVersion).thenReturn(apiVersion)
    when(mockAppConfig.apiContext).thenReturn(apiContext)

    val clientId = "clientId"
  }

  "getField" when {
    "call api subscription field microservice" should {
      "return carrierId successfully" in new setup {
        val connector = new ApiSubscriptionFieldsConnector(mockHttpClient, mockAppConfig, baseUrl)
        val expectedSuccessResponse: SubscriptionFieldsResponse = subscriptionFieldsResponse

        when(mockHttpClient.get(mEq(url"$baseUrl/field/application/$clientId/context/$apiContext/version/$apiVersion"))(any()))
          .thenReturn(mockRequestBuilder)
        when(mockRequestBuilder.execute(using any(), any()))
          .thenReturn(Future.successful(expectedSuccessResponse))

        val result: SubscriptionFieldsResponse = await(connector.getSubscriptionFields(clientId)(hc))

        result shouldBe expectedSuccessResponse
        verify(mockRequestBuilder).execute(using any(), any())
      }
    }
  }
}
