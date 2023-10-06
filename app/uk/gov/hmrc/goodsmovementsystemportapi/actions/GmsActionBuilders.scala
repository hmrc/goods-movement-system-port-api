/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.actions

import javax.inject.{Inject, Singleton}
import play.api.mvc.{ActionBuilder, AnyContent, DefaultActionBuilder}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.requests.{GmsAuthRequest, VersionedRequest}

@Singleton
class GmsActionBuilders @Inject()(
  defaultActionBuilder:                 DefaultActionBuilder,
  validatedAction:                      AcceptHeaderFilter,
  apiVersionTransformer:                ApiVersionRefiner,
  gmsAuthenticatedRequestActionBuilder: GmsAuthenticatedRequestActionBuilder
) {

  lazy val validateAuthAndApiVersionTransformer: ActionBuilder[GmsAuthRequest, AnyContent] = defaultActionBuilder
    .andThen(validatedAction)
    .andThen(apiVersionTransformer)
    .andThen(gmsAuthenticatedRequestActionBuilder)

  lazy val validateAndApiVersionTransformer: ActionBuilder[VersionedRequest, AnyContent] = defaultActionBuilder
    .andThen(validatedAction)
    .andThen(apiVersionTransformer)
}
