/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

trait WireMockConfig {
  me: BaseISpec with WireMockSupport =>

  additionalAppConfig ++=
    setWireMockPort(
      "goods-movement-system",
      "auth",
      "goods-movement-system-reference-data",
      "api-subscription-fields"
    )

  private def setWireMockPort(services: String*): Map[String, Int] =
    services.foldLeft[Map[String, Int]](Map.empty[String, Int]) {
      case (map, service) =>
        map + (s"microservice.services.$service.port" -> mockServerPort)
    }
}
