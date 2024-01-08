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

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthProvider.StandardApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendHeaderCarrierProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GmsAuthenticatedRequestActionBuilder @Inject()(
  val config:                    Configuration,
  val env:                       Environment,
  val authConnector:             AuthConnector
)(implicit val executionContext: ExecutionContext)
    extends ActionFunction[VersionedRequest, GmsAuthRequest]
    with Results
    with AuthorisedFunctions
    with BackendHeaderCarrierProvider {

  override def invokeBlock[A](request: VersionedRequest[A], block: GmsAuthRequest[A] => Future[Result]): Future[Result] =
    authorised(AuthProviders(StandardApplication))
      .retrieve(Retrievals.clientId) { optClientId =>
        optClientId.fold[Future[Result]](Future(Forbidden))(clientId => block(GmsAuthRequest(clientId, request)))
      }(hc(request), executionContext)
      .recover(handleFailure)

  def handleFailure: PartialFunction[Throwable, Result] = {
    case _: NoActiveSession => Unauthorized
  }
}
