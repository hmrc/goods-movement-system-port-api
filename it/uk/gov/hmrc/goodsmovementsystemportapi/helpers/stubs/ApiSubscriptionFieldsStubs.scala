/*
 * Copyright 2023 HM Revenue & Customs
 *
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
