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
import forms.ReportingChangeFormProvider
import models.ReportingChange.{LifetimeAllowance, OtherCompensation}
import models.{CheckMode, NormalMode, ReportingChange, UserAnswers, WhichYearsScottishTaxpayer}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ReportingChangePage, ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ReportingChangeView

import scala.concurrent.Future

class ReportingChangeControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ReportingChangeFormProvider()
  val form         = formProvider()

  lazy val reportingNormalRoute = routes.ReportingChangeController.onPageLoad(NormalMode).url
  lazy val reportingCheckRoute  = routes.ReportingChangeController.onPageLoad(CheckMode).url

  "ReportingChange Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, reportingNormalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ReportingChangeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ReportingChangePage, ReportingChange.values.toSet).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, reportingNormalRoute)

        val view = application.injector.instanceOf[ReportingChangeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(ReportingChange.values.toSet), NormalMode)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted in Normal Mode" in {

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
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(ReportingChangePage, ReportingChange.values.toSet).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ReportingChangePage
          .navigate(NormalMode, expectedAnswers)
          .url
      }
    }

    "must redirect to the next page when valid data is submitted in Check Mode" in {

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
          FakeRequest(POST, reportingCheckRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))

        val result = route(application, request).value

        val expectedAnswers =
          emptyUserAnswers.set(ReportingChangePage, ReportingChange.values.toSet).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ReportingChangePage
          .navigate(CheckMode, expectedAnswers)
          .url
      }
    }

    "must redirect to ScottishTaxpayerFrom2016 when user selects AnnualAllowance in Normal Mode" in {

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
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.AnnualAllowance.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode).url
      }
    }

    "must redirect to CheckYourAnswers when user does not select AnnualAllowance in Normal Mode" in {

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
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.LifetimeAllowance.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }

    "must redirect to CheckYourAnswers when user is in CheckMode and answers exist for ScottishTaxpayerFrom2016Page" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(ReportingChangePage, ReportingChange.values.toSet)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, true)
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
          FakeRequest(POST, reportingCheckRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }

    "must redirect to ScottishTaxpayerFrom2016 when user is in CheckMode and answers do not exist for ScottishTaxpayerFrom2016Page" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(ReportingChangePage, ReportingChange.values.toSet)
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
          FakeRequest(POST, reportingCheckRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ScottishTaxpayerFrom2016Controller.onPageLoad(CheckMode).url
      }
    }

    "must redirect to CheckYourAnswers when user is in CheckMode and user doesn't select annual allowance" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val answers = emptyUserAnswers
        .set(ReportingChangePage, ReportingChange.values.toSet)
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
          FakeRequest(POST, reportingCheckRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.LifetimeAllowance.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ReportingChangeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, reportingNormalRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
