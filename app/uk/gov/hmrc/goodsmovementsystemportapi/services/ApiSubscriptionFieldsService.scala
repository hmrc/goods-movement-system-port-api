/*
 * Copyright 2023 HM Revenue & Customs
 *
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
