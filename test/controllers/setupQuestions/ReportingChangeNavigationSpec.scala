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

package controllers.setupQuestions

import base.SpecBase
import controllers.routes
import controllers.setupQuestions.{routes => setupRoutes}
import controllers.annualAllowance.setupQuestions.{routes => setupAARoutes}
import forms.setupQuestions.ReportingChangeFormProvider
import models.{CheckMode, NormalMode, ReportingChange, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualAllowance.setupQuestions.ScottishTaxpayerFrom2016Page
import pages.setupQuestions.ReportingChangePage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class ReportingChangeNavigationSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ReportingChangeFormProvider()
  val form         = formProvider()

  lazy val reportingNormalRoute = setupRoutes.ReportingChangeController.onPageLoad(NormalMode).url
  lazy val reportingCheckRoute  = setupRoutes.ReportingChangeController.onPageLoad(CheckMode).url

  "ReportingChange Controller" - {

    "must redirect to the next page when valid data is submitted in Normal Mode" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequestToTheNormalRoute

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

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequestToTheCheckRoute

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

      val application: Application = constructApplication()

      running(application) {
        val request =
          FakeRequest(POST, reportingNormalRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.AnnualAllowance.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual setupAARoutes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode).url
      }
    }

    "must redirect to CheckYourAnswers when user does not select AnnualAllowance in Normal Mode" in {

      val application: Application = constructApplication()

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

      val answers = emptyUserAnswers
        .set(ReportingChangePage, ReportingChange.values.toSet)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, true)
        .success
        .value

      val application: Application = constructApplication(Some(answers))

      running(application) {
        val request = aFakePostRequestToTheCheckRoute

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }

    "must redirect to ScottishTaxpayerFrom2016 when user is in CheckMode and answers do not exist for ScottishTaxpayerFrom2016Page" in {

      val application: Application = constructApplication()

      running(application) {
        val request = aFakePostRequestToTheCheckRoute

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual setupAARoutes.ScottishTaxpayerFrom2016Controller.onPageLoad(CheckMode).url
      }
    }

    "must redirect to CheckYourAnswers when user is in CheckMode and user doesn't select annual allowance" in {

      val application: Application = constructApplication()

      running(application) {
        val request =
          FakeRequest(POST, reportingCheckRoute)
            .withFormUrlEncodedBody(("value[0]", ReportingChange.LifetimeAllowance.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
      }
    }
  }

  private def constructApplication(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) = {
    val mockSessionRepository = mock[SessionRepository]

    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

    applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository)
      )
      .build()
  }

  private def aFakePostRequestToTheCheckRoute = {
    val request =
      FakeRequest(POST, reportingCheckRoute)
        .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))
    request
  }

  private def aFakePostRequestToTheNormalRoute = {
    val request =
      FakeRequest(POST, reportingNormalRoute)
        .withFormUrlEncodedBody(("value[0]", ReportingChange.values.head.toString))
    request
  }
}
