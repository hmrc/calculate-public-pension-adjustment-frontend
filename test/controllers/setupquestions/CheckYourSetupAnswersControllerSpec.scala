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
import config.FrontendAppConfig
import models.{AAKickOutStatus, Done, NormalMode, ReportingChange}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.setupquestions.ReportingChangePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import viewmodels.checkAnswers.setupquestions.ReportingChangeSummary
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourSetupAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          "checkYourAnswers.setup.subHeading",
          controllers.routes.TaskListController.onPageLoad(),
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(GET, controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to ScottishTaxpayerFrom2016Controller if reporting page has indicated AA and Scottsih tax payer page has not been answered and AA eligible" in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head))
        .success
        .value
        .set(AAKickOutStatus(), 2)
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
          FakeRequest(GET, controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val list = SummaryListViewModel(Seq(ReportingChangeSummary.row(userAnswers)(messages(application))).flatten)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          "checkYourAnswers.setup.subHeading",
          controllers.annualallowance.preaaquestions.routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode),
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to task list controller if reporting page has indicated AA and Scottsih tax payer page has been answered and AA eligible " in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head))
        .success
        .value
        .set(AAKickOutStatus(), 2)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, false)
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
          FakeRequest(GET, controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val list = SummaryListViewModel(Seq(ReportingChangeSummary.row(userAnswers)(messages(application))).flatten)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          "checkYourAnswers.setup.subHeading",
          controllers.routes.TaskListController.onPageLoad(),
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }
  }
}
