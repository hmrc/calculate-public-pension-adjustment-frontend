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
import config.FrontendAppConfig
import forms.PreviousClaimContinueFormProvider
import models.{Done, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PreviousClaimContinuePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmissionDataService, SubmitBackendService, UserDataService}
import views.html.PreviousClaimContinueView

import scala.concurrent.Future

class PreviousClaimContinueControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PreviousClaimContinueFormProvider()
  val form         = formProvider()

  lazy val previousClaimContinueRoute = routes.PreviousClaimContinueController.onPageLoad().url

  "PreviousClaimContinue Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, previousClaimContinueRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PreviousClaimContinueView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form)(request, messages(application)).toString
      }
    }

    "must not populate the view on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PreviousClaimContinuePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, previousClaimContinueRoute)

        val view = application.injector.instanceOf[PreviousClaimContinueView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data true is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, previousClaimContinueRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.PreviousClaimContinueController.redirect().url
      }
    }

    "must redirect to the next page when valid data false is submitted" in {
      val mockUserDataService       = mock[UserDataService]
      val mockSubmitBackendService  = mock[SubmitBackendService]
      val mockSubmissionDataService = mock[SubmissionDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendService.clearUserAnswers()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendService.clearCalcUserAnswers()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendService.clearSubmissions()(any())) thenReturn Future.successful(Done)
      when(mockSubmissionDataService.clear()(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .overrides(bind[SubmissionDataService].toInstance(mockSubmissionDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, previousClaimContinueRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, previousClaimContinueRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[PreviousClaimContinueView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm)(request, messages(application)).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, previousClaimContinueRoute)

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
          FakeRequest(POST, previousClaimContinueRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "Redirect()" - {
      "must redirect to the new calculation results page when calculationReviewEnabled flag true and submissions are present" in {
        val mockSubmitBackendService = mock[SubmitBackendService]
        val mockFrontEndAppConfig    = mock[FrontendAppConfig]

        when(mockSubmitBackendService.submissionsPresentInSubmissionService(any())(any()))
          .thenReturn(Future.successful(true))

        when(mockFrontEndAppConfig.calculationReviewEnabled).thenReturn(true)

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
            .overrides(bind[FrontendAppConfig].toInstance(mockFrontEndAppConfig))
            .build()

        running(application) {
          val appConfig = application.injector.instanceOf[FrontendAppConfig]
          val request   = FakeRequest(GET, routes.PreviousClaimContinueController.redirect().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual s"${appConfig.submitFrontend}/calculation-results"
        }
      }

      "must redirect to the old calculation results page when calculationReviewEnabled flag false and submissions are present" in {
        val mockSubmitBackendService = mock[SubmitBackendService]
        val mockFrontEndAppConfig    = mock[FrontendAppConfig]

        when(mockSubmitBackendService.submissionsPresentInSubmissionService(any())(any()))
          .thenReturn(Future.successful(true))

        when(mockFrontEndAppConfig.calculationReviewEnabled).thenReturn(false)

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
            .overrides(bind[FrontendAppConfig].toInstance(mockFrontEndAppConfig))
            .build()

        running(application) {
          val appConfig = application.injector.instanceOf[FrontendAppConfig]
          val request   = FakeRequest(GET, routes.PreviousClaimContinueController.redirect().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual s"${appConfig.submitFrontend}/calculation-result"
        }
      }

      "must redirect to the Task List page when no submissions are present" in {
        val mockSubmitBackendService = mock[SubmitBackendService]

        when(mockSubmitBackendService.submissionsPresentInSubmissionService(any())(any()))
          .thenReturn(Future.successful(false))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
            .build()

        running(application) {
          val request = FakeRequest(GET, routes.PreviousClaimContinueController.redirect().url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad.url
        }
      }
    }
  }

}
