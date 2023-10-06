/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.platform.controllers

import akka.stream.Materializer
import cats.implicits.catsSyntaxEq
import controllers.{Assets, AssetsBuilder, AssetsMetadata}
import javax.inject.{Inject, Named, Singleton}
import play.api.Configuration
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent, ControllerComponents, DefaultActionBuilder}
import play.filters.cors.CORSActionBuilder
import uk.gov.hmrc.goodsmovementsystemportapi.platform.views.txt

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DocumentationController @Inject()(
  defaultActionBuilder:          DefaultActionBuilder,
  httpErrorHandler:              HttpErrorHandler,
  meta:                          AssetsMetadata,
  assets:                        Assets,
  cc:                            ControllerComponents,
  configuration:                 Configuration,
  @Named("apiStatus") apiStatus: String
)(implicit materializer:         Materializer, executionContext: ExecutionContext)
    extends AssetsBuilder(httpErrorHandler, meta) {

  def documentation(version: String, endpointName: String): Action[AnyContent] =
    super.at(s"/public/api/documentation/$version", s"${endpointName.replaceAll(" ", "-")}.xml")

  def definition(): Action[AnyContent] = defaultActionBuilder.async {
    Future.successful(
      Ok(txt.definition(apiStatus, apiStatus =!= "ALPHA"))
        .withHeaders("Content-Type" -> "application/json"))
  }

  def yaml(version: String, file: String): Action[AnyContent] =
    CORSActionBuilder(configuration).async { implicit request =>
      assets.at(s"/public/api/conf/$version", file)(request)
    }
}
