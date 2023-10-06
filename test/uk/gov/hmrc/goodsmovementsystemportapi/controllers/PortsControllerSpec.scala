/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import cats.data.EitherT
import cats.implicits._
import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.goodsmovementsystemportapi.ResultAssertions
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.{ErrorResponse, GetControlledArrivalErrors, GetControlledDepartureErrors, GetDepartureErrors, PortErrors}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ControllerBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrReducedResponse, GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrReducedResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.http.HttpResponse

import java.time.Instant
import scala.concurrent.Future

class PortsControllerSpec extends ControllerBaseSpec with ResultAssertions {

  trait Setup {

    val controller =
      new PortsController(mockPortService, preAuthenticatedActionBuilders, stubControllerComponents())

    val portId = "portId"
    val httpResponse: HttpResponse = HttpResponse(200, "")
  }

  "getControlledArrivalsGmr" when {
    "get arrivals gmr is successful" should {
      "return 200 OK" in new Setup {

        when(mockPortService.getArrivals(mEq("clientId"), mEq(portId), mEq(false))(any()))
          .thenReturn(EitherT.rightT[Future, GetControlledArrivalErrors](arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)))

        val result = controller.getControlledGmrsForArrivals(portId)(fakeRequest)

        status(result) shouldBe OK
        contentAsJson(result).as[List[GetControlledArrivalsGmrReducedResponse]] shouldBe arrivalsGmrResponse.map(
          GetControlledArrivalsGmrReducedResponse.apply)

        verify(mockPortService).getArrivals(mEq("clientId"), mEq(portId), mEq(false))(any())
      }

      "return 200 OK with ignore_effective_dates" in new Setup {

        when(mockPortService.getArrivals(mEq("clientId"), mEq(portId), mEq(true))(any()))
          .thenReturn(EitherT.rightT[Future, GetControlledArrivalErrors](arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)))

        val result =
          controller.getControlledGmrsForArrivals(portId)(fakeRequest.withHeaders(fakeRequest.headers.add("IGNORE-Effective-DatES" -> "true")))

        status(result) shouldBe OK
        contentAsJson(result).as[List[GetControlledArrivalsGmrReducedResponse]] shouldBe arrivalsGmrResponse.map(
          GetControlledArrivalsGmrReducedResponse.apply)

        verify(mockPortService).getArrivals(mEq("clientId"), mEq(portId), mEq(true))(any())
      }
    }

    "get arrivals gmr is unsuccessful" should {
      "return 404 if the service returns PortNotFoundError" in new Setup {
        List(
          PortErrors.subscriptionPortIdNotFoundError -> ErrorResponse(
            403,
            "PORT_ID_NOT_FOUND",
            "No valid port in your subscription fields, please contact SDST."),
          PortErrors.portIdMismatchError -> ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you.")
        ).foreach {
          case (error, response) =>
            when(mockPortService.getArrivals(mEq("clientId"), mEq(portId), mEq(false))(any()))
              .thenReturn(EitherT[Future, GetControlledArrivalErrors, List[GetControlledArrivalsGmrReducedResponse]](
                Future(error.asLeft[List[GetControlledArrivalsGmrReducedResponse]])))

            val result = controller.getControlledGmrsForArrivals(portId)(fakeRequest)

            status(result) shouldBe response.httpStatusCode
            assertErrorResponse(result, response.errorCode, response.message, nestedErrorCount = 0)
        }

        verify(mockPortService, times(2)).getArrivals(mEq("clientId"), mEq(portId), mEq(false))(any())
      }
    }
  }

  "getControlledDeparturesGmr" when {
    "get departures gmr is successful" should {
      "return 200 OK" in new Setup {

        when(mockPortService.getControlledDepartures(mEq("clientId"), mEq(portId), mEq(false))(any()))
          .thenReturn(
            EitherT.rightT[Future, GetControlledDepartureErrors](departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)))

        val result = controller.getControlledGmrsForDepartures(portId)(fakeRequest)

        status(result) shouldBe OK
        contentAsJson(result).as[List[GetControlledDeparturesGmrReducedResponse]] shouldBe departuresGmrResponse.map(
          GetControlledDeparturesGmrReducedResponse.apply)

        verify(mockPortService).getControlledDepartures(mEq("clientId"), mEq(portId), mEq(false))(any())
      }
      "return 200 OK with ignore_effective_dates" in new Setup {

        when(mockPortService.getControlledDepartures(mEq("clientId"), mEq(portId), mEq(true))(any()))
          .thenReturn(
            EitherT.rightT[Future, GetControlledDepartureErrors](departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)))

        val result =
          controller.getControlledGmrsForDepartures(portId)(fakeRequest.withHeaders(fakeRequest.headers.add("IGNORE-Effective-DatES" -> "true")))

        status(result) shouldBe OK
        contentAsJson(result).as[List[GetControlledDeparturesGmrReducedResponse]] shouldBe departuresGmrResponse.map(
          GetControlledDeparturesGmrReducedResponse.apply)

        verify(mockPortService).getControlledDepartures(mEq("clientId"), mEq(portId), mEq(true))(any())
      }
    }

    "get departures gmr is unsuccessful" should {
      "return 404 if the service returns PortNotFoundError" in new Setup {
        List(
          PortErrors.subscriptionPortIdNotFoundError -> ErrorResponse(
            403,
            "PORT_ID_NOT_FOUND",
            "No valid port in your subscription fields, please contact SDST."),
          PortErrors.portIdMismatchError -> ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you.")
        ).foreach {
          case (error, response) =>
            when(mockPortService.getControlledDepartures(mEq("clientId"), mEq(portId), mEq(false))(any()))
              .thenReturn(EitherT[Future, GetControlledDepartureErrors, List[GetControlledDeparturesGmrReducedResponse]](
                Future(error.asLeft[List[GetControlledDeparturesGmrReducedResponse]])))

            val result = controller.getControlledGmrsForDepartures(portId)(fakeRequest)

            status(result) shouldBe response.httpStatusCode
            assertErrorResponse(result, response.errorCode, response.message, nestedErrorCount = 0)
        }

        verify(mockPortService, times(2)).getControlledDepartures(mEq("clientId"), mEq(portId), mEq(false))(any())
      }
    }
  }

  "getDeparturesGmr" when {
    "get departures gmr is successful" should {
      "return 200 OK" in new Setup {
        val updatedDateTime = Instant.parse("2021-07-03T16:33:51.000z")
        when(mockPortService.getDepartures(mEq("clientId"), mEq(portId), mEq(false), mEq(None), mEq(None))(any()))
          .thenReturn(EitherT.rightT[Future, GetDepartureErrors](departuresExtendedGmrResponse))

        val result = controller.getGmrsForDepartures(portId, None, None)(fakeRequest)

        status(result) shouldBe OK
        val actual = contentAsJson(result).as[List[GetPortDepartureExpandedGmrResponse]]
        actual shouldBe departuresExtendedGmrResponse
        actual.map(x => (Json.toJson(x) \\ "updatedDateTime")(0) shouldBe JsString("2021-07-03T16:33:51.000Z"))

        verify(mockPortService).getDepartures(mEq("clientId"), mEq(portId), mEq(false), mEq(None), mEq(None))(any())
      }

      "return 200 OK with ignore_effective_dates" in new Setup {

        when(mockPortService.getDepartures(mEq("clientId"), mEq(portId), mEq(true), mEq(None), mEq(None))(any()))
          .thenReturn(EitherT.rightT[Future, GetDepartureErrors](departuresExtendedGmrResponse))

        val result =
          controller.getGmrsForDepartures(portId, None, None)(fakeRequest.withHeaders(fakeRequest.headers.add("IGNORE-Effective-DatES" -> "true")))

        status(result) shouldBe OK
        val actual = contentAsJson(result).as[List[GetPortDepartureExpandedGmrResponse]]
        actual shouldBe departuresExtendedGmrResponse
        actual.map(x => (Json.toJson(x) \\ "updatedDateTime")(0) shouldBe JsString("2021-07-03T16:33:51.000Z"))
        verify(mockPortService).getDepartures(mEq("clientId"), mEq(portId), mEq(true), mEq(None), mEq(None))(any())
      }
    }

    "get departures gmr is unsuccessful" should {
      "return derived errors" in new Setup {
        List[(GetDepartureErrors, ErrorResponse)](
          PortErrors.subscriptionPortIdNotFoundError -> ErrorResponse(
            403,
            "PORT_ID_NOT_FOUND",
            "No valid port in your subscription fields, please contact SDST."),
          PortErrors.portIdMismatchError -> ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you."),
          PortErrors.tooManyGmrsError -> ErrorResponse(
            400,
            "TOO_MANY_RESULTS",
            "Too many goods movement records were found. Please filter down your query."),
          PortErrors.invalidDateCombinationError -> ErrorResponse(400, "INVALID_DATE_COMBINATION", "lastUpdatedFrom should be before lastUpdatedTo.")
        ).foreach {
          case (error, response) =>
            when(mockPortService.getDepartures(mEq("clientId"), mEq(portId), mEq(false), mEq(None), mEq(None))(any()))
              .thenReturn(EitherT[Future, GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]](
                Future(error.asLeft[List[GetPortDepartureExpandedGmrResponse]])))

            val result = controller.getGmrsForDepartures(portId, None, None)(fakeRequest)

            status(result) shouldBe response.httpStatusCode
            assertErrorResponse(result, response.errorCode, response.message, nestedErrorCount = 0)
        }

        verify(mockPortService, times(4)).getDepartures(mEq("clientId"), mEq(portId), mEq(false), mEq(None), mEq(None))(any())
      }
    }
  }

}
