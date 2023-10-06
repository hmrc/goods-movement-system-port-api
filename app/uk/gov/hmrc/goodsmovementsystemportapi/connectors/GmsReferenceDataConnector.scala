/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.connectors

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits.readFromJson
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GmsReferenceDataConnector @Inject()(
  httpClient:                         HttpClient,
  @Named("referenceDataUrl") baseUrl: String
)(implicit executionContext:          ExecutionContext) {

  private def url(path: String) = s"$baseUrl$path"

  def getReferenceData(implicit hc: HeaderCarrier): Future[GvmsReferenceData] =
    httpClient.GET[GvmsReferenceData](url(s"/goods-movement-system-reference-data/reference-data"))

}
