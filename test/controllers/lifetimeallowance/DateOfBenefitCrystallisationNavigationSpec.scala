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

package controllers.lifetimeallowance

import base.SpecBase
import controllers.lifetimeallowance.{routes => ltaRoutes}
import models.{CheckMode, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class HadBenefitCrystallisationEventNavigationSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val normalRoute = ltaRoutes.HadBenefitCrystallisationEventController.onPageLoad(NormalMode).url
  lazy val checkRoute  = ltaRoutes.HadBenefitCrystallisationEventController.onPageLoad(CheckMode).url

  "HadBenefitCrystallisationEvent Controller" - {

    "must redirect to DateOfBenefitCrystallisationEventController when user selects yes in Normal Mode" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequest(normalRoute, "true")
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ltaRoutes.DateOfBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
          .url
      }
    }

    "must redirect to NotAbleToUseThisServiceLtaController when user selects no in Normal Mode" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequest(normalRoute, "false")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad.url
      }
    }

    "must redirect to CheckYourAnswersController when user selects yes in Check Mode" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequest(checkRoute, "true")
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad.url
      }
    }

    "must redirect to NotAbleToUseThisServiceLta when user selects no in Check Mode" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequest(checkRoute, "false")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad.url
      }
    }
  }

  private def constructApplication(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) = {
    val mockSessionRepository = mock[SessionRepository]

    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

    applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository)
      )
      .build()
  }

  private def aFakePostRequest(route: String, value: String) = {
    val request =
      FakeRequest(POST, route)
        .withFormUrlEncodedBody(("value", value))
    request
  }
}
