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

///*
// * Copyright 2024 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */

package connectors

import base.SpecBase
import generators.Generators
import models.CalculationResults._
import models.submission.{Failure, SubmissionRequest, SubmissionResponse, Success}
import models.{Done, IncomeSubJourney, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, Period, ReducedNetIncomeRequest, ReducedNetIncomeResponse}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NO_CONTENT}
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import com.github.tomakehurst.wiremock.client.WireMock._

import scala.util.Try

class ReducedNetIncomeConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.calculate-public-pension-adjustment.port" -> server.port)
      .build()

  val hc: HeaderCarrier                  = HeaderCarrier()
  val scottishTaxYears: List[Period]     = List(Period._2021)
  val incomeSubJourney: IncomeSubJourney = IncomeSubJourney(
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(true),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1),
    Some(1)
  )

  val reducedNetIncomeRequest: ReducedNetIncomeRequest =
    ReducedNetIncomeRequest(Period._2021, scottishTaxYears, 1, incomeSubJourney)

  "SubmissionConnectorSpec" - {

    "sendSubmissionRequest" - {

      "must return a response containing a calculated personal allowance and reduced net income" in {

        val url = s"/calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income"
        val app = application

        running(app) {
          val connector = app.injector.instanceOf[ReducedNetIncomeConnector]

          val reducedNetIncomeResponse: ReducedNetIncomeResponse = ReducedNetIncomeResponse(1, 1)
          val responseBody                                       = Json.toJson(reducedNetIncomeResponse).toString

          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withBody(responseBody)))

          val result = connector.sendReducedNetIncomeRequest(reducedNetIncomeRequest)(hc).futureValue
          result mustBe reducedNetIncomeResponse

        }
      }

      "must return a failed future when the server responds with an error status" in {

        val url = s"/calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income"
        val app = application

        running(app) {
          val connector = app.injector.instanceOf[ReducedNetIncomeConnector]

          val responseBody = Json.toJson(Failure(Seq("someError"))).toString

          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody)))

          val response: Try[ReducedNetIncomeResponse] =
            Try(connector.sendReducedNetIncomeRequest(reducedNetIncomeRequest)(hc).futureValue)

          response.isFailure mustBe true
        }
      }

    }
  }
}
