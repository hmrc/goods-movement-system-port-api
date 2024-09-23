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

import uk.gov.hmrc.goodsmovementsystemportapi.connectors.httpreads.CustomEitherHttpReads
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.GetDepartureErrors
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.http.HttpReadsInstances.readFromJson
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.time.Instant
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GoodsMovementSystemConnector @Inject() (
  httpClient:               HttpClientV2,
  @Named("gmsUrl") baseUrl: String
)(implicit executionContext: ExecutionContext)
    extends CustomEitherHttpReads {

  def getControlledArrivalsGmr(portId: String)(implicit hc: HeaderCarrier): Future[List[GetControlledArrivalsGmrResponse]] =
    httpClient.get(url"$baseUrl/goods-movement-system/$portId/arrivals/controlled").execute[List[GetControlledArrivalsGmrResponse]]

  def getControlledDeparturesGmr(portId: String)(implicit hc: HeaderCarrier): Future[List[GetControlledDeparturesGmrResponse]] =
    httpClient.get(url"$baseUrl/goods-movement-system/$portId/departures/controlled").execute[List[GetControlledDeparturesGmrResponse]]

  def getDeparturesGmr(
    portId:          String,
    lastUpdatedFrom: Option[Instant],
    lastUpdatedTo:   Option[Instant]
  )(implicit hc: HeaderCarrier): Future[Either[GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]]] =
    httpClient
      .get(
        url"$baseUrl/goods-movement-system/$portId/departures?lastUpdatedFrom=$lastUpdatedFrom&lastUpdatedTo=$lastUpdatedTo"
      )
      .execute[Either[GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]]]
}
