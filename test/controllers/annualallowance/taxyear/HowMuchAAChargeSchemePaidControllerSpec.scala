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
import forms.annualallowance.taxyear.HowMuchAAChargeSchemePaidFormProvider
import models.{CheckMode, NormalMode, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.HowMuchAAChargeSchemePaidPage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualallowance.taxyear.HowMuchAAChargeSchemePaidView

import scala.concurrent.Future

class HowMuchAAChargeSchemePaidControllerSpec extends SpecBase with MockitoSugar {
  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt(0)

  lazy val howMuchAAChargeSchemePaidRoute =
    controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  lazy val howMuchAAChargeSchemePaid2019Route =
    controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
      .onPageLoad(NormalMode, Period._2019, SchemeIndex(0))
      .url

  lazy val howMuchAAChargeSchemePaidCheckRoute =
    controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
      .onPageLoad(CheckMode, Period._2018, SchemeIndex(0))
      .url

  private def formWithMockMessages = {
    val messages = mock[Messages]

    val formProvider = new HowMuchAAChargeSchemePaidFormProvider()
    formProvider("")(messages)
  }

  "HowMuchAAChargeSchemePaid Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howMuchAAChargeSchemePaidRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowMuchAAChargeSchemePaidView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formWithMockMessages,
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          "6 April 2017 and 5 April 2018"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(HowMuchAAChargeSchemePaidPage(Period._2019, SchemeIndex(0)), validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howMuchAAChargeSchemePaid2019Route)

        val view = application.injector.instanceOf[HowMuchAAChargeSchemePaidView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formWithMockMessages.fill(validAnswer),
          NormalMode,
          Period._2019,
          SchemeIndex(0),
          "6 April 2018 and 5 April 2019"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, howMuchAAChargeSchemePaidRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, howMuchAAChargeSchemePaidRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = formWithMockMessages.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[HowMuchAAChargeSchemePaidView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          boundForm,
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          startEndDate = "6 April 2017 and 5 April 2018"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, howMuchAAChargeSchemePaidRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to start of the service for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, howMuchAAChargeSchemePaidRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
