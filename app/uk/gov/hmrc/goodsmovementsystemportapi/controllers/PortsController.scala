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

import cats.implicits.catsStdInstancesForFuture
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.GmsActionBuilders
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.CustomHeaders
import uk.gov.hmrc.goodsmovementsystemportapi.services.PortsService

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class PortsController @Inject()(portService: PortsService, gmsActionBuilders: GmsActionBuilders, cc: ControllerComponents)(
  implicit ec:                               ExecutionContext)
    extends BaseGmrController(cc) {

  import gmsActionBuilders._

  def getControlledGmrsForArrivals(portId: String): Action[AnyContent] = validateAuthAndApiVersionTransformer.async { implicit request =>
    val ignoreEffectiveDateHeader = request.headers.get(CustomHeaders.IGNORE_EFFECTIVE_DATES).fold(false)(_.toBoolean)
    portService
      .getArrivals(request.clientId, portId, ignoreEffectiveDateHeader)
      .fold(
        ErrorResponse.handleGetControlledArrivalError,
        gmrs => Ok(Json.toJson(gmrs))
      )
  }

  def getControlledGmrsForDepartures(portId: String): Action[AnyContent] = validateAuthAndApiVersionTransformer.async { implicit request =>
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
      val ignoreEffectiveDateHeader = request.headers.get(CustomHeaders.IGNORE_EFFECTIVE_DATES).fold(false)(_.toBoolean)
      portService
        .getDepartures(request.clientId, portId, ignoreEffectiveDateHeader, lastUpdatedFrom, lastUpdatedTo)
        .fold(
          ErrorResponse.handleGetDepartureError,
          gmrs => Ok(Json.toJson(gmrs))
        )
    }

}
