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

import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.test.FutureAwaits
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse

import scala.concurrent.Future

class ApiSubscriptionFieldsConnectorSpec extends BaseSpec with FutureAwaits {

  trait setup {
    val baseUrl       = "http://localhost:0000"
    val mockAppConfig = mock[AppConfig]
    val apiContext    = "apiContext"
    val apiVersion    = "1.0"
    when(mockAppConfig.apiVersion).thenReturn(apiVersion)
    when(mockAppConfig.apiContext).thenReturn(apiContext)

    val clientId = "clientId"
  }

  "getField" when {
    "call api subscription field microservice" should {
      "return carrierId successfully" in new setup {
        val connector               = new ApiSubscriptionFieldsConnector(mockHttpClient, mockAppConfig, baseUrl)
        val expectedSuccessResponse = subscriptionFieldsResponse

        when(
          mockHttpClient
            .GET[SubscriptionFieldsResponse](mEq(s"$baseUrl/field/application/$clientId/context/$apiContext/version/$apiVersion"), any(), any())(
              any(),
              any(),
              any()
            )
        )
          .thenReturn(Future.successful(expectedSuccessResponse))

        val result = await(connector.getSubscriptionFields(clientId)(hc))

        result shouldBe expectedSuccessResponse
      }
    }
  }
}
