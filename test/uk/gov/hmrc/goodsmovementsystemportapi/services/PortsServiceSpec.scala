/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.services

import cats.data.OptionT
import cats.implicits._
import org.mockito.Mockito._
import org.scalatest.EitherValues
import play.api.test.Helpers.await
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.GoodsMovementSystemConnector
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.PortErrors.{PortIdMismatchError, SubscriptionPortIdNotFoundError}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrReducedResponse, GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrReducedResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}

import java.time.Instant
import scala.concurrent.Future

class PortsServiceSpec extends BaseSpec with EitherValues {

  abstract class Setup(showPending: Boolean) {
    val mockGoodsMovementSystemConnector: GoodsMovementSystemConnector = mock[GoodsMovementSystemConnector]
    val service =
      new PortsService(mockGoodsMovementSystemConnector, mockApiSubscriptionFieldsService, mockGmsReferenceDataService, mockAppConfig, showPending)
    val record = gmsReferenceDataSummay
  }

  "getDepartures" when {
    "getDepartures is successful" should {
      "return the gmr" in new Setup(true) {
        val portId = "1"

        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getDeparturesGmr(portId, None, None)(hc))
          .thenReturn(Future(Right(departuresExtendedGmrResponse)))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future](portId))

        val result = await(service.getDepartures("clientId", portId, false, None, None).value)

        result.right.value shouldBe departuresExtendedGmrResponse

        verify(mockGoodsMovementSystemConnector).getDeparturesGmr(portId, None, None)(hc)
      }

      "return all gmr" in new Setup(true) {
        val multiple = GetPortDepartureExpandedGmrResponse(
          gmrId                   = "gmrId",
          isUnaccompanied         = true,
          updatedDateTime         = Instant.now,
          inspectionRequired      = None,
          reportToLocations       = None,
          vehicleRegNum           = None,
          trailerRegistrationNums = Some(List("ABC")),
          containerReferenceNums  = None,
          checkedInCrossing       = crossing,
          readyToEmbark           = None
        ) :: GetPortDepartureExpandedGmrResponse(
          gmrId                   = "gmrId",
          isUnaccompanied         = true,
          updatedDateTime         = Instant.now,
          inspectionRequired      = Some(true),
          reportToLocations       = None,
          vehicleRegNum           = None,
          trailerRegistrationNums = Some(List("ABC")),
          containerReferenceNums  = None,
          checkedInCrossing       = crossing,
          readyToEmbark           = None
        ) :: departuresExtendedGmrResponse

        val portId = "1"
        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getDeparturesGmr(portId, None, None)(hc))
          .thenReturn(Future(Right(multiple)))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future](portId))

        val result = await(service.getDepartures("clientId", portId, false, None, None).value)

        result.right.value shouldBe multiple

        verify(mockGoodsMovementSystemConnector).getDeparturesGmr(portId, None, None)(hc)
      }

      "return the gmr if portid has spaces" in new Setup(true) {
        val portId       = "2 5"
        val portIdTrimed = portId.replaceAll("\\s", "")

        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future(record))
        when(mockGoodsMovementSystemConnector.getDeparturesGmr(portIdTrimed, None, None)(hc))
          .thenReturn(Future(Right(departuresExtendedGmrResponse)))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future](portId))

        val result = await(service.getDepartures("clientId", portId, false, None, None).value)

        result.right.value shouldBe departuresExtendedGmrResponse

        verify(mockGoodsMovementSystemConnector).getDeparturesGmr(portIdTrimed, None, None)(hc)
      }
    }

    "getDepartures is unSuccessful" should {
      "return 403 (Forbidden) PortIdMismatchError" in new Setup(true) {
        val portId = "1"

        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getDeparturesGmr(portId, None, None)(hc))
          .thenReturn(Future(Right(departuresExtendedGmrResponse)))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future]("xyz"))

        val result = await(service.getDepartures("clientId", portId, false, None, None).value)

        result.left.value shouldBe PortIdMismatchError
      }

      "return SubscriptionPortIdNotFoundError" in new Setup(true) {
        val portId = "1"

        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getDeparturesGmr(portId, None, None)(hc))
          .thenReturn(Future(Right(departuresExtendedGmrResponse)))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.none[Future, String])

        val result = await(service.getDepartures("clientId", portId, false, None, None).value)

        result.left.value shouldBe SubscriptionPortIdNotFoundError
      }
    }
  }

  "with showing pending" when {
    val showPending = true
    "getArrivalsControlledGmrs" when {
      "getArrivals is successful" should {
        "return the gmr" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)
          result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
        }

        "return all gmr" in new Setup(showPending) {
          val withPending = GetControlledArrivalsGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = None,
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            actualCrossing          = crossing
          ) :: GetControlledArrivalsGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = Some(true),
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            actualCrossing          = crossing
          ) :: arrivalsGmrResponse

          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))

          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(withPending))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.right.value shouldBe withPending.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
        }

        "return the gmr if portid has spaces" in new Setup(showPending) {
          val portId       = " 2 5 "
          val portIdTrimed = portId.replaceAll("\\s", "")

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portIdTrimed)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portIdTrimed)(hc)
        }
      }

      "getArrivals is unsuccessful" should {
        "return 403 (Forbidden) PortIdMismatchError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future]("xyz"))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.left.value shouldBe PortIdMismatchError

        }

        "return SubscriptionPortIdNotFoundError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.none[Future, String])

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.left.value shouldBe SubscriptionPortIdNotFoundError
        }
      }

      "return the gmr if ignoreEffectiveDateHeader is false & effectiveDatesCheck feature flag true" in new Setup(showPending) {
        val portId = "1"
        when(mockAppConfig.effectiveDatesCheck).thenReturn(true)
        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
          .thenReturn(Future(arrivalsGmrResponse))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future](portId))

        val result = await(service.getArrivals("clientId", portId, false).value)
        result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

        verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
      }

      "return the gmr if ignoreEffectiveDateHeader is true & effectiveDatesCheck feature flag true" in new Setup(showPending) {
        val portId = "2"
        when(mockAppConfig.effectiveDatesCheck).thenReturn(true)
        when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
        when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
          .thenReturn(Future(arrivalsGmrResponse))

        when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
          .thenReturn(OptionT.some[Future](portId))

        val result = await(service.getArrivals("clientId", portId, true).value)
        result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

        verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
      }
    }

    "getDeparturesControlledGmrs" when {
      "getDepartures is successful" should {
        "return the gmr" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portId)(hc)
        }

        "return all gmr" in new Setup(showPending) {
          val withPending = GetControlledDeparturesGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = None,
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            checkedInCrossing       = crossing
          ) :: GetControlledDeparturesGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = Some(true),
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            checkedInCrossing       = crossing
          ) :: departuresGmrResponse

          val portId = "1"
          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(withPending))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe withPending.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portId)(hc)
        }

        "return the gmr if portid has spaces" in new Setup(showPending) {
          val portId       = "2 5"
          val portIdTrimed = portId.replaceAll("\\s", "")

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portIdTrimed)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portIdTrimed)(hc)
        }
      }

      "getDepartures is unSuccessful" should {
        "return 403 (Forbidden) PortIdMismatchError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future]("xyz"))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.left.value shouldBe PortIdMismatchError
        }

        "return SubscriptionPortIdNotFoundError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.none[Future, String])

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.left.value shouldBe SubscriptionPortIdNotFoundError
        }
      }
    }

  }

  "without showing pending" when {
    val showPending = false
    "getArrivalsControlledGmrs" when {
      "getArrivals is successful" should {
        "return the gmr" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
        }

        "return only controlled gmrs" in new Setup(showPending) {
          val withPending = GetControlledArrivalsGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = None,
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            actualCrossing          = crossing
          ) :: arrivalsGmrResponse

          val portId = "1"
          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(withPending))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portId)(hc)
        }

        "return the gmr if portid has spaces" in new Setup(showPending) {
          val portId       = " 1 "
          val portIdTrimed = portId.replaceAll("\\s", "")

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portIdTrimed)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.right.value shouldBe arrivalsGmrResponse.map(GetControlledArrivalsGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledArrivalsGmr(portIdTrimed)(hc)
        }
      }

      "getArrivals is unsuccessful" should {
        "return 403 (Forbidden) PortIdMismatchError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future]("xyz"))

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.left.value shouldBe PortIdMismatchError

        }

        "return SubscriptionPortIdNotFoundError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledArrivalsGmr(portId)(hc))
            .thenReturn(Future(arrivalsGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.none[Future, String])

          val result = await(service.getArrivals("clientId", portId, false).value)

          result.left.value shouldBe SubscriptionPortIdNotFoundError
        }

      }
    }

    "getDeparturesControlledGmrs" when {
      "getControlledDepartures is successful" should {
        "return the gmr" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portId)(hc)
        }

        "return only controlled gmrs" in new Setup(showPending) {
          val withPending = GetControlledDeparturesGmrResponse(
            gmrId                   = "gmrId",
            isUnaccompanied         = true,
            inspectionRequired      = None,
            reportToLocations       = None,
            vehicleRegNum           = None,
            trailerRegistrationNums = Some(List("ABC")),
            containerReferenceNums  = None,
            checkedInCrossing       = crossing
          ) :: departuresGmrResponse

          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(withPending))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portId)(hc)
        }

        "return the gmr if portid has spaces" in new Setup(showPending) {
          val portId       = "2 5"
          val portIdTrimed = portId.replaceAll("\\s", "")

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portIdTrimed)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future](portId))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.right.value shouldBe departuresGmrResponse.map(GetControlledDeparturesGmrReducedResponse.apply)

          verify(mockGoodsMovementSystemConnector).getControlledDeparturesGmr(portIdTrimed)(hc)
        }
      }

      "getControlledDepartures is unSuccessful" should {
        "return 403 (Forbidden) PortIdMismatchError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.some[Future]("xyz"))

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.left.value shouldBe PortIdMismatchError
        }

        "return SubscriptionPortIdNotFoundError" in new Setup(showPending) {
          val portId = "1"

          when(mockGmsReferenceDataService.getReferenceData).thenReturn(Future.successful(record))
          when(mockGoodsMovementSystemConnector.getControlledDeparturesGmr(portId)(hc))
            .thenReturn(Future(departuresGmrResponse))

          when(mockApiSubscriptionFieldsService.getField("clientId", SubscriptionFieldsResponse.portIdKey))
            .thenReturn(OptionT.none[Future, String])

          val result = await(service.getControlledDepartures("clientId", portId, false).value)

          result.left.value shouldBe SubscriptionPortIdNotFoundError
        }

      }
    }
  }
}
