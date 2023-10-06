/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions.requests

import play.api.mvc.WrappedRequest
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

case class GmsAuthRequest[A](clientId: String, request: VersionedRequest[A]) extends WrappedRequest[A](request) {
  val apiVersion: ApiVersion = request.apiVersion
}
