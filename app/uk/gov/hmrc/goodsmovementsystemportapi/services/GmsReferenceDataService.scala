/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.GmsReferenceDataConnector
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class GmsReferenceDataService @Inject()(
  referenceDataConnector: GmsReferenceDataConnector,
) {

  def getReferenceData(implicit hc: HeaderCarrier): Future[GvmsReferenceData] =
    referenceDataConnector.getReferenceData
}
