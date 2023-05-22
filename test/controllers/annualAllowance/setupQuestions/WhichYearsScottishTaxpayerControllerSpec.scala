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

package controllers.annualAllowance.setupQuestions

import base.SpecBase
import forms.annualAllowance.setupQuestions.WhichYearsScottishTaxpayerFormProvider
import models.{CheckMode, NormalMode, UserAnswers, WhichYearsScottishTaxpayer}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualAllowance.setupQuestions.WhichYearsScottishTaxpayerPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualAllowance.setupQuestions.WhichYearsScottishTaxpayerView
import controllers.routes
import controllers.annualAllowance.setupQuestions.{routes => setupAARoutes}

import scala.concurrent.Future

class WhichYearsScottishTaxpayerControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichYearsNormalRoute = setupAARoutes.WhichYearsScottishTaxpayerController.onPageLoad(NormalMode).url
  lazy val whichYearsCheckRoute  = setupAARoutes.WhichYearsScottishTaxpayerController.onPageLoad(CheckMode).url

  val formProvider = new WhichYearsScottishTaxpayerFormProvider()
  val form         = formProvider()

  "WhichYearsScottishTaxpayer Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichYearsNormalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichYearsScottishTaxpayerView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichYearsNormalRoute)

        val view = application.injector.instanceOf[WhichYearsScottishTaxpayerView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(WhichYearsScottishTaxpayer.values.toSet), NormalMode)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted in NormalMode" in {

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
          FakeRequest(POST, whichYearsNormalRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearsScottishTaxpayer.values.head.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual WhichYearsScottishTaxpayerPage
          .navigate(NormalMode, expectedAnswers)
          .url
      }
    }

    "must redirect to the next page when valid data is submitted in CheckMode" in {

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
          FakeRequest(POST, whichYearsCheckRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearsScottishTaxpayer.values.head.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual WhichYearsScottishTaxpayerPage.navigate(CheckMode, expectedAnswers).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichYearsNormalRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhichYearsScottishTaxpayerView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whichYearsNormalRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whichYearsNormalRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearsScottishTaxpayer.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to PayingPublicPensionScheme when valid data is submitted in NormalMode" in {

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
          FakeRequest(POST, whichYearsNormalRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearsScottishTaxpayer.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual setupAARoutes.PayingPublicPensionSchemeController.onPageLoad(NormalMode).url
      }
    }

    "must redirect to CheckYourAnswers when valid data is submitted in CheckMode" in {

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
          FakeRequest(POST, whichYearsCheckRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearsScottishTaxpayer.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }
  }
}
