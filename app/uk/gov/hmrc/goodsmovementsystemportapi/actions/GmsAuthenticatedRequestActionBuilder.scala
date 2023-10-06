/*
 * Copyright 2023 HM Revenue & Customs
 *
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
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GmsAuthenticatedRequestActionBuilder @Inject()(
  val config:                    Configuration,
  val env:                       Environment,
  val authConnector:             AuthConnector
)(implicit val executionContext: ExecutionContext)
    extends ActionFunction[VersionedRequest, GmsAuthRequest]
    with Results
    with AuthRedirects
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
