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

package uk.gov.hmrc.goodsmovementsystemportapi.helpers.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object ApiSubscriptionFieldsStubs {

  def getSubscriptionFields: StubMapping =
    stubFor(
      get(urlEqualTo("/field/application/clientId-abc/context/customs%2Fgoods-movement-system-carrier/version/1.0"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(s"""
                 |{
                 |  "clientId": "clientId-abc",
                 |  "apiContext": "goods-movement-system-carrier",
                 |  "apiVersion": "1.0",
                 |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
                 |  "fields": {
                 |    "carrierId": "C00011"
                 |  }
                 |}
                 |""".stripMargin)
        ))
}
