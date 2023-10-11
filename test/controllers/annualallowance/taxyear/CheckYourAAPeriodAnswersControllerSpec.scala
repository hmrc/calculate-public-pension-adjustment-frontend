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

package controllers.annualallowance.taxyear

import base.SpecBase
import models.Period
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView
import controllers.routes
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContributionAmountPage, FlexiAccessDefinedContributionAmountPage}
import play.api.inject.bind
import repositories.SessionRepository
import viewmodels.checkAnswers.annualallowance.taxyear.{DefinedContributionAmountSummary, FlexiAccessDefinedContributionAmountSummary}
import views.html.annualallowance.taxyear.CheckYourAAPeriodAnswersView

import java.time.LocalDate

class CheckYourAAPeriodAnswersControllerSpec extends SpecBase with SummaryListFluency {
  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2023).url
        )

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAAPeriodAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          false,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          list,
          list,
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus false when fleixble access date not end of period and the correct view for a GET" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2023, 4, 3))
        .success
        .value
        .set(DefinedContributionAmountPage(Period._2023), BigInt(100))
        .success
        .value
        .set(FlexiAccessDefinedContributionAmountPage(Period._2023), BigInt(100))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2023).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq             = Seq(
          DefinedContributionAmountSummary.row(ua, Period._2023)(messages(application)),
          FlexiAccessDefinedContributionAmountSummary.row(ua, Period._2023)(messages(application))
        ).flatten
        val emptySequence           = SummaryListViewModel(Seq.empty)
        val sequenceWithFlexiAmount = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          maybeFlexiPeriodEndDateRowsStatus = false,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          sequenceWithFlexiAmount,
          emptySequence
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus true when fleixble access date is end of period and the correct view for a GET" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2023, 4, 5))
        .success
        .value
        .set(DefinedContributionAmountPage(Period._2023), BigInt(100))
        .success
        .value
        .set(FlexiAccessDefinedContributionAmountPage(Period._2023), BigInt(0))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2023).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq                = Seq(DefinedContributionAmountSummary.row(ua, Period._2023)(messages(application))).flatten
        val emptySequence              = SummaryListViewModel(Seq.empty)
        val sequenceWithoutFlexiAmount = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          maybeFlexiPeriodEndDateRowsStatus = true,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          emptySequence,
          sequenceWithoutFlexiAmount
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
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2023).url
        )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
