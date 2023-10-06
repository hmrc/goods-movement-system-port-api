/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers

sealed trait InvalidApiVersion extends Product with Serializable

object InvalidApiVersion {
  case object MissingAcceptHeader extends InvalidApiVersion
  case object InvalidAcceptHeaderFormat extends InvalidApiVersion
  case object UnsupportedVersion extends InvalidApiVersion

}
