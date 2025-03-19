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

package controllers.annualallowance.preaaquestions

import base.SpecBase
import config.FrontendAppConfig
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.PayTaxCharge1415Page
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import viewmodels.checkAnswers.annualallowance.preaaquestions.PayTaxCharge1415Summary
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAASetupAnswersView

class CheckYourAASetupAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAASetupAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          false,
          controllers.routes.TaskListController.onPageLoad(),
          list,
          "checkYourAnswers.aa.pIASubHeading",
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybePensionInputAmounts false when user answers true to PayTaxCharge1415 Page" in {

      val mockUserDataService = mock[UserDataService]

      val ua = emptyUserAnswers.set(PayTaxCharge1415Page, true).success.value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAASetupAnswersView]

        val expectedSeq = Seq(PayTaxCharge1415Summary.row(ua)(messages(application))).flatten

        val list = SummaryListViewModel(expectedSeq)

        val pIAList = SummaryListViewModel(Seq.empty)

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          maybePensionInputAmounts = false,
          controllers.routes.TaskListController.onPageLoad(),
          list,
          "checkYourAnswers.aa.pIASubHeading",
          pIAList
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybePensionInputAmounts true when user answers false to PayTaxCharge1415 Page" in {

      val mockUserDataService = mock[UserDataService]

      val ua = emptyUserAnswers.set(PayTaxCharge1415Page, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAASetupAnswersView]

        val expectedSeq = Seq(PayTaxCharge1415Summary.row(ua)(messages(application))).flatten

        val list = SummaryListViewModel(expectedSeq)

        val pIAList = SummaryListViewModel(Seq.empty)

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          maybePensionInputAmounts = true,
          controllers.routes.TaskListController.onPageLoad(),
          list,
          "checkYourAnswers.aa.pIASubHeading",
          pIAList
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
        val request   = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
