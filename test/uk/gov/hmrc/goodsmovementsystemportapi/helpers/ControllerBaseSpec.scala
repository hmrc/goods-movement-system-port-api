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

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import org.scalatest.Inside
import org.scalatest.concurrent.Eventually
import org.scalatest.time.Span
import play.api.http.{HeaderNames, HttpProtocol, MimeTypes, Status}
import play.api.mvc._
import play.api.test._
import uk.gov.hmrc.goodsmovementsystemportapi.ResultAssertions
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.{AcceptHeaderFilter, ApiVersionRefiner, GmsActionBuilders, GmsAuthenticatedRequestActionBuilder}
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.Version_1_0

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait ControllerBaseSpec
    extends BaseSpec
    with ResultExtractors
    with HeaderNames
    with Status
    with Results
    with MimeTypes
    with HttpProtocol
    with DefaultAwaitTimeout
    with Writeables
    with EssentialActionCaller
    with RouteInvokers
    with FutureAwaits
    with StubControllerComponentsFactory
    with ResultAssertions
    with Inside
    with Eventually {

  def preAuthenticatedActionBuilders: GmsActionBuilders =
    new GmsActionBuilders(mock[DefaultActionBuilder], mock[AcceptHeaderFilter], mock[ApiVersionRefiner], mock[GmsAuthenticatedRequestActionBuilder]) {
      override lazy val validateAuthAndApiVersionTransformer: ActionBuilder[GmsAuthRequest, AnyContent] =
        new ActionBuilder[GmsAuthRequest, AnyContent] {
          override def parser: BodyParser[AnyContent] = stubControllerComponents().parsers.defaultBodyParser

          override def invokeBlock[A](request: Request[A], block: GmsAuthRequest[A] => Future[Result]): Future[Result] =
            block(GmsAuthRequest("clientId", VersionedRequest(Version_1_0, request)))

          override protected def executionContext: ExecutionContext = ec
        }

      override lazy val validateAndApiVersionTransformer: ActionBuilder[VersionedRequest, AnyContent] =
        new ActionBuilder[VersionedRequest, AnyContent] {
          override def parser: BodyParser[AnyContent] = stubControllerComponents().parsers.defaultBodyParser

          override def invokeBlock[A](request: Request[A], block: VersionedRequest[A] => Future[Result]): Future[Result] =
            block(VersionedRequest(Version_1_0, request))

          override protected def executionContext: ExecutionContext = ec
        }
    }

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(3.seconds, Span(100, org.scalatest.time.Millis))

}
