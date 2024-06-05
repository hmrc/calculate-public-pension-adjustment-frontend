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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import models.CalculationResults.{CalculationInputs, CalculationResponse, Resubmission, TotalAmounts}
import models.submission.Failure
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

import scala.util.Try

class CalculationResultConnectorSpec
    extends SpecBase
    with WireMockHelper
    with ScalaCheckPropertyChecks
    with Generators {

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.calculate-public-pension-adjustment.port" -> server.port)
      .build()

  val hc: HeaderCarrier = HeaderCarrier()

  "CalculationResponseConnector" - {

    "sendRequest" - {

      "must return a calculation response when the server provides one" in {

        val url = s"/calculate-public-pension-adjustment/show-calculation"
        val app = application

        val calcInputs = CalculationInputs(Resubmission(false, None), None, None)

        running(app) {
          val connector = app.injector.instanceOf[CalculationResultConnector]

          val calculationResponse = CalculationResponse(
            Resubmission(false, None),
            TotalAmounts(0, 0, 0),
            List(
            ),
            List(
            )
          )

          val responseBody = Json.toJson(calculationResponse).toString

          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withBody(responseBody)))

          val result = connector.sendRequest(calcInputs).futureValue

          result mustBe a[CalculationResponse]
          result mustBe calculationResponse
        }
      }

      "must return a failed future when the server responds with an error" in {

        val url = s"/calculate-public-pension-adjustment/show-calculation"
        val app = application

        val calcInputs = CalculationInputs(Resubmission(false, None), None, None)

        running(app) {
          val connector = app.injector.instanceOf[CalculationResultConnector]

          val responseBody = Json.toJson(Failure(Seq("someError"))).toString

          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody)))

          val response: Try[CalculationResponse] =
            Try(connector.sendRequest(calcInputs).futureValue)

          response.isFailure mustBe true
        }
      }
    }
  }
}
