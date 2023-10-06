/*
 * Copyright 2023 HM Revenue & Customs
 *
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
