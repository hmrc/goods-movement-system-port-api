/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.auth.core.AuthProvider.StandardApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ActionFunctionBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.Version_1_0

import scala.concurrent.Future

class GmsAuthenticatedRequestActionBuilderSpec extends ActionFunctionBaseSpec {

  val gmsAuthenticatedRequestActionBuilder =
    new GmsAuthenticatedRequestActionBuilder(configuration, environment, mockAuthConnector)

  "invokeBlock" when {
    "the user has a valid bearer token" should {
      "execute the block successfully if auth returns successfully" in {
        when(mockAuthConnector
          .authorise[Option[String]](ArgumentMatchers.eq(AuthProviders(StandardApplication)), ArgumentMatchers.eq(Retrievals.clientId))(any(), any()))
          .thenReturn(Future.successful(Some("clientId")))

        val request: VersionedRequest[AnyContent] = VersionedRequest(Version_1_0, fakeRequest)

        val block: GmsAuthRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

        val result = gmsAuthenticatedRequestActionBuilder.invokeBlock[AnyContent](request, block)

        status(result)          shouldBe OK
        contentAsString(result) shouldBe "COMPLETED"
      }
      "return FORBIDDEN if the auth returns exception" in {
        when(mockAuthConnector
          .authorise[Option[String]](ArgumentMatchers.eq(AuthProviders(StandardApplication)), ArgumentMatchers.eq(Retrievals.clientId))(any(), any()))
          .thenReturn(Future.successful(None))

        val request: VersionedRequest[AnyContent] = VersionedRequest(Version_1_0, fakeRequest)

        val block: GmsAuthRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

        val result = gmsAuthenticatedRequestActionBuilder.invokeBlock[AnyContent](request, block)

        status(result) shouldBe FORBIDDEN
      }
    }

    "the user does not have an active session" in {
      Seq[NoActiveSession](
        BearerTokenExpired(),
        MissingBearerToken(),
        InvalidBearerToken(),
        SessionRecordNotFound()
      ).foreach { exception =>
        when(mockAuthConnector
          .authorise[Option[String]](ArgumentMatchers.eq(AuthProviders(StandardApplication)), ArgumentMatchers.eq(Retrievals.clientId))(any(), any()))
          .thenReturn(Future.failed(exception))

        val request: VersionedRequest[AnyContent] = VersionedRequest(Version_1_0, fakeRequest)

        val block: GmsAuthRequest[AnyContent] => Future[Result] = _ => Future.successful(Ok("COMPLETED"))

        val result = gmsAuthenticatedRequestActionBuilder.invokeBlock[AnyContent](request, block)

        status(result)          shouldBe UNAUTHORIZED
        contentAsString(result) shouldBe ""
      }
    }
  }
}
