/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.{ApiSubscriptionFieldsConnector, GmsReferenceDataConnector}
import uk.gov.hmrc.goodsmovementsystemportapi.services.{ApiSubscriptionFieldsService, GmsReferenceDataService, PortsService}
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

trait AllMocks extends MockitoSugar { me: BeforeAndAfterEach =>

  val mockApiSubscriptionFieldsConnector: ApiSubscriptionFieldsConnector = mock[ApiSubscriptionFieldsConnector]
  val mockApiSubscriptionFieldsService:   ApiSubscriptionFieldsService   = mock[ApiSubscriptionFieldsService]
  val mockAuditConnector:                 AuditConnector                 = mock[AuditConnector]
  val mockAuthConnector:                  AuthConnector                  = mock[AuthConnector]
  val mockHttpClient:                     HttpClient                     = mock[HttpClient]
  val mockPortService:                    PortsService                   = mock[PortsService]
  val mockGmsReferenceDataConnector:      GmsReferenceDataConnector      = mock[GmsReferenceDataConnector]
  val mockGmsReferenceDataService:        GmsReferenceDataService        = mock[GmsReferenceDataService]
  val mockAppConfig:                      AppConfig                      = mock[AppConfig]

  override protected def beforeEach(): Unit =
    Seq(
      mockAuditConnector,
      mockAuthConnector,
      mockHttpClient,
      mockPortService
    ).foreach(Mockito.reset(_))
}
