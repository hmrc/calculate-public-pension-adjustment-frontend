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
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{CalculateBackendService, SubmitBackendService, UserDataService}

import java.time.Instant
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val indexControllerRoute = routes.IndexController.onPageLoad().url

  "Index Controller" - {

    "must redirect to the option sign in controller when user not authenticated" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, indexControllerRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` controllers.routes.OptionalSignInController
          .onPageLoad()
          .url
      }
    }

    "when authenticated" - {

      "redirect to PreviousClaimContinueController when submission started is true & + submissionsPresentInSubmissionServiceWithId is true" in {

        val mockUserDataService      = mock[UserDataService]
        val mockSubmitBackendService = mock[SubmitBackendService]

        when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
        when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
          Future.successful(Some(SubmissionStatusResponse("id", true)))
        when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
          Future.successful(true)

        val userAnswers =
          UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService),
              bind[SubmitBackendService].toInstance(mockSubmitBackendService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, indexControllerRoute)

          val result = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          redirectLocation(result).get `must` `be`(
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

        val application =
          applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService),
              bind[SubmitBackendService].toInstance(mockSubmitBackendService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(GET, indexControllerRoute)

          val result = route(application, request).value

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

        val application =
          applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(GET, indexControllerRoute)
              .withSession()

          val result = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          redirectLocation(result).get `must` `be`("/public-pension-adjustment/previous-claim-continue")

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

        val application =
          applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService),
              bind[SubmitBackendService].toInstance(mockSubmitBackendService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(GET, indexControllerRoute)
              .withSession()

          val result = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          redirectLocation(result).get `must` `be`(
            s"/public-pension-adjustment/previous-claim-continue"
          )
        }
      }

      "must redirect to change-previous-adjustment when submission started is none & submissionsPresentInSubmissionServiceWithId is false" in {

        val mockUserDataService      = mock[UserDataService]
        val mockSubmitBackendService = mock[SubmitBackendService]

        when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
        when(mockUserDataService.checkSubmissionStatusWithId(any())(any())) `thenReturn`
          Future.successful(None)
        when(mockSubmitBackendService.submissionsPresentInSubmissionServiceWithId(any())(any())) `thenReturn`
          Future.successful(false)

        val userAnswers =
          UserAnswers(userAnswersId, Json.obj(), "uniqueId", Instant.now, true)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers), userIsAuthenticated = true)
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService),
              bind[SubmitBackendService].toInstance(mockSubmitBackendService)
            )
            .build()

        running(application) {
          val request =
            FakeRequest(GET, indexControllerRoute)
              .withSession()

          val result = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          redirectLocation(result).get `must` `be`("/public-pension-adjustment/change-previous-adjustment")
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
          val request = FakeRequest(GET, indexControllerRoute).withSession("authToken" -> "some-auth-token")
          val result  = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
        }
      }
    }

    "not authenticated" - {

      "must not call updateUserAnswersFromCalcUA" in {
        val mockCalculateBackendService = mock[CalculateBackendService]

        val application = applicationBuilder(userAnswers = None, userIsAuthenticated = false)
          .overrides(
            bind[CalculateBackendService].toInstance(mockCalculateBackendService)
          )
          .build()

        running(application) {
          val request = FakeRequest(GET, indexControllerRoute)
          val result  = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          verify(mockCalculateBackendService, times(0)).updateUserAnswersFromCalcUA(any())(any())
        }
      }
    }
  }
}
