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

package uk.gov.hmrc.goodsmovementsystemportapi.config

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Singleton

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {}

  @Provides
  @Named("gmsUrl")
  @Singleton
  def registerGmsUrlProvider(servicesConfig: ServicesConfig): String =
    servicesConfig.baseUrl("goods-movement-system")

  @Provides
  @Named("apiSubscriptionFieldsUrl")
  @Singleton
  def registerApiSubscriptionUrlProvider(servicesConfig: ServicesConfig): String =
    servicesConfig.baseUrl("api-subscription-fields")

  @Provides
  @Named("apiStatus")
  @Singleton
  def apiEndpointsEnabledProvider(): String =
    configuration.getAndValidate[String]("api.status", Set("ALPHA", "BETA", "STABLE", "DEPRECATED", "RETIRED"))

  @Provides
  @Named("referenceDataUrl")
  @Singleton
  def registerReferenceDataUrlProvider(servicesConfig: ServicesConfig): String =
    servicesConfig.baseUrl("goods-movement-system-reference-data")

  @Provides
  @Named("showPendingGmrs")
  @Singleton
  def showPendingGmrsProvider(configuration: Configuration): Boolean =
    configuration.get[Boolean]("features.showPending")

}
