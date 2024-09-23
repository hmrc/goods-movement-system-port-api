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

import cats.implicits.catsSyntaxEq
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import play.api.libs.json.Json.toJson
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.{Configuration, Logger}
import uk.gov.hmrc.auth.core.AuthorisationException
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.ErrorResponse.*
import uk.gov.hmrc.goodsmovementsystemportapi.utils.LogUtils.logAndResult
import uk.gov.hmrc.http.*
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.backend.http.JsonErrorHandler
import uk.gov.hmrc.play.bootstrap.config.DefaultHttpAuditEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomErrorHandler @Inject() (
  auditConnector:        AuditConnector,
  defaultHttpAuditEvent: DefaultHttpAuditEvent,
  configuration:         Configuration
)(implicit executionContext: ExecutionContext)
    extends JsonErrorHandler(auditConnector, defaultHttpAuditEvent, configuration)
    with Results {

  import defaultHttpAuditEvent.dataEvent
  implicit val logger: Logger = Logger(this.getClass.getName)

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future {
      implicit val headerCarrier: HeaderCarrier = hc(request)
      statusCode match {
        case NOT_FOUND =>
          auditConnector.sendEvent(
            dataEvent(
              eventType = "ResourceNotFound",
              transactionName = "Resource Endpoint Not Found",
              request = request,
              detail = Map.empty[String, String]
            )
          )
          NotFound(toJson(ErrorResponse(NOT_FOUND, "URI not found", request.path)))
        case BAD_REQUEST =>
          auditConnector.sendEvent(
            dataEvent(
              eventType = "ServerValidationError",
              transactionName = "Request bad format exception",
              request = request,
              detail = Map.empty[String, String]
            )
          )
          if (request.method === "GET")
            BadRequest(toJson(ErrorResponse(BAD_REQUEST, "INVALID_QUERY_PARAMS", message)))
          else
            BadRequest(toJson(ErrorResponse(BAD_REQUEST, "INVALID_JSON", "Invalid JSON - the payload could not be parsed")))
        case _ =>
          auditConnector.sendEvent(
            dataEvent(
              eventType = "ClientError",
              transactionName = s"A client error occurred, status: ${statusCode.toString}",
              request = request,
              detail = Map.empty[String, String]
            )
          )
          Status(statusCode)(toJson(ErrorResponse(statusCode, "OTHER_ERROR", message)))
      }
    }

  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    implicit val headerCarrier: HeaderCarrier = hc(request)

    val message = s"! Internal server error, for (${request.method}) [${request.uri}] -> "
    val eventType = ex match {
      case _: NotFoundException       => "ResourceNotFound"
      case _: AuthorisationException  => "ClientError"
      case _: JsValidationException   => "ServerValidationError"
      case _: GatewayTimeoutException => "GatewayTimeoutException"
      case _ => "ServerInternalError"
    }

    // TODO reconfirm all these errors.
    val errorResponse = ex match {
      case e: GatewayTimeoutException =>
        logger.warn(message, e)
        Gateway_Timeout_Exception
      case e: AuthorisationException =>
        logger.warn(message, e)
        Internal_Server_Error
      case e: HttpException =>
        logException(e, e.responseCode)
        Internal_Server_Error
      case e: Exception with UpstreamErrorResponse =>
        logException(e, e.statusCode)
        ErrorResponse(e.reportAs, "INTERNAL_SERVER_ERROR", "Something went wrong, try again later.")
      case e: Throwable =>
        logger.error(message, e)
        Internal_Server_Error
    }

    auditConnector.sendEvent(
      dataEvent(
        eventType = eventType,
        transactionName = "Unexpected error",
        request = request,
        detail = Map("transactionFailureReason" -> ex.getMessage)
      )
    )

    Future(logAndResult(errorResponse, includeMessage = false))
  }

  private def logException(exception: Exception, responseCode: Int): Unit =
    if (upstreamWarnStatuses contains responseCode)
      logger.warn(exception.getMessage, exception)
    else
      logger.error(exception.getMessage, exception)

}
