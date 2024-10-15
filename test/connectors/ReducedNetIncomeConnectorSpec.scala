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
import uk.gov.hmrc.http.HeaderCarrier
import com.github.tomakehurst.wiremock.client.WireMock._

import scala.util.Try

class ReducedNetIncomeConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.calculate-public-pension-adjustment.port" -> server.port)
      .build()

  val hc: HeaderCarrier = HeaderCarrier()
  val scottishTaxYears :List[Period] = List(Period._2021)
  val incomeSubJourney: IncomeSubJourney = IncomeSubJourney(Some(1), Some(1), Some(1), Some(1), Some(true), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1), Some(1))


  val reducedNetIncomeRequest: ReducedNetIncomeRequest = ReducedNetIncomeRequest(Period._2021, scottishTaxYears, 1, incomeSubJourney)

  "SubmissionConnectorSpec" - {

    "sendSubmissionRequest" - {


      "must return a Success response containing a uniqueId when the server provides one" in {

        val url = s"/calculate-public-pension-adjustment/calculate-personal-allowance-and-reduced-net-income"
        val app = application

        running(app) {
          val connector = app.injector.instanceOf[ReducedNetIncomeConnector]

          val reducedNetIncomeResponse: ReducedNetIncomeResponse = ReducedNetIncomeResponse(1,1)
          val responseBody = Json.toJson(reducedNetIncomeResponse).toString

          //server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withStatus(ACCEPTED).withBody(responseBody)))
          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withBody(responseBody)))

          val result1 = connector.sendReducedNetIncomeRequest(reducedNetIncomeRequest)(hc).futureValue
          println("============================")
          println(result1)
          println("============================")

          result1 mustBe reducedNetIncomeResponse


        }
      }

//      "must return a failed future when the server responds with an error" in {
//
//        val url = s"/calculate-public-pension-adjustment/submission"
//        val app = application
//
//        running(app) {
//          val connector         = app.injector.instanceOf[SubmissionsConnector]
//          val submissionRequest =
//            SubmissionRequest(
//              CalculationInputs(
//                Resubmission(false, None),
//                Setup(
//                  Some(
//                    AnnualAllowanceSetup(
//                      Some(true),
//                      Some(false),
//                      Some(false),
//                      Some(false),
//                      Some(false),
//                      Some(false),
//                      Some(MaybePIAIncrease.No),
//                      Some(MaybePIAUnchangedOrDecreased.No),
//                      Some(false),
//                      Some(false),
//                      Some(false),
//                      Some(false)
//                    )
//                  ),
//                  Some(
//                    LifetimeAllowanceSetup(
//                      Some(true),
//                      Some(false),
//                      Some(true),
//                      Some(false),
//                      Some(false),
//                      Some(false),
//                      Some(true)
//                    )
//                  )
//                ),
//                None,
//                None
//              ),
//              None,
//              "",
//              ""
//            )
//
//          val responseBody = Json.toJson(Failure(Seq("someError"))).toString
//
//          server.stubFor(post(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST).withBody(responseBody)))
//
//          val response: Try[SubmissionResponse] =
//            Try(connector.sendSubmissionRequest(submissionRequest)(hc).futureValue)
//
//          response.isFailure mustBe true
//        }
//      }
//    }
//
//    "clear" - {
//      "must return Done when the server responds with NO_CONTENT" in {
//        val app = application
//
//        server.stubFor(
//          delete(urlEqualTo("/calculate-public-pension-adjustment/submission"))
//            .willReturn(aResponse().withStatus(NO_CONTENT))
//        )
//
//        running(app) {
//          val connector = app.injector.instanceOf[SubmissionsConnector]
//          val result    = connector.clear()(hc).futureValue
//
//          result mustBe Done
//        }
//      }
//
//      "must return a failed future when the server responds with an error status" in {
//        val app = application
//
//        server.stubFor(
//          delete(urlEqualTo("/calculate-public-pension-adjustment/submission"))
//            .willReturn(aResponse().withStatus(BAD_REQUEST))
//        )
//
//        running(app) {
//          val connector = app.injector.instanceOf[UserAnswersConnector]
//          val result    = connector.clear()(hc).failed.futureValue
//
//          result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
//        }
//      }
//
//    }
//
//  }

    }}}
