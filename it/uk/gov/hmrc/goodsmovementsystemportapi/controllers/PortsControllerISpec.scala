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

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import com.github.tomakehurst.wiremock.client.WireMock.{getRequestedFor, urlEqualTo, verify}
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.goodsmovementsystemportapi.controllers.behaviours.{AuthorisedBehaviours, SchemaContractBehaviours}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseISpec
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.stubs.AuthStubs
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrReducedResponse, GetControlledDeparturesGmrReducedResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.Version_1_0

import scala.concurrent.Future

class PortsControllerISpec extends BaseISpec with SchemaContractBehaviours with AuthorisedBehaviours {

  val controller: PortsController = app.injector.instanceOf[PortsController]

  "GET /:portId/arrivals/controlled" should {
    val portId = "1"
    behave like authorisedEndpoint(routes.PortsController.getControlledGmrsForArrivals("portId"))
    "return 200 OK" in {
      AuthStubs.userIsAuthenticated

      stubGet(s"/goods-movement-system/$portId/arrivals/controlled", Json.stringify(Json.toJson(arrivalsGmrResponse)))

      stubGet(s"/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gmsReferenceDataSummay)))

      stubGet(
        s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
        s"""
                                                                                                               |{
                                                                                                               |  "clientId": "clientId-abc",
                                                                                                               |  "apiContext": "goods-movement-system-port",
                                                                                                               |  "apiVersion": "1.0",
                                                                                                               |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
                                                                                                               |  "fields": {
                                                                                                               |    "portId": "$portId"
                                                                                                               |  }
                                                                                                               |}
                                                                                                               |""".stripMargin
      )

      val result: Future[Result] = callRoute(fakeRequest(routes.PortsController.getControlledGmrsForArrivals(portId)))

      status(result) shouldBe OK
      contentAsJson(result).as[List[GetControlledArrivalsGmrReducedResponse]] shouldBe arrivalsGmrResponse.map(
        GetControlledArrivalsGmrReducedResponse.apply
      )

      validateAgainstSchema(result, "get-controlled-gmrs-for-arrival-schema.json", Version_1_0)
      verify(getRequestedFor(urlEqualTo("/goods-movement-system/1/arrivals/controlled")))
    }

    "the arrival port does not exist" should {
      "return 403 PORT_ID_NOT_FOUND" in {
        AuthStubs.userIsAuthenticated

        stubGet(
          s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
          s"""
                                                                                                                 |{
                                                                                                                 |  "clientId": "clientId-abc",
                                                                                                                 |  "apiContext": "goods-movement-system-port",
                                                                                                                 |  "apiVersion": "1.0",
                                                                                                                 |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
                                                                                                                 |  "fields": {
                                                                                                                 |    "portId": "12"
                                                                                                                 |  }
                                                                                                                 |}
                                                                                                                 |""".stripMargin
        )

        val result = callRoute(fakeRequest(routes.PortsController.getControlledGmrsForArrivals(portId)))

        status(result) shouldBe FORBIDDEN
        assertErrorResponse(result, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you.", nestedErrorCount = 0)

        verify(getRequestedFor(urlEqualTo("/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0")))
      }
    }

  }

  "GET /:portId/departures/controlled" should {
    val portId = "1"
    behave like authorisedEndpoint(routes.PortsController.getControlledGmrsForDepartures("portId"))

    "return 200 OK" in {
      AuthStubs.userIsAuthenticated

      stubGet(s"/goods-movement-system/$portId/departures/controlled", Json.stringify(Json.toJson(departuresGmrResponse)))
      stubGet(s"/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gmsReferenceDataSummay)))
      stubGet(
        s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
        s"""
                                                                                                               |{
                                                                                                               |  "clientId": "clientId-abc",
                                                                                                               |  "apiContext": "goods-movement-system-port",
                                                                                                               |  "apiVersion": "1.0",
                                                                                                               |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
                                                                                                               |  "fields": {
                                                                                                               |    "portId": "$portId"
                                                                                                               |  }
                                                                                                               |}
                                                                                                               |""".stripMargin
      )

      val result: Future[Result] = callRoute(fakeRequest(routes.PortsController.getControlledGmrsForDepartures(portId)))

      status(result) shouldBe OK
      contentAsJson(result).as[List[GetControlledDeparturesGmrReducedResponse]] shouldBe departuresGmrResponse.map(
        GetControlledDeparturesGmrReducedResponse.apply
      )

      validateAgainstSchema(result, "get-controlled-gmrs-for-departure-schema.json", Version_1_0)
      verify(getRequestedFor(urlEqualTo("/goods-movement-system/1/departures/controlled")))
    }

    "the departure port does not exist" should {

      "return 403 PORT_ID_NOT_FOUND" in {
        AuthStubs.userIsAuthenticated

        stubGet(
          s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
          s"""
                                                                                                                 |{
                                                                                                                 |  "clientId": "clientId-abc",
                                                                                                                 |  "apiContext": "goods-movement-system-port",
                                                                                                                 |  "apiVersion": "1.0",
                                                                                                                 |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
                                                                                                                 |  "fields": {
                                                                                                                 |    "portId": "12"
                                                                                                                 |  }
                                                                                                                 |}
                                                                                                                 |""".stripMargin
        )

        val result = callRoute(fakeRequest(routes.PortsController.getControlledGmrsForDepartures(portId)))

        status(result) shouldBe FORBIDDEN
        assertErrorResponse(result, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you.", nestedErrorCount = 0)

        verify(getRequestedFor(urlEqualTo("/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0")))
      }
    }
  }

  "GET /:portId/departures" should {
    val portId = "1"
    behave like authorisedEndpoint(routes.PortsController.getGmrsForDepartures("portId", None, None))

    "return 200 OK" in {
      AuthStubs.userIsAuthenticated

      stubGet(s"/goods-movement-system/$portId/departures", Json.stringify(Json.toJson(departuresExtendedGmrResponse)))
      stubGet(s"/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gmsReferenceDataSummay)))
      stubGet(
        s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
        s"""
           |{
           |  "clientId": "clientId-abc",
           |  "apiContext": "goods-movement-system-port",
           |  "apiVersion": "1.0",
           |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
           |  "fields": {
           |    "portId": "$portId"
           |  }
           |}
           |""".stripMargin
      )

      val result: Future[Result] = callRoute(fakeRequest(routes.PortsController.getGmrsForDepartures(portId, None, None)))

      status(result)                                                      shouldBe OK
      contentAsJson(result).as[List[GetPortDepartureExpandedGmrResponse]] shouldBe departuresExtendedGmrResponse

      validateAgainstSchema(result, "get-checked-in-gmrs-for-departure-schema.json", Version_1_0)
      verify(getRequestedFor(urlEqualTo("/goods-movement-system/1/departures")))
    }

    "the departure port does not exist" should {

      "return 403 PORT_ID_NOT_FOUND" in {
        AuthStubs.userIsAuthenticated

        stubGet(
          s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
          s"""
             |{
             |  "clientId": "clientId-abc",
             |  "apiContext": "goods-movement-system-port",
             |  "apiVersion": "1.0",
             |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
             |  "fields": {
             |    "portId": "12"
             |  }
             |}
             |""".stripMargin
        )

        val result = callRoute(fakeRequest(routes.PortsController.getGmrsForDepartures(portId, None, None)))

        status(result) shouldBe FORBIDDEN
        assertErrorResponse(result, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you.", nestedErrorCount = 0)

        verify(getRequestedFor(urlEqualTo("/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0")))
      }
    }

    "the response contains too many gmrs" should {

      "return 400 TOO_MANY_RESULTS" in {
        AuthStubs.userIsAuthenticated

        stubGet(s"/goods-movement-system/$portId/departures", Json.stringify(Json.obj("code" -> "TOO_MANY_RESULTS")))

        stubGet(s"/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gmsReferenceDataSummay)))
        stubGet(
          s"/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0",
          s"""
             |{
             |  "clientId": "clientId-abc",
             |  "apiContext": "goods-movement-system-port",
             |  "apiVersion": "1.0",
             |  "fieldsId": "55c2b945-1c82-4749-b4fc-42e5u32192ew",
             |  "fields": {
             |    "portId": "$portId"
             |  }
             |}
             |""".stripMargin
        )

        val result = callRoute(fakeRequest(routes.PortsController.getGmrsForDepartures(portId, None, None)))

        status(result) shouldBe BAD_REQUEST
        assertErrorResponse(
          result,
          "TOO_MANY_RESULTS",
          "Too many goods movement records were found. Please filter down your query.",
          nestedErrorCount = 0
        )

        verify(getRequestedFor(urlEqualTo("/field/application/clientId-abc/context/customs%2Fgoods-movement-system-port/version/1.0")))
      }
    }
  }

}
