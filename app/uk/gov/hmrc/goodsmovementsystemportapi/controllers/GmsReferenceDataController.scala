/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.goodsmovementsystemportapi.actions.GmsActionBuilders
import uk.gov.hmrc.goodsmovementsystemportapi.services.GmsReferenceDataService

import scala.concurrent.ExecutionContext

@Singleton
class GmsReferenceDataController @Inject()(
  gmsActionBuilders:         GmsActionBuilders,
  gmsReferenceDataService:   GmsReferenceDataService,
  cc:                        ControllerComponents
)(implicit executionContext: ExecutionContext)
    extends BaseGmrController(cc) {

  import gmsActionBuilders._

  def getReferenceData: Action[AnyContent] = validateAndApiVersionTransformer.async { implicit request =>
    gmsReferenceDataService.getReferenceData.map(gvmsRefData => Ok(Json.toJson(gvmsRefData)))
  }

}
