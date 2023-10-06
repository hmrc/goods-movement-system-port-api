/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiSubscriptionFieldsConnector @Inject()(
  httpClient:                                 HttpClient,
  apiConfig:                                  AppConfig,
  @Named("apiSubscriptionFieldsUrl") baseUrl: String
)(implicit executionContext:                  ExecutionContext) {

  private def url(path: String) = s"$baseUrl$path"

  def getSubscriptionFields(clientId: String)(implicit hc: HeaderCarrier): Future[SubscriptionFieldsResponse] =
    httpClient
      .GET[SubscriptionFieldsResponse](url(s"/field/application/$clientId/context/${apiConfig.apiContext}/version/${apiConfig.apiVersion}"))

}
