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

package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import models.submission.{SubmissionRequest, SubmissionResponse, Success}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HttpClient, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class BackendConnector @Inject() (config: FrontendAppConfig, httpClient: HttpClient)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendSubmissionRequest(
    submissionRequest: SubmissionRequest
  ): Future[SubmissionResponse] = {
    val body: JsValue = Json.toJson(submissionRequest)

    httpClient
      .doPost(
        s"${config.cppaBaseUrl}/calculate-public-pension-adjustment/submission",
        body
      )
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(response.json.as[Success])
          case _        =>
            logger.error(s"Unexpected response from backend with status ${response.status}")
            Future.failed(UpstreamErrorResponse("Unexpected response from backend", response.status))
        }
      }
  }
}
