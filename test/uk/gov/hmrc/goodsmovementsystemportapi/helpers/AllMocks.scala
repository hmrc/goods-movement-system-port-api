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

package uk.gov.hmrc.goodsmovementsystemportapi.helpers

import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.goodsmovementsystemportapi.config.AppConfig
import uk.gov.hmrc.goodsmovementsystemportapi.connectors.{ApiSubscriptionFieldsConnector, GmsReferenceDataConnector}
import uk.gov.hmrc.goodsmovementsystemportapi.services.{ApiSubscriptionFieldsService, GmsReferenceDataService, PortsService}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

trait AllMocks extends MockitoSugar { me: BeforeAndAfterEach =>

  val mockApiSubscriptionFieldsConnector: ApiSubscriptionFieldsConnector = mock[ApiSubscriptionFieldsConnector]
  val mockApiSubscriptionFieldsService:   ApiSubscriptionFieldsService   = mock[ApiSubscriptionFieldsService]
  val mockAppConfig:                      AppConfig                      = mock[AppConfig]
  val mockAuditConnector:                 AuditConnector                 = mock[AuditConnector]
  val mockAuthConnector:                  AuthConnector                  = mock[AuthConnector]
  val mockGmsReferenceDataConnector:      GmsReferenceDataConnector      = mock[GmsReferenceDataConnector]
  val mockGmsReferenceDataService:        GmsReferenceDataService        = mock[GmsReferenceDataService]
  val mockHttpClient:                     HttpClientV2                   = mock[HttpClientV2]
  val mockPortService:                    PortsService                   = mock[PortsService]
  val mockRequestBuilder:                 RequestBuilder                 = mock[RequestBuilder]

  override protected def beforeEach(): Unit =
    Seq[Any](
      mockApiSubscriptionFieldsConnector,
      mockApiSubscriptionFieldsService,
      mockAppConfig,
      mockAuditConnector,
      mockAuthConnector,
      mockGmsReferenceDataConnector,
      mockGmsReferenceDataService,
      mockHttpClient,
      mockPortService,
      mockRequestBuilder
    ).foreach(Mockito.reset(_))
}
