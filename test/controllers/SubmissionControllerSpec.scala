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

package controllers

import base.SpecBase
import models.Done
import models.submission.Success
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import services.{CalculationResultService, UserDataService}

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val storeAndRedirectRoute = routes.SubmissionController.storeAndRedirect().url

  "Submission Controller" - {

    "Must submit answers with no calculation and redirect to submit frontend when backend call succeeds" in {

      val mockUserDataService = mock[UserDataService]
      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val mockCalculationResultService: CalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.submitUserAnswersWithNoCalculation(any))
        .thenReturn(Future.successful(Success("someId")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, storeAndRedirectRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value must endWith(
          "/submit-public-pension-adjustment/landing-page?submissionUniqueId=someId"
        )
      }
    }
  }
}
