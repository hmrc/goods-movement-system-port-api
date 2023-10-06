/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import play.api.mvc.{AnyContent, Result}
import play.api.test.FakeRequest
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ActionFunctionBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.Version_1_0

import scala.concurrent.Future

class ApiVersionRefinerSpec extends ActionFunctionBaseSpec {

  val apiVersionTransformer = new ApiVersionRefiner

  "refine" when {
    "the accept header is a valid HMRC accept header" should {
      "execute the block" in {
        val request = GmsAuthRequest[AnyContent]("clientId", VersionedRequest(Version_1_0, fakeRequest))

        val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

        val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

        status(result)          shouldBe OK
        contentAsString(result) shouldBe "COMPLETED"
      }
    }

    "the accept header is invalid" when {
      "the accept header is not present" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "a" -> "b"

          val request = GmsAuthRequest[AnyContent]("clientId", VersionedRequest(Version_1_0, FakeRequest().withHeaders(invalidAcceptHeader)))

          val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the accept header is not valid" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "text/hmrc.vnd.1+jsonX"

          val request = GmsAuthRequest[AnyContent]("cleintId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the version is not present" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc+json"

          val request = GmsAuthRequest[AnyContent]("cleintId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the version is invalid" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1+json"

          val request = GmsAuthRequest[AnyContent]("cleintId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }

      "the content type is not present" should {
        "return NOT_ACCEPTABLE" in {
          val invalidAcceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0"

          val request = GmsAuthRequest[AnyContent]("cleintId", VersionedRequest(Version_1_0, fakeRequest.withHeaders(invalidAcceptHeader)))

          val block: VersionedRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

          val result = apiVersionTransformer.invokeBlock[AnyContent](request, block)

          status(result)          shouldBe NOT_ACCEPTABLE
          contentAsString(result) shouldBe """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
        }
      }
    }
  }
}
