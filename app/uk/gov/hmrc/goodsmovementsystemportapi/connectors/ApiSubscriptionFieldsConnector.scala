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

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiSubscriptionFieldsConnector @Inject() (
  httpClient:                                 HttpClient,
  apiConfig:                                  AppConfig,
  @Named("apiSubscriptionFieldsUrl") baseUrl: String
)(implicit executionContext: ExecutionContext) {

  private def url(path: String) = s"$baseUrl$path"

  def getSubscriptionFields(clientId: String)(implicit hc: HeaderCarrier): Future[SubscriptionFieldsResponse] =
    httpClient
      .GET[SubscriptionFieldsResponse](url(s"/field/application/$clientId/context/${apiConfig.apiContext}/version/${apiConfig.apiVersion}"))

}
