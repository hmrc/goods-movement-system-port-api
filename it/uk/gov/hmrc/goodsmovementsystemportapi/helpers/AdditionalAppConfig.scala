/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import scala.collection.mutable

trait AdditionalAppConfig {
  @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
  val additionalAppConfig: mutable.Map[String, Any] = mutable.Map.empty[String, Any]
}
