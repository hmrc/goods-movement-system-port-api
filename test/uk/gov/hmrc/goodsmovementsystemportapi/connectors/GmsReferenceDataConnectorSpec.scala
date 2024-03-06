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

import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.test.FutureAwaits
import uk.gov.hmrc.goodsmovementsystemportapi.helpers.BaseSpec
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.GvmsReferenceData

import scala.concurrent.Future

class GmsReferenceDataConnectorSpec extends BaseSpec with FutureAwaits {

  trait Setup {
    val connector: GmsReferenceDataConnector = new GmsReferenceDataConnector(mockHttpClient, "http://localhost:0000")
  }
  "getReferenceData" when {
    "the GET was successful" should {
      "return the http response" in new Setup {

        when(
          mockHttpClient
            .GET[GvmsReferenceData](mEq("http://localhost:0000/goods-movement-system-reference-data/reference-data"), any(), any())(
              any(),
              any(),
              any()
            )
        )
          .thenReturn(Future.successful(gmsReferenceDataSummary))

        val result: GvmsReferenceData = await(connector.getReferenceData(hc))

        result shouldBe gmsReferenceDataSummary

        verify(mockHttpClient)
          .GET[GvmsReferenceData](mEq("http://localhost:0000/goods-movement-system-reference-data/reference-data"), any(), any())(any(), any(), any())
      }
    }

  }

}
