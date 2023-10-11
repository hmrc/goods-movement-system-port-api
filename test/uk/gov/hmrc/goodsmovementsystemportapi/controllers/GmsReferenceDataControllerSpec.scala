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

package uk.gov.hmrc.goodsmovementsystemportapi.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.mvc.Result
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.ControllerBaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataControllerSpec extends ControllerBaseSpec {

  trait Setup {
    val controller = new GmsReferenceDataController(preAuthenticatedActionBuilders, mockGmsReferenceDataService, stubControllerComponents())
  }

  "getReferenceData" when {
    "getReferenceData is successful" should {
      "return 200 OK" in new Setup {

        when(mockGmsReferenceDataService.getReferenceData(any()))
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: Future[Result] = controller.getReferenceData(fakeRequest)

        status(result)                              shouldBe OK
        contentAsJson(result).as[GvmsReferenceData] shouldBe gmsReferenceDataSummary

        verify(mockGmsReferenceDataService).getReferenceData(any())
      }
    }
  }
}
