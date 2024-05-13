/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import base.SpecBase
import connectors.SubmissionsConnector
import models.Done
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class SubmissionDataServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {
  "SubmissionDataService" - {
    val hc: HeaderCarrier = HeaderCarrier()

    "clear" - {
      "must return Done when the connector clears successfully" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.clear()(any())) thenReturn Future.successful(Done)

        val result = service.clear()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to clear" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.clear()(any())) thenReturn Future.failed(new RuntimeException("Clear failed"))

        val result = service.clear()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }
  }
}
