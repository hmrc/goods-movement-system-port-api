/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.config

import java.net.URLEncoder

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  val authBaseUrl: String = servicesConfig.baseUrl("auth")

  val auditingEnabled: Boolean = config.get[Boolean]("auditing.enabled")
  val graphiteHost:    String  = config.get[String]("microservice.metrics.graphite.host")

  val apiContext = URLEncoder.encode(config.get[String]("apiContext"), "UTF-8")
  val apiVersion = config.get[String]("apiVersion")

  val effectiveDatesCheck: Boolean = config.get[Boolean]("features.effectiveDatesCheck")

}
