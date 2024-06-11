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
import models.{Done, TaxFreeInterestRequest}
import play.api.{Configuration, Logging}
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculateBackendConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit
                                                                                            ec: ExecutionContext
) extends Logging {

  private val baseUrl = config.get[Service]("microservice.services.calculate-public-pension-adjustment")

  def findTaxFreeInterestValue(
                                taxFreeInterestRequest: TaxFreeInterestRequest
                              )(implicit hc: HeaderCarrier): Future[Int] = {
    httpClient
      .post(
        url"$baseUrl/calculate-public-pension-adjustment/tax-free-interest-value"
      )
      .withBody(Json.toJson(taxFreeInterestRequest))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            println(s"=========MIKE==============${response.body.toInt}===============MIKE=================")
            Future.successful(response.body.toInt)
          case _ => Future.failed(
            UpstreamErrorResponse(
              "Unexpected response from calculate-public-pension-adjustment/tax-free-interest-value",
              response.status
            )
          )
        }
      }
  }

  def updateUserAnswersFromCalcUA(id: String)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .get(
        url"$baseUrl/calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers-with-id/$id"
      )
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK         =>
            Future.successful(Done)
          case NO_CONTENT => Future.successful(Done)
          case _          =>
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers-with-id with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers-with-id",
                response.status
              )
            )
        }
      }

}