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

import org.mockito.Mockito.when
import play.api.Configuration
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ControllerBaseSpec
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.bootstrap.config.DefaultHttpAuditEvent

class CustomErrorHandlerSpec extends ControllerBaseSpec {

  trait Setup {
    val mockConfig = {
      val c = mock[Configuration]
      when(c.get[Seq[Int]]("bootstrap.errorHandler.warnOnly.statusCodes")).thenReturn(Seq(400))
      c
    }
    val customHttpErrorHandler = new CustomErrorHandler(mockAuditConnector, mock[DefaultHttpAuditEvent], mockConfig)
  }

  "when a handling a server side error" which {
    "is an authorisation exceptions" should {
      "return 401 UNAUTHORIZED" in new Setup {
        List[AuthorisationException](
          InsufficientConfidenceLevel(),
          InsufficientEnrolments(),
          UnsupportedAffinityGroup(),
          UnsupportedCredentialRole(),
          UnsupportedAuthProvider(),
          IncorrectCredentialStrength(),
          BearerTokenExpired(),
          MissingBearerToken(),
          InvalidBearerToken(),
          SessionRecordNotFound(),
          InternalError(),
          FailedRelationship()
        ).foreach { exception =>
          val result = customHttpErrorHandler
            .onServerError(FakeRequest(), exception)

          status(result) shouldBe INTERNAL_SERVER_ERROR

          assertErrorResponse(result, "INTERNAL_SERVER_ERROR", "Something went wrong, try again later.", 0)
        }
      }
    }

    "is a HTTP Exception" should {
      "return the status code of the exception with the message" in new Setup {
        List(
          new HttpException("HTTP Exception", 411),
          new BadRequestException("bad request"),
          new UnauthorizedException("unauthorised"),
          new PaymentRequiredException("payment required"),
          new ForbiddenException("forbidden"),
          new NotFoundException("not found"),
          new MethodNotAllowedException("method not allowed"),
          new NotAcceptableException("not acceptable"),
          new ProxyAuthenticationRequiredException("proxy authentication required"),
          new RequestTimeoutException("request timeout"),
          new ConflictException("conflict"),
          new GoneException("gone"),
          new LengthRequiredException("legnth required"),
          new PreconditionFailedException("precondition failed"),
          new RequestEntityTooLargeException("request entity too large"),
          new RequestUriTooLongException("request uri too long"),
          new UnsupportedMediaTypeException("unsupported media type"),
          new RequestRangeNotSatisfiableException("request range not satisfied"),
          new ExpectationFailedException("expectation failed"),
          new UnprocessableEntityException("unprocessable entity"),
          new LockedException("locked"),
          new FailedDependencyException("failed dependency"),
          new TooManyRequestException("too many requests"),
          new InternalServerException("internal server"),
          new NotImplementedException("not implemented"),
          new BadGatewayException("bad gateway"),
          new ServiceUnavailableException("service unavailable"),
          new HttpVersionNotSupportedException("http version not supported"),
          new InsufficientStorageException("insufficient storage")
        ).foreach { exception =>
          val result = customHttpErrorHandler
            .onServerError(FakeRequest(), exception)

          status(result) shouldBe INTERNAL_SERVER_ERROR

          assertErrorResponse(result, "INTERNAL_SERVER_ERROR", "Something went wrong, try again later.", 0)
        }
      }
      "return the GatewayTimeout status code of the exception with the message" in new Setup {
        val exception = new GatewayTimeoutException("gateway timeout")
        val result = customHttpErrorHandler
          .onServerError(FakeRequest(), exception)

        status(result) shouldBe GATEWAY_TIMEOUT

        assertErrorResponse(result, "GATEWAY_TIMEOUT", "Gateway timeout, try again later.", 0)
      }
    }

    "is an upstream exception" should {
      "return the upstream response code" in new Setup {
        List(
          UpstreamErrorResponse("upstream 4xx", 410, 500) -> INTERNAL_SERVER_ERROR,
          UpstreamErrorResponse("upstream 5xx", 500, 502) -> BAD_GATEWAY
        ).foreach {
          case (exception, expected) =>
            val result = customHttpErrorHandler
              .onServerError(FakeRequest(), exception)

            status(result) shouldBe expected

            assertErrorResponse(result, "INTERNAL_SERVER_ERROR", "Something went wrong, try again later.", 0)
        }
      }
    }

  }

  "OnClientError" which {
    "if the json is invalid" should {
      "return bad request exception" in new Setup {

        val result = customHttpErrorHandler.onClientError(FakeRequest("POST", "/"), BAD_REQUEST)

        status(result) shouldBe BAD_REQUEST
        assertErrorResponse(result, "INVALID_JSON", "Invalid JSON - the payload could not be parsed", 0)
      }
    }

    "if the query parameter is invalid" should {
      "return bad request exception" in new Setup {

        val result = customHttpErrorHandler.onClientError(FakeRequest("GET", "/abc?key=value"), BAD_REQUEST)

        status(result)                              shouldBe BAD_REQUEST
        (contentAsJson(result) \ "code").as[String] shouldBe ("INVALID_QUERY_PARAMS")
      }
    }

    "is any other exception" should {
      "report full exception for other 4xx responses" in new Setup {
        List(
          UNAUTHORIZED,
          PAYMENT_REQUIRED,
          FORBIDDEN,
          NOT_FOUND,
          METHOD_NOT_ALLOWED,
          NOT_ACCEPTABLE,
          PROXY_AUTHENTICATION_REQUIRED,
          REQUEST_TIMEOUT,
          CONFLICT,
          GONE,
          LENGTH_REQUIRED,
          PRECONDITION_FAILED,
          REQUEST_ENTITY_TOO_LARGE,
          REQUEST_URI_TOO_LONG,
          UNSUPPORTED_MEDIA_TYPE,
          REQUESTED_RANGE_NOT_SATISFIABLE,
          EXPECTATION_FAILED,
          UNPROCESSABLE_ENTITY,
          LOCKED,
          FAILED_DEPENDENCY,
          TOO_MANY_REQUESTS
        ).foreach { exception =>
          val result = customHttpErrorHandler.onClientError(FakeRequest(), exception)

          status(result) shouldBe exception
        }
      }
    }
  }

}
