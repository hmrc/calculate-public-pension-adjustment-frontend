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

package controllers

import base.SpecBase
import models.CalculationResults.CalculationResponse
import models.submission.{Failure, Success}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, status, _}
import services.CalculationResultService

import scala.concurrent.Future
import scala.io.Source

class CalculationResultControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val normalRoute = routes.CalculationResultController.onPageLoad().url

  "CalculationResult Controller" - {

    "must show the calculation results view on a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationResultsViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
      }
    }

    "must redirect to submit landing page on a POST when answers / calculation are submitted to backend successfully" in {

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any))
        .thenReturn(Future.successful(Success("123")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value.endsWith(
          "/submit-public-pension-adjustment/landing-page?submissionUniqueId=123"
        ) mustBe true
      }
    }

    "must redirect to journey recovery on a POST when answers / calculation submission fails" in {

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any))
        .thenReturn(Future.successful(Failure(Seq("someError"))))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe "/public-pension-adjustment/there-is-a-problem"
      }
    }

    def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
      val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
      val json: JsValue  = Json.parse(source)
      json.as[CalculationResponse]
    }
  }
}
