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

package controllers.annualallowance.taxyear

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import forms.annualallowance.taxyear.{BlindPersonsAllowanceAmountFormProvider, HowMuchAAChargeSchemePaidFormProvider}
import models.{Done, NormalMode, Period, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.BlindPersonsAllowanceAmountPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.annualallowance.taxyear.BlindPersonsAllowanceAmountView
import play.api.i18n.Messages

import scala.concurrent.Future

class BlindPersonsAllowanceAmountControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt("2300")

  val startEndDate = "6 April 2016 to 5 April 2017"

  lazy val blindPersonsAllowanceAmountRoute =
    controllers.annualallowance.taxyear.routes.BlindPersonsAllowanceAmountController
      .onPageLoad(NormalMode, Period._2017)
      .url

  private def formWithMockMessages = {

    val formProvider = new BlindPersonsAllowanceAmountFormProvider()
    formProvider(Period._2017)()
  }

  "BlindPersonsAllowanceAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, blindPersonsAllowanceAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BlindPersonsAllowanceAmountView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(formWithMockMessages, NormalMode, Period._2017, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(BlindPersonsAllowanceAmountPage(Period._2017), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, blindPersonsAllowanceAmountRoute)

        val view = application.injector.instanceOf[BlindPersonsAllowanceAmountView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formWithMockMessages.fill(validAnswer),
          NormalMode,
          Period._2017,
          startEndDate
        )(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, blindPersonsAllowanceAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, blindPersonsAllowanceAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = formWithMockMessages.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[BlindPersonsAllowanceAmountView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2017, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, blindPersonsAllowanceAmountRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, blindPersonsAllowanceAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}