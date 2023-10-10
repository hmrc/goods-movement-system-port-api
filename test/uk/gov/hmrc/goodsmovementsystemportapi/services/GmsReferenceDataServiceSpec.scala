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

import org.mockito.Mockito._
import play.api.test.Helpers.await
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataServiceSpec extends BaseSpec {

  trait Setup {
    val service = new GmsReferenceDataService(mockGmsReferenceDataConnector)
  }

  "getReferenceData" when {
    "the getReferenceData is successful" should {
      "return the 200(OK)" in new Setup {

        when(mockGmsReferenceDataConnector.getReferenceData)
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: GvmsReferenceData = await(service.getReferenceData)

        result shouldBe gmsReferenceDataSummary

        verify(mockGmsReferenceDataConnector).getReferenceData
      }
    }

  }

}
