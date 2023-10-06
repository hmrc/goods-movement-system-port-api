/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.config

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Singleton

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {

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
