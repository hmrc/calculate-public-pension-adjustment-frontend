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

package controllers.auth

import base.SpecBase
import config.FrontendAppConfig
import models.{Done, ReportingChange, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.setupquestions.ReportingChangePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuditService, SubmissionDataService, UserDataService}

import java.net.URLEncoder
import scala.concurrent.Future

class AuthControllerSpec extends SpecBase with MockitoSugar {

  val userAnswers =
    UserAnswers(userAnswersId).set(ReportingChangePage, ReportingChange.values.toSet).success.value

  "signOut" - {

    "must clear user answers and redirect to sign out, specifying the exit survey as the continue URL" in {

      val mockAuditService = mock[AuditService]

      when(mockAuditService.auditAuthenticatedUserSignOut(any())(any())) thenReturn Future.successful

      val application =
        applicationBuilder(Some(userAnswers.copy(authenticated = true)), true)
          .overrides(bind[AuditService].toInstance(mockAuditService))
          .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.signOut().url)

        val result = route(application, request).value

        val encodedContinueUrl  =
          URLEncoder.encode("http://localhost:12804/public-pension-adjustment/account/signed-out", "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
      }
    }
  }

  "signOutUnauthorised" - {

    "must redirect to sign out, specifying the exit survey as the continue URL" in {

      val application = applicationBuilder(None).build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.signOutUnauthorised().url)

        val result = route(application, request).value

        val encodedContinueUrl  = URLEncoder.encode(appConfig.redirectToStartPage, "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
      }
    }
  }

  "sessionTimeout" - {

    val mockUserDataService       = mock[UserDataService]
    val mockSubmissionDataService = mock[SubmissionDataService]

    when(mockUserDataService.clear()(any())) thenReturn Future.successful(Done)
    when(mockSubmissionDataService.clear()(any())) thenReturn Future.successful(Done)

    "must redirect to sign out page if authenticated" in {

      val application =
        applicationBuilder(None, userIsAuthenticated = true)
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.sessionTimeout().url)
        val result    = route(application, request).value

        val encodedContinueUrl  =
          URLEncoder.encode("http://localhost:12804/public-pension-adjustment/account/signed-out", "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
      }
    }

    "must clear user answers and redirect to exit Survey if not authenticated" in {

      val application =
        applicationBuilder(Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService)
          )
          .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.sessionTimeout().url)

        val result = route(application, request).value

        val expectedRedirectUrl = appConfig.exitSurveyUrl

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
        verify(mockUserDataService, times(1)).clear()(any())
        verify(mockSubmissionDataService, times(1)).clear()(any())
      }
    }
  }
}
