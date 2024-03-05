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

package uk.gov.hmrc.goodsmovementsystemportapi.services

import cats.Apply
import cats.data.EitherT
import cats.implicits._
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.GoodsMovementSystemConnector
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.{GetControlledArrivalErrors, GetControlledDepartureErrors, GetDepartureErrors, PortErrors, SubscriptionValidationErrors}
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.PortErrors.{InvalidDateCombinationError, portIdMismatchError}
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrReducedResponse, GetControlledDeparturesGmrReducedResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.Port
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{Instant, LocalDate}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PortsService @Inject() (
  goodsMovementSystemConnector:          GoodsMovementSystemConnector,
  apiSubscriptionFieldsService:          ApiSubscriptionFieldsService,
  gmsReferenceDataService:               GmsReferenceDataService,
  appConfig:                             AppConfig,
  @Named("showPendingGmrs") showPending: Boolean
)(implicit executionContext: ExecutionContext) {

  def getArrivals(clientId: String, portId: String, ignoreEffectiveDateHeader: Boolean)(implicit
    hc: HeaderCarrier
  ): EitherT[Future, GetControlledArrivalErrors, List[GetControlledArrivalsGmrReducedResponse]] =
    for {
      portIdVal <- validate(clientId, portId, ignoreEffectiveDateHeader)
      response  <- EitherT.liftF(goodsMovementSystemConnector.getControlledArrivalsGmr(portIdVal))
    } yield
      if (showPending) {
        response.map(GetControlledArrivalsGmrReducedResponse.apply)
      } else {
        response.filter(_.inspectionRequired.exists(identity)).map(GetControlledArrivalsGmrReducedResponse.apply)
      }

  def getControlledDepartures(clientId: String, portId: String, ignoreEffectiveDateHeader: Boolean)(implicit
    hc: HeaderCarrier
  ): EitherT[Future, GetControlledDepartureErrors, List[GetControlledDeparturesGmrReducedResponse]] =
    for {
      portIdVal <- validate(clientId, portId, ignoreEffectiveDateHeader)
      response  <- EitherT.liftF(goodsMovementSystemConnector.getControlledDeparturesGmr(portIdVal))
    } yield
      if (showPending) {
        response.map(GetControlledDeparturesGmrReducedResponse.apply)
      } else {
        response.filter(_.inspectionRequired.exists(identity)).map(GetControlledDeparturesGmrReducedResponse.apply)
      }

  def getDepartures(
    clientId:                  String,
    portId:                    String,
    ignoreEffectiveDateHeader: Boolean,
    lastUpdatedFrom:           Option[Instant],
    lastUpdatedTo:             Option[Instant]
  )(implicit hc: HeaderCarrier): EitherT[Future, GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]] = {
    val isValidDateCombination: Boolean = Apply[Option]
      .map2(lastUpdatedFrom, lastUpdatedTo) { case (from, to) =>
        from.isBefore(to)
      }
      .getOrElse(true)

    for {
      portIdVal <- validate(clientId, portId, ignoreEffectiveDateHeader)
      _         <- EitherT.cond[Future](isValidDateCombination, lastUpdatedFrom -> lastUpdatedTo, InvalidDateCombinationError)
      response  <- EitherT(goodsMovementSystemConnector.getDeparturesGmr(portIdVal, lastUpdatedFrom, lastUpdatedTo))
    } yield response
  }

  private def validate(clientId: String, portId: String, ignoreEffectiveDateHeader: Boolean)(implicit
    hc: HeaderCarrier
  ): EitherT[Future, SubscriptionValidationErrors, String] = {
    val portIdVal = portId.replaceAll("\\s", "")

    for {
      authorisedPortIds <- apiSubscriptionFieldsService
                             .getField(clientId, SubscriptionFieldsResponse.portIdKey)
                             .toRight(PortErrors.subscriptionPortIdNotFoundError)
      _ <- EitherT.cond[Future](
             authorisedPortIds.split(",").map(_.replaceAll("\\s", "")).contains(portIdVal) && portIdVal.length > 0,
             (),
             PortErrors.portIdMismatchError
           )
      authorisedPort <- EitherT.fromOptionF(
                          gmsReferenceDataService.getReferenceData.map(_.ports.find(port => port.portId === portIdVal.toInt)),
                          PortErrors.portIdMismatchError
                        )
      _ <- EitherT.fromEither[Future](validateRoute(authorisedPort, ignoreEffectiveDateHeader))
    } yield portIdVal
  }

  private def validateRoute(authorisedPort: Port, ignoreEffectiveDateHeader: Boolean): Either[SubscriptionValidationErrors, Port] =
    for {
      _ <- if (isEffectiveDateCheckRequired(ignoreEffectiveDateHeader)) {
             Either.cond(
               authorisedPort.portEffectiveTo.forall(LocalDate.now().isBefore) && LocalDate.now().isAfter(authorisedPort.portEffectiveFrom),
               authorisedPort,
               portIdMismatchError
             )
           } else {
             authorisedPort.asRight[SubscriptionValidationErrors]
           }

    } yield authorisedPort

  private def isEffectiveDateCheckRequired(ignoreEffectiveDateHeader: Boolean): Boolean =
    if (appConfig.effectiveDatesCheck) {
      !ignoreEffectiveDateHeader
    } else {
      true
    }
}
