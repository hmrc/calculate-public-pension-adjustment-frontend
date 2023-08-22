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

package controllers.annualallowance.preaaquestions

import base.SpecBase
import controllers.routes
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.PayTaxCharge1516Page
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.checkAnswers.annualallowance.preaaquestions.PayTaxCharge1516Summary
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAASetupAnswersView
import play.api.inject.bind

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

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          false,
          "checkYourAnswers.aa.subHeading",
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

    "must return maybePensionInputAmounts false when user answers true to PayTaxCharge1516 Page" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers.set(PayTaxCharge1516Page, true).success.value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAASetupAnswersView]

        val expectedSeq = Seq(PayTaxCharge1516Summary.row(ua)(messages(application))).flatten

        val list = SummaryListViewModel(expectedSeq)

        val pIAList = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          maybePensionInputAmounts = false,
          "checkYourAnswers.aa.subHeading",
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

    "must return maybePensionInputAmounts true when user answers false to PayTaxCharge1516 Page" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers.set(PayTaxCharge1516Page, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAASetupAnswersView]

        val expectedSeq = Seq(PayTaxCharge1516Summary.row(ua)(messages(application))).flatten

        val list = SummaryListViewModel(expectedSeq)

        val pIAList = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          maybePensionInputAmounts = true,
          "checkYourAnswers.aa.subHeading",
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

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad().url
        )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
