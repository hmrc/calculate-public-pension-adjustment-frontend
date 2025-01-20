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
import models.{ReducedNetIncomeRequest, ReducedNetIncomeResponse}
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.writeableOf_JsValue

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReducedNetIncomeConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit
  ec: ExecutionContext
) extends Logging {

  private val baseUrl                  = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val subsidiaryCalculationUrl =
    url"$baseUrl/calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income"

  def sendReducedNetIncomeRequest(
    reducedNetIncomeRequest: ReducedNetIncomeRequest
  )(implicit hc: HeaderCarrier): Future[ReducedNetIncomeResponse] =
    httpClient
      .post(subsidiaryCalculationUrl)
      .withBody(Json.toJson(reducedNetIncomeRequest))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "ReducedNetIncomeConnector on set")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[ReducedNetIncomeResponse])
          case _  =>
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
