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
import models.submission.Success
import models.{Done, IncomeSubJourney, Period, ReducedNetIncome}
import play.api.http.Status.{ACCEPTED, NO_CONTENT, OK}
import play.api.libs.json.Json
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class ReducedNetIncomeConnector @Inject()(config: Configuration, httpClient: HttpClientV2)(implicit
                                                                                           ec: ExecutionContext
) extends Logging {

  private val baseUrl = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val testUrl = url"$baseUrl/calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income"

  def sendReducedNetIncomeRequest(
                             period: Period,
                             scottishTaxYears: List[Period],
                             totalIncome: Int,
                             incomeSubJourney: IncomeSubJourney
                           )(implicit hc: HeaderCarrier): Future[ReducedNetIncomeConnector] =
    httpClient
      .post(testUrl)
      .withBody(Json.toJson(period, scottishTaxYears, totalIncome, incomeSubJourney))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "ReducedNetIncomeConnector on set")
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(response.json.as[Success])
          case _        =>
            logger.error(
              s"Unexpected response from call to /calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income",
                response.status
              )
            )
        }
      }

}
