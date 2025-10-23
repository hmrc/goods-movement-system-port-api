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

package uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers

import play.api.Logger
import play.api.http.Status.*
import play.api.libs.json.Json.toJson
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.Status
import uk.gov.hmrc.goodsmovementsystemportapi.models.errorhandlers.NestedError
import uk.gov.hmrc.goodsmovementsystemportapi.utils.LogUtils.logAndResult

class ErrorResponse(
  val httpStatusCode: Int,
  val errorCode:      String,
  val message:        String,
  val errors:         Option[List[NestedError]]
) {
  def toResult: Result =
    Status(httpStatusCode)(toJson(this)(ErrorResponse.writes))
}

object ErrorResponse {

  case object InvalidAcceptHeader
      extends ErrorResponse(
        httpStatusCode = 406,
        errorCode = "ACCEPT_HEADER_INVALID",
        message = "The accept header is missing or invalid",
        errors = None
      )

  case class InvalidRequestBody(nestedErrors: List[NestedError])
      extends ErrorResponse(
        httpStatusCode = 400,
        errorCode = "JSON_SCHEMA_ERROR",
        message = "The JSON payload is not conformant with the JSON schema",
        errors = Some(nestedErrors)
      )

  implicit val writes: Writes[ErrorResponse] = (o: ErrorResponse) => {
    val j = Json.obj("code" -> o.errorCode, "message" -> o.message)
    o.errors.fold(j)(errors => j ++ Json.obj("errors" -> errors))
  }

  def apply(status: Int, code: String, message: String) =
    new ErrorResponse(status, code, message, None)

  def handleGetDepartureError(implicit logger: Logger): PartialFunction[GetDepartureErrors, Result] = {
    case PortErrors.tooManyGmrsError =>
      logAndResult(
        ErrorResponse(400, "TOO_MANY_RESULTS", "Too many goods movement records were found. Please filter down your query."),
        includeMessage = true
      )
    case PortErrors.invalidDateCombinationError =>
      logAndResult(ErrorResponse(400, "INVALID_DATE_COMBINATION", "lastUpdatedFrom should be before lastUpdatedTo."), includeMessage = true)
    case PortErrors.subscriptionPortIdNotFoundError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "No valid port in your subscription fields, please contact SDST."), includeMessage = true)
    case PortErrors.portIdMismatchError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you."), includeMessage = true)
  }

  def handleGetControlledArrivalError(implicit logger: Logger): PartialFunction[GetControlledArrivalErrors, Result] = {
    case PortErrors.subscriptionPortIdNotFoundError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "No valid port in your subscription fields, please contact SDST."), includeMessage = true)
    case PortErrors.portIdMismatchError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you."), includeMessage = true)
  }

  def handleGetControlledDepartureError(implicit logger: Logger): PartialFunction[GetControlledDepartureErrors, Result] = {
    case PortErrors.subscriptionPortIdNotFoundError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "No valid port in your subscription fields, please contact SDST."), includeMessage = true)
    case PortErrors.portIdMismatchError =>
      logAndResult(ErrorResponse(403, "PORT_ID_NOT_FOUND", "The port supplied was not a valid port for you."), includeMessage = true)
  }

  case object Internal_Server_Error
      extends ErrorResponse(
        httpStatusCode = INTERNAL_SERVER_ERROR,
        errorCode = "INTERNAL_SERVER_ERROR",
        message = "Something went wrong, try again later.",
        errors = None
      )

  case object MissingCorrelationIdErrorResponse
      extends ErrorResponse(
        httpStatusCode = INTERNAL_SERVER_ERROR,
        errorCode = "INTERNAL_SERVER_ERROR",
        message = "Something went wrong, try again later.",
        errors = None
      )

  case object Gateway_Timeout_Exception
      extends ErrorResponse(
        httpStatusCode = GATEWAY_TIMEOUT,
        errorCode = "GATEWAY_TIMEOUT",
        message = "Gateway timeout, try again later.",
        errors = None
      )

}
