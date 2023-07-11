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
import models.CalculationUserAnswers
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class CalculationResultConnector @Inject() (
  config: FrontendAppConfig,
  httpClient1: HttpClient
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(
    calculationUserAnswers: CalculationUserAnswers
  )(implicit hc: HeaderCarrier): Future[CalculationUserAnswers] =
    httpClient1
      .doPost(
        s"${config.cppaBaseUrl}/calculate-public-pension-adjustment/show-calculation",
        Json.toJson(calculationUserAnswers)
      )
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[CalculationUserAnswers])
          case _  =>
            logger.error(s"Unexpected response from Cppa with status ${response.status}")
            Future.failed(UpstreamErrorResponse("Unexpected response from Cppa", response.status))
        }
      }

}
