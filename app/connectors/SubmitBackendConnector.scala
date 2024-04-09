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

package connectors

import config.Service
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.{Done, SubmissionStatusResponse, UserAnswers}
import play.api.Configuration
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitBackendConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit
  ec: ExecutionContext
) {

  private val baseUrl = config.get[Service]("microservice.services.submit-public-pension-adjustment")

  def userAnswersPresentInSubmissionService(id: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    httpClient
      .get(url"$baseUrl/submit-public-pension-adjustment/user-answers-present/$id")
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmitBackendConnector on userAnswersPresentInSubmissionService")
      .flatMap { response =>
        Future.successful(response.body.toBoolean)
      }

  def submissionsPresentInSubmissionService(uniqueId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    httpClient
      .get(url"$baseUrl/submit-public-pension-adjustment/submissions-present/$uniqueId")
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmitBackendConnector on submissionsPresentInSubmissionService")
      .flatMap { response =>
        Future.successful(response.body.toBoolean)
      }

  def clearUserAnswers()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(url"$baseUrl/submit-public-pension-adjustment/user-answers")
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmitBackendConnector on clear")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }

  def clearSubmissions()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(url"$baseUrl/submit-public-pension-adjustment/submissions")
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmitBackendConnector on clear")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }
}
