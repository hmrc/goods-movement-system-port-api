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

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.http.HttpReadsInstances.readFromJson
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GoodsMovementSystemConnector @Inject()(
  httpClient:                HttpClient,
  @Named("gmsUrl") baseUrl:  String
)(implicit executionContext: ExecutionContext)
    extends CustomEitherHttpReads {

  private def url(path: String) = s"$baseUrl$path"

  def getControlledArrivalsGmr(portId: String)(implicit hc: HeaderCarrier): Future[List[GetControlledArrivalsGmrResponse]] =
    httpClient.GET[List[GetControlledArrivalsGmrResponse]](url(s"/goods-movement-system/$portId/arrivals/controlled"))

  def getControlledDeparturesGmr(portId: String)(implicit hc: HeaderCarrier): Future[List[GetControlledDeparturesGmrResponse]] =
    httpClient.GET[List[GetControlledDeparturesGmrResponse]](url(s"/goods-movement-system/$portId/departures/controlled"))

  def getDeparturesGmr(
    portId:          String,
    lastUpdatedFrom: Option[Instant],
    lastUpdatedTo:   Option[Instant]
  )(implicit hc:     HeaderCarrier): Future[Either[GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]]] =
    httpClient.GET[Either[GetDepartureErrors, List[GetPortDepartureExpandedGmrResponse]]](
      url(s"/goods-movement-system/$portId/departures"),
      Seq[List[(String, String)]](
        lastUpdatedFrom.map(from => "lastUpdatedFrom" -> from.toString).toList,
        lastUpdatedTo.map(to => "lastUpdatedTo"       -> to.toString).toList
      ).flatten[(String, String)]
    )

}
