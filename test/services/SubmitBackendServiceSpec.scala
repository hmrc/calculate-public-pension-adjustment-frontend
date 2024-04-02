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
import connectors.{SubmitBackendConnector, UserAnswersConnector}
import models.{Done, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class SubmitBackendServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

  "SubmitBackendService" - {
    val hc: HeaderCarrier = HeaderCarrier()

    "clearUserAnswers" - {
      "must return Done when the connector clears successfully" in {
        val connector = mock[SubmitBackendConnector]
        val service   = new SubmitBackendService(connector)

        when(connector.clearUserAnswers()(any())) thenReturn Future.successful(Done)

        val result = service.clearUserAnswers()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to clear" in {
        val connector = mock[SubmitBackendConnector]
        val service   = new SubmitBackendService(connector)

        when(connector.clearUserAnswers()(any())) thenReturn Future.failed(new RuntimeException("Clear failed"))

        val result = service.clearUserAnswers()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }

    "clearSubmissions" - {
      "must return Done when the connector clears successfully" in {
        val connector = mock[SubmitBackendConnector]
        val service   = new SubmitBackendService(connector)

        when(connector.clearSubmissions()(any())) thenReturn Future.successful(Done)

        val result = service.clearSubmissions()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to clear" in {
        val connector = mock[SubmitBackendConnector]
        val service   = new SubmitBackendService(connector)

        when(connector.clearSubmissions()(any())) thenReturn Future.failed(new RuntimeException("Clear failed"))

        val result = service.clearSubmissions()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }

    "recordsPresentInSubmissionService" - {
      "should call connector when checking submission status" in {
        val mockUserAnswersConnector = mock[SubmitBackendConnector]

        val service = new SubmitBackendService(mockUserAnswersConnector)

        service.userAnswersPresentInSubmissionService("id")(hc)

        verify(mockUserAnswersConnector, times(1))
          .userAnswersPresentInSubmissionService("id")(hc)
      }
    }

    "submissionsPresentInSubmissionService" - {
      "should call connector when checking submission status" in {
        val mockUserAnswersConnector = mock[SubmitBackendConnector]

        val service = new SubmitBackendService(mockUserAnswersConnector)

        service.submissionsPresentInSubmissionService("uniqueId")(hc)

        verify(mockUserAnswersConnector, times(1))
          .submissionsPresentInSubmissionService("uniqueId")(hc)
      }
    }
  }
}
