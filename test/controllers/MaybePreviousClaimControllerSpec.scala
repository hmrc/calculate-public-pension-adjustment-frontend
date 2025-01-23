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
import models.{Done, SubmissionStatusResponse, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.setupquestions.annualallowance.SavingsStatementPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{CalculateBackendService, SubmitBackendService, UserDataService}

import java.time.Instant
import scala.concurrent.Future
class MaybePreviousClaimControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  "MaybePreviousClaimController" - {

    "must redirect to the correct URL based on the user answers" in {

      val mockUserDataService         = mock[UserDataService]
      val mockSubmitBackendService    = mock[SubmitBackendService]
      val mockCalculateBackendService = mock[CalculateBackendService]

      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn` Future.successful(None)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn` Future
        .successful(
          true
        )

      val userAnswers = UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, authenticated = true)

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[UserDataService].toInstance(mockUserDataService),
          bind[SubmitBackendService].toInstance(mockSubmitBackendService),
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` routes.PreviousClaimContinueController.onPageLoad().url
      }
    }

    // TODO Revisit Unit test
    //    "must update user answers if authenticated and no existing user answers" in {
    //      val mockCalculateBackendService = mock[CalculateBackendService]
    //      when(mockCalculateBackendService.updateUserAnswersFromCalcUA(any())(any())) `thenReturn` Future.successful(Done)
    //
    //      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = true)
    //        .overrides(
    //          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
    //        ).build()
    //
    //      running(application) {
    //        val request = AuthenticatedIdentifierRequest(FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url), "id")
    //        val result = route(application, request).value
    //
    //        status(result) `mustEqual` SEE_OTHER
    //        verify(mockCalculateBackendService, times(1)).updateUserAnswersFromCalcUA(any())(any())
    //      }
    //    }

    "must not update user answers if not authenticated" in {
      val mockCalculateBackendService = mock[CalculateBackendService]

      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = false)
        .overrides(
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
      }
    }

    "must not update user answers if there are existing user answers" in {
      val mockCalculateBackendService = mock[CalculateBackendService]
      val userAnswers                 = UserAnswers(userAnswersId, Json.obj())

      val application = applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
        .overrides(
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
      }
    }

    "must redirect to the next page when valid data is submitted and no previous user answers" in {
      val mockUserDataService         = mock[UserDataService]
      val mockCalculateBackendService = mock[CalculateBackendService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[UserDataService].toInstance(mockUserDataService),
          bind[CalculateBackendService].toInstance(mockCalculateBackendService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is true & submissionsPresentInSubmissionService is true" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
        Future.successful(Some(SubmissionStatusResponse("id", true)))
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
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
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).get must be(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is true & recordsPresentInSubmissionService is false" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
        Future.successful(Some(SubmissionStatusResponse("id", true)))
      when(mockUserDataService.updateSubmissionStatus(any())(any())) `thenReturn`
        Future.successful(Done)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
        Future.successful(false)
      when(mockSubmitBackendService.clearUserAnswers()(any())) `thenReturn`
        Future.successful(Done)
      when(mockSubmitBackendService.clearSubmissions()(any())) `thenReturn`
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
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).get `must` `be`("/public-pension-adjustment/previous-claim-continue")
      }
    }

    "must redirect to the continue previous claim when submission started is false" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn` Future.successful(
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
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        redirectLocation(result).get `must` `be`(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to PreviousClaimContinueController when submission started is none & submissionsPresentInSubmissionServiceWithId is true" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
        Future.successful(None)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
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
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).get `must` `be`(
          s"/public-pension-adjustment/previous-claim-continue"
        )
      }
    }

    "must redirect to resubmitting adjustment page when submission started is none & recordsPresentInSubmissionService is false" in {

      val mockUserDataService      = mock[UserDataService]
      val mockSubmitBackendService = mock[SubmitBackendService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
        Future.successful(None)
      when(mockUserDataService.updateSubmissionStatus(any())(any())) `thenReturn`
        Future.successful(Done)
      when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
        Future.successful(false)
      when(mockSubmitBackendService.clearUserAnswers()(any())) `thenReturn`
        Future.successful(Done)
      when(mockSubmitBackendService.clearSubmissions()(any())) `thenReturn`
        Future.successful(Done)

      val userAnswers =
        UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .overrides(bind[SubmitBackendService].toInstance(mockSubmitBackendService))
          .build()

      running(application) {
        val request = FakeRequest(GET, routes.MaybePreviousClaimController.redirect().url)
        val result  = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).get `must` `be`("/public-pension-adjustment/change-previous-adjustment")
      }
    }
  }
}
