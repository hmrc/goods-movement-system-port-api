/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import cats.implicits.catsStdInstancesForFuture

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.GmsActionBuilders
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.CustomHeaders
import uk.gov.hmrc.goodsmovementsystemportapi.services.PortsService

import java.time.Instant
import scala.concurrent.ExecutionContext

@Singleton()
class PortsController @Inject()(portService: PortsService, gmsActionBuilders: GmsActionBuilders, cc: ControllerComponents)(
  implicit ec:                               ExecutionContext)
    extends BaseGmrController(cc) {

  import gmsActionBuilders._

  def getControlledGmrsForArrivals(portId: String): Action[AnyContent] = validateAuthAndApiVersionTransformer.async { implicit request =>
    logger.info(s"Received request for controlled GMR arrivals for port $portId")
    val ignoreEffectiveDateHeader = request.headers.get(CustomHeaders.IGNORE_EFFECTIVE_DATES).fold(false)(_.toBoolean)
    portService
      .getArrivals(request.clientId, portId, ignoreEffectiveDateHeader)
      .fold(
        ErrorResponse.handleGetControlledArrivalError,
        gmrs => Ok(Json.toJson(gmrs))
      )
  }

  def getControlledGmrsForDepartures(portId: String): Action[AnyContent] = validateAuthAndApiVersionTransformer.async { implicit request =>
    logger.info(s"Received request for controlled GMR departures for port $portId")
    val ignoreEffectiveDateHeader = request.headers.get(CustomHeaders.IGNORE_EFFECTIVE_DATES).fold(false)(_.toBoolean)
    portService
      .getControlledDepartures(request.clientId, portId, ignoreEffectiveDateHeader)
      .fold(
        ErrorResponse.handleGetControlledDepartureError,
        gmrs => Ok(Json.toJson(gmrs))
      )
  }

  def getGmrsForDepartures(portId: String, lastUpdatedFrom: Option[Instant], lastUpdatedTo: Option[Instant]): Action[AnyContent] =
    validateAuthAndApiVersionTransformer.async { implicit request =>
      logger.info(s"Received request for GMR departures for port $portId")
      val ignoreEffectiveDateHeader = request.headers.get(CustomHeaders.IGNORE_EFFECTIVE_DATES).fold(false)(_.toBoolean)
      portService
        .getDepartures(request.clientId, portId, ignoreEffectiveDateHeader, lastUpdatedFrom, lastUpdatedTo)
        .fold(
          ErrorResponse.handleGetDepartureError,
          gmrs => Ok(Json.toJson(gmrs))
        )
    }

}
