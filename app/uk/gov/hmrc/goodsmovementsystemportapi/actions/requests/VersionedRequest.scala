/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions.requests

import play.api.mvc.{Request, WrappedRequest}
import uk.gov.hmrc.goodsmovementsystemportapi.schemas.ApiVersion

case class VersionedRequest[A](apiVersion: ApiVersion, request: Request[A]) extends WrappedRequest[A](request)
