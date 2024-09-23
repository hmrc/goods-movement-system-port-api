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

package uk.gov.hmrc.goodsmovementsystemportapi.platform.controllers

import cats.implicits.catsSyntaxEq
import controllers.{Assets, AssetsBuilder, AssetsMetadata}
import org.apache.pekko.stream.Materializer
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent, DefaultActionBuilder}
import play.api.{Configuration, Environment}
import play.filters.cors.CORSActionBuilder
import uk.gov.hmrc.goodsmovementsystemportapi.platform.views.txt

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DocumentationController @Inject() (
  defaultActionBuilder:          DefaultActionBuilder,
  httpErrorHandler:              HttpErrorHandler,
  meta:                          AssetsMetadata,
  assets:                        Assets,
  configuration:                 Configuration,
  @Named("apiStatus") apiStatus: String
)(implicit materializer: Materializer, executionContext: ExecutionContext, env: Environment)
    extends AssetsBuilder(httpErrorHandler, meta, env) {

  def documentation(version: String, endpointName: String): Action[AnyContent] =
    super.at(s"/public/api/documentation/$version", s"${endpointName.replaceAll(" ", "-")}.xml")

  def definition(): Action[AnyContent] = defaultActionBuilder.async {
    Future.successful(
      Ok(txt.definition(apiStatus, apiStatus =!= "ALPHA"))
        .withHeaders("Content-Type" -> "application/json")
    )
  }

  def yaml(version: String, file: String): Action[AnyContent] =
    CORSActionBuilder(configuration).async { implicit request =>
      assets.at(s"/public/api/conf/$version", file)(request)
    }
}
