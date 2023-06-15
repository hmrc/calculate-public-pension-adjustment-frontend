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
import controllers.routes
import forms.lifetimeallowance.LifetimeAllowanceChargeAmountFormProvider
import models.{CheckMode, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.lifetimeallowance.LifetimeAllowanceChargeAmountPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.lifetimeallowance.LifetimeAllowanceChargeAmountView

import scala.concurrent.Future

class LifetimeAllowanceChargeAmountControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new LifetimeAllowanceChargeAmountFormProvider()
  val form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt(0)

  lazy val lifetimeAllowanceChargeAmountRoute =
    controllers.lifetimeallowance.routes.LifetimeAllowanceChargeAmountController.onPageLoad(NormalMode).url

  lazy val lifetimeAllowanceChargeAmountCheckRoute =
    controllers.lifetimeallowance.routes.LifetimeAllowanceChargeAmountController.onPageLoad(CheckMode).url

  "LifetimeAllowanceChargeAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, lifetimeAllowanceChargeAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[LifetimeAllowanceChargeAmountView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(LifetimeAllowanceChargeAmountPage, validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, lifetimeAllowanceChargeAmountRoute)

        val view = application.injector.instanceOf[LifetimeAllowanceChargeAmountView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(
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
          FakeRequest(POST, lifetimeAllowanceChargeAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(LifetimeAllowanceChargeAmountPage, BigInt(1000)).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual LifetimeAllowanceChargeAmountPage
          .navigate(NormalMode, expectedAnswers)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, lifetimeAllowanceChargeAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[LifetimeAllowanceChargeAmountView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, lifetimeAllowanceChargeAmountRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, lifetimeAllowanceChargeAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
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
          FakeRequest(POST, lifetimeAllowanceChargeAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(LifetimeAllowanceChargeAmountPage, BigInt(1000)).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual pages.lifetimeallowance.LifetimeAllowanceChargeAmountPage
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
          FakeRequest(POST, lifetimeAllowanceChargeAmountCheckRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(LifetimeAllowanceChargeAmountPage, BigInt(1000)).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual pages.lifetimeallowance.LifetimeAllowanceChargeAmountPage
          .navigate(CheckMode, expectedAnswers)
          .url
      }
    }
  }
}
