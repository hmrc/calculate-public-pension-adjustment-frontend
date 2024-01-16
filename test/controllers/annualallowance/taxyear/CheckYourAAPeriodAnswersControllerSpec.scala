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

package controllers.annualallowance.taxyear

import base.SpecBase
import config.FrontendAppConfig
import models.Period
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedBenefit2016PostAmountPage, DefinedBenefit2016PreAmountPage, DefinedContribution2016PostAmountPage, DefinedContribution2016PostFlexiAmountPage, DefinedContribution2016PreAmountPage, DefinedContribution2016PreFlexiAmountPage, DefinedContributionAmountPage, FlexiAccessDefinedContributionAmountPage}
import play.api.inject.bind
import repositories.SessionRepository
import viewmodels.checkAnswers.annualallowance.taxyear.{DefinedBenefit2016PostAmountSummary, DefinedBenefit2016PreAmountSummary, DefinedContribution2016PostAmountSummary, DefinedContribution2016PreAmountSummary, DefinedContributionAmountSummary, FlexiAccessDefinedContributionAmountSummary}
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
          false,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          list,
          list,
          list,
          list,
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus and is2016Period false when flexi date not end of period and not 2016 and the correct view for a GET" in {

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
          false,
          false,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          sequenceWithFlexiAmount,
          emptySequence,
          emptySequence,
          emptySequence
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus true and is2016Period false when flexi date  end of period but not 2016 and the correct view for a GET" in {

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
          true,
          false,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          emptySequence,
          sequenceWithoutFlexiAmount,
          emptySequence,
          emptySequence
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus false and is2016Period true when flexi date not end of period but is 2016 with correct view for a GET" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedBenefit2016PostAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(100))
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
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq         = Seq(
          DefinedContribution2016PreAmountSummary.row(ua)(messages(application)),
          DefinedContribution2016PostAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PreAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PostAmountSummary.row(ua)(messages(application))
        ).flatten
        val emptySequence       = SummaryListViewModel(Seq.empty)
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          false,
          true,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          emptySequence,
          emptySequence,
          summarySequence2016,
          emptySequence
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus and is2016Period true when flexi end of pre2016 sub period with correct view for a GET" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, Period.pre2016End)
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedBenefit2016PostAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PreFlexiAmountPage, BigInt(0))
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(100))
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
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq         = Seq(
          DefinedContribution2016PreAmountSummary.row(ua)(messages(application)),
          DefinedContribution2016PostAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PreAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PostAmountSummary.row(ua)(messages(application))
        ).flatten
        val emptySequence       = SummaryListViewModel(Seq.empty)
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          true,
          true,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          emptySequence,
          emptySequence,
          emptySequence,
          summarySequence2016
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return maybeFlexiPeriodEndDateRowsStatus and is2016Period true when flexi end of post2016 sub period with correct view for a GET" in {

      val mockSessionRepository = mock[SessionRepository]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, Period.post2016End)
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedBenefit2016PostAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(100))
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(0))
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
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq         = Seq(
          DefinedContribution2016PreAmountSummary.row(ua)(messages(application)),
          DefinedContribution2016PostAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PreAmountSummary.row(ua)(messages(application)),
          DefinedBenefit2016PostAmountSummary.row(ua)(messages(application))
        ).flatten
        val emptySequence       = SummaryListViewModel(Seq.empty)
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          true,
          true,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad(),
          emptySequence,
          emptySequence,
          emptySequence,
          emptySequence,
          summarySequence2016
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
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2023).url
        )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
