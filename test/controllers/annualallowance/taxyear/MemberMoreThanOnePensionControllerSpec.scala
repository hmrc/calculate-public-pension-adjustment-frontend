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

package controllers.annualallowance.taxyear

import base.SpecBase
import controllers.routes
import forms.annualallowance.taxyear.MemberMoreThanOnePensionFormProvider
import models.{CheckMode, NormalMode, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualallowance.taxyear.MemberMoreThanOnePensionView

import scala.concurrent.Future

class MemberMoreThanOnePensionControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new MemberMoreThanOnePensionFormProvider()
  val form         = formProvider()

  lazy val memberMoreThanOnePensionRoute =
    controllers.annualallowance.taxyear.routes.MemberMoreThanOnePensionController
      .onPageLoad(NormalMode, Period._2018)
      .url

  lazy val memberMoreThanOnePensionCheckRoute =
    controllers.annualallowance.taxyear.routes.MemberMoreThanOnePensionController
      .onPageLoad(CheckMode, Period._2018)
      .url

  "MemberMoreThanOnePension Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, memberMoreThanOnePensionRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MemberMoreThanOnePensionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2018)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(MemberMoreThanOnePensionPage(Period._2018), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, memberMoreThanOnePensionRoute)

        val view = application.injector.instanceOf[MemberMoreThanOnePensionView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, Period._2018)(
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
          FakeRequest(POST, memberMoreThanOnePensionRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2018), false)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual MemberMoreThanOnePensionPage(Period._2018)
          .navigate(NormalMode, userAnswers.get)
          .url
      }
    }

    "must redirect to Pension Scheme Details Controller when user is entering first period in NormalMode" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2018), true)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, memberMoreThanOnePensionRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual controllers.annualallowance.taxyear.routes.PensionSchemeDetailsController
          .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
          .url
      }
    }

    "must redirect to Which Scheme Controller when user is entering second or greater period" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2018), true)
        .success
        .value
        .set(MemberMoreThanOnePensionPage(Period._2017), true)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, memberMoreThanOnePensionRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.annualallowance.taxyear.routes.WhichSchemeController
          .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
          .url
      }
    }

    "must redirect to Check AA Period Answers Controller when user resubmits answers from check page" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2018), true)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, memberMoreThanOnePensionCheckRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController
          .onPageLoad(Period._2018)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, memberMoreThanOnePensionRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[MemberMoreThanOnePensionView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2018)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, memberMoreThanOnePensionRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, memberMoreThanOnePensionRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
