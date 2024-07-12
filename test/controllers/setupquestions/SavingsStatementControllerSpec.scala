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

package controllers.setupquestions

import base.SpecBase
import controllers.setupquestions.{routes => setupRoutes}
import forms.SavingsStatementFormProvider
import models.{Done, NormalMode, SubmissionStatusResponse, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.setupquestions.SavingsStatementPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CalculateBackendService, SubmitBackendService, UserDataService}
import views.html.setupquestions.SavingsStatementView

import java.time.Instant
import scala.concurrent.Future

class SavingsStatementControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new SavingsStatementFormProvider()
  val form         = formProvider()

  lazy val savingsStatementNormalRoute = setupRoutes.SavingsStatementController.onPageLoad(NormalMode).url

  "SavingsStatementController" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, savingsStatementNormalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SavingsStatementView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must return OK for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, savingsStatementNormalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
      }
    }

    "must redirect to the next page when valid data is submitted test and no previous user answers" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(SavingsStatementPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, savingsStatementNormalRoute)

        val view = application.injector.instanceOf[SavingsStatementView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[SavingsStatementView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is true & submissionsPresentInSubmissionServiceWithId is true" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) thenReturn
        Future.successful(Some(SubmissionStatusResponse("id", true)))
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) thenReturn
        Future.successful(true)

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)
          .set(SavingsStatementPage, true)
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is true & recordsPresentInSubmissionService is false" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) thenReturn
        Future.successful(Some(SubmissionStatusResponse("id", true)))
      when(mockUserDataService.updateSubmissionStatus(any())(any())) thenReturn
        Future.successful(Done)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) thenReturn
        Future.successful(false)
      when(mockSubmitBackendService.clearUserAnswers()(any())) thenReturn
        Future.successful(Done)
      when(mockSubmitBackendService.clearSubmissions()(any())) thenReturn
        Future.successful(Done)

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)
          .set(SavingsStatementPage, true)
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be("/public-pension-adjustment/previous-claim-continue")
      }
    }

    "must redirect to the continue previous claim when submission started is false" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) thenReturn Future.successful(
        Some(SubmissionStatusResponse("id", false))
      )

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)
          .set(SavingsStatementPage, true)
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withSession()
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get must be(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is none & submissionsPresentInSubmissionServiceWithId is true" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) thenReturn
        Future.successful(None)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) thenReturn
        Future.successful(true)

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)
          .set(SavingsStatementPage, true)
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to change-previous-adjustment when submission started is none & submissionsPresentInSubmissionServiceWithId is false" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) thenReturn
        Future.successful(None)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) thenReturn
        Future.successful(false)

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)
          .set(SavingsStatementPage, true)
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, savingsStatementNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be("/public-pension-adjustment/change-previous-adjustment")
      }
    }

    // TODO Revisit Unit test
//    "must call updateUserAnswersFromCalcUA when authenticated and no existing user answers" in {
//      val mockCalculateBackendService = mock[CalculateBackendService]
//      when(mockCalculateBackendService.updateUserAnswersFromCalcUA(any())(any())) thenReturn Future.successful(Done)
//
//      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = true)
//        .overrides(
//          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
//        )
//        .build()
//
//      running(application) {
//        val request = AuthenticatedIdentifierRequest(FakeRequest(GET, savingsStatementNormalRoute), "id")
//        val result  = route(application, request).value
//
//        status(result) mustEqual OK
//        verify(mockCalculateBackendService, times(1)).updateUserAnswersFromCalcUA(any())(any())
//      }
//    }

    "must not call updateUserAnswersFromCalcUA when not authenticated" in {
      val mockCalculateBackendService = mock[CalculateBackendService]

      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = false)
        .overrides(
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, savingsStatementNormalRoute)
        val result  = route(application, request).value

        status(result) mustEqual OK
        verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
      }
    }

    "must not call updateUserAnswersFromCalcUA when there are existing user answers" in {
      val mockCalculateBackendService = mock[CalculateBackendService]
      val userAnswers                 = UserAnswers(userAnswersId, Json.obj())

      val application = applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
        .overrides(
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, savingsStatementNormalRoute).withSession("authToken" -> "some-auth-token")
        val result  = route(application, request).value

        status(result) mustEqual OK
        verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
      }
    }
  }
}
