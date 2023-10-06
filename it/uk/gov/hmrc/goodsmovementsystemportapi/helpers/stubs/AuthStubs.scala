/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object AuthStubs {

  def userIsAuthenticated: StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(
          s"""
               |{
               |  "authorise": [
               |    {
               |      "authProviders": ["StandardApplication"]
               |    }
               |  ],
               |  "retrieve": [
               |    "clientId"
               |  ]
               |}
               |""".stripMargin,
          true,
          true
        ))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(s"""{ "clientId": "clientId-abc" }""".stripMargin)
        ))

  def missingBearerToken(): StubMapping =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(
          s"""
             |{
             |  "authorise": [
             |    {
             |      "authProviders": ["StandardApplication"]
             |    }
             |  ],
             |  "retrieve": [
             |    "clientId"
             |  ]
             |}
             |           """.stripMargin,
          true,
          true
        ))
        .willReturn(aResponse()
          .withStatus(401)
          .withHeader("WWW-Authenticate", """MDTP detail="MissingBearerToken"""")))

  def invalidBearerToken(): StubMapping =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(
          s"""
             |{
             |  "authorise": [
             |    {
             |      "authProviders": ["StandardApplication"]
             |    }
             |  ],
             |  "retrieve": [
             |    "clientId"
             |  ]
             |}
             |           """.stripMargin,
          true,
          true
        ))
        .willReturn(aResponse()
          .withStatus(401)
          .withHeader("WWW-Authenticate", """MDTP detail="InvalidBearerToken"""")))

  def sessionHasExpired(): StubMapping =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(
          s"""
             |{
             |  "authorise": [
             |    {
             |      "authProviders": ["StandardApplication"]
             |    }
             |  ],
             |  "retrieve": [
             |    "clientId"
             |  ]
             |}
             |           """.stripMargin,
          true,
          true
        ))
        .willReturn(aResponse()
          .withStatus(401)
          .withHeader("WWW-Authenticate", """MDTP detail="BearerTokenExpired"""")))

}
