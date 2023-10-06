/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import play.api.mvc.{AnyContent, Request, Result}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ActionFunctionBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.Version_1_0

import scala.concurrent.Future

class AcceptHeaderFilterSpec extends ActionFunctionBaseSpec {

  val acceptHeaderFilter = new AcceptHeaderFilter()

  "filter" when {
    "the accept header is a valid HMRC accept header" should {
      "execute the block" in {
        val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest))

        val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

        val result = acceptHeaderFilter.invokeBlock(request, block)

        status(result)          shouldBe OK
        contentAsString(result) shouldBe "COMPLETED"
      }
    }

    "the accept header is invalid" when {
      "the accept header is not valid" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "text/hmrc.vnd.1+jsonX"

          val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = acceptHeaderFilter.invokeBlock(request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the version is not present" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc+json"

          val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = acceptHeaderFilter.invokeBlock(request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the version is invalid" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1+json"

          val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = acceptHeaderFilter.invokeBlock(request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the content type is not present" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0"

          val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = acceptHeaderFilter.invokeBlock(request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the content type is invalid" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+jsonX"

          val request = GmsAuthRequest("clientId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = acceptHeaderFilter.invokeBlock(request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }
    }
  }
}
