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

import com.google.inject.Inject
import config.Service
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.Done
import models.submission.{SubmissionRequest, SubmissionResponse, Success}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class SubmissionsConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit
  ec: ExecutionContext
) extends Logging {

  private val baseUrl       = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val submissionUrl = url"$baseUrl/calculate-public-pension-adjustment/submission"

  def sendSubmissionRequest(
    submissionRequest: SubmissionRequest
  )(implicit hc: HeaderCarrier): Future[SubmissionResponse] =
    httpClient
      .post(submissionUrl)
      .withBody(Json.toJson(submissionRequest))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on set")
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(response.json.as[Success])
          case _        =>
            logger.error(
              s"Unexpected response from call to /calculate-public-pension-adjustment/submission with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/submission",
                response.status
              )
            )
        }
      }
  def clear()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(submissionUrl)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on clear")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }
}
