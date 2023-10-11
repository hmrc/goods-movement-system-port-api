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

package uk.gov.hmrc.goodsmovementsystemportapi.services

import cats.data.OptionT
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.ApiSubscriptionFieldsConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiSubscriptionFieldsService @Inject()(
  apiSubscriptionFieldsConnector: ApiSubscriptionFieldsConnector
)(implicit executionContext:      ExecutionContext) {

  def getField(clientId: String, fieldKey: String)(implicit hc: HeaderCarrier): OptionT[Future, String] =
    OptionT(apiSubscriptionFieldsConnector.getSubscriptionFields(clientId).map(_.fields.get(fieldKey)))

}
