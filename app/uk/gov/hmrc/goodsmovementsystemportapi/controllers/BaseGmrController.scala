/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.{BackendController, BackendHeaderCarrierProvider}

class BaseGmrController(cc: ControllerComponents) extends BackendController(cc) with BackendHeaderCarrierProvider {
  implicit val logger: Logger = Logger(this.getClass.getName)
}
