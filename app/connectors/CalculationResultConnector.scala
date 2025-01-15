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
import config.FrontendAppConfig
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.CalculationResults.{CalculationInputs, CalculationResponse}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class CalculationResultConnector @Inject() (
  config: FrontendAppConfig,
  httpClient: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(
    calculationInputs: CalculationInputs
  )(implicit hc: HeaderCarrier): Future[CalculationResponse] =
    httpClient
      .post(url"${config.cppaBaseUrl}/calculate-public-pension-adjustment/show-calculation")
      .withBody(Json.toJson(calculationInputs))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "Calculation Result on set")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[CalculationResponse])
          case _  =>
            logger.error(
              s"Unexpected response from call to /calculate-public-pension-adjustment/show-calculation with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/show-calculation",
                response.status
              )
            )
        }
      }

}
