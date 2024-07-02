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
import services.UserDataService
import viewmodels.checkAnswers.ClaimingTaxReliefPensionSummary
import viewmodels.checkAnswers.annualallowance.taxyear.{AdjustedIncomeSummary, AmountClaimedOnOverseasPensionSummary, AmountFlexibleRemunerationArrangementsSummary, AmountSalarySacrificeArrangementsSummary, AnyLumpSumDeathBenefitsSummary, AnySalarySacrificeArrangementsSummary, AreYouNonDomSummary, BlindAllowanceSummary, BlindPersonsAllowanceAmountSummary, ClaimingTaxReliefPensionNotAdjustedIncomeSummary, DefinedBenefit2016PostAmountSummary, DefinedBenefit2016PreAmountSummary, DefinedContribution2016PostAmountSummary, DefinedContribution2016PreAmountSummary, DefinedContributionAmountSummary, DidYouContributeToRASSchemeSummary, DoYouKnowPersonalAllowanceSummary, FlexiAccessDefinedContributionAmountSummary, FlexibleRemunerationArrangementsSummary, HasReliefClaimedOnOverseasPensionSummary, HowMuchContributionPensionSchemeSummary, HowMuchTaxReliefPensionSummary, KnowAdjustedAmountSummary, LumpSumDeathBenefitsValueSummary, PersonalAllowanceSummary, RASContributionAmountSummary, TaxReliefSummary, ThresholdIncomeSummary, TotalIncomeSummary}
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
          list,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return only relevant rows when not 2016 periood and flexi date =/= enddate and the correct view for a GET" in {

      val mockUserDataService = mock[UserDataService]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2023, 4, 3))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
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
            bind[UserDataService].toInstance(mockUserDataService)
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
        val sequenceWithFlexiAmount = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          sequenceWithFlexiAmount,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return relevant rows only for when flexi date == period end date but not 2016 period and the correct view for a GET" in {

      val mockUserDataService = mock[UserDataService]

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2023, 4, 5))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(100))
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
            bind[UserDataService].toInstance(mockUserDataService)
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
        val sequenceWithoutFlexiAmount = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          sequenceWithoutFlexiAmount,
          "checkYourAnswers.aa.period.subHeading.2023",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return relevant rows when period is 2016 but flexi date =/= sub period end dates with the correct view for a GET" in {

      val mockUserDataService = mock[UserDataService]

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
            bind[UserDataService].toInstance(mockUserDataService)
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
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          summarySequence2016,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return relevant rows when period is 2016 and flexi date is pre sub period end dates with the correct view for a GET" in {

      val mockUserDataService = mock[UserDataService]

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
        .set(DefinedContributionAmountPage(Period._2023), BigInt(100))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
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
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          summarySequence2016,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return relevant rows when period is 2016 and flexi date is post sub period end dates with the correct view for a GET" in {

      val mockUserDataService = mock[UserDataService]

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
        .set(DefinedContributionAmountPage(Period._2023), BigInt(100))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
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
        val summarySequence2016 = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          summarySequence2016,
          "checkYourAnswers.aa.period.subHeading.2016",
          controllers.routes.TaskListController.onPageLoad()
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return relevant answers for income sub journey user answers" in {

      val mockUserDataService = mock[UserDataService]

      val userAnswers = incomeSubJourneyData

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2022).url
        )

        val result = route(application, request).value
        val view   = application.injector.instanceOf[CheckYourAAPeriodAnswersView]

        val expectedSeq = Seq(
          ThresholdIncomeSummary.row(userAnswers, Period._2022)(messages(application)),
          TotalIncomeSummary.row(userAnswers, Period._2022)(messages(application)),
          AnySalarySacrificeArrangementsSummary.row(userAnswers, Period._2022)(messages(application)),
          AmountSalarySacrificeArrangementsSummary.row(userAnswers, Period._2022)(messages(application)),
          FlexibleRemunerationArrangementsSummary.row(userAnswers, Period._2022)(messages(application)),
          AmountFlexibleRemunerationArrangementsSummary.row(userAnswers, Period._2022)(messages(application)),
          DidYouContributeToRASSchemeSummary.row(userAnswers, Period._2022)(messages(application)),
          RASContributionAmountSummary.row(userAnswers, Period._2022)(messages(application)),
          AnyLumpSumDeathBenefitsSummary.row(userAnswers, Period._2022)(messages(application)),
          LumpSumDeathBenefitsValueSummary.row(userAnswers, Period._2022)(messages(application)),
          ClaimingTaxReliefPensionSummary.row(userAnswers, Period._2022)(messages(application)),
          TaxReliefSummary.row(userAnswers, Period._2022)(messages(application)),
          KnowAdjustedAmountSummary.row(userAnswers, Period._2022)(messages(application)),
          AdjustedIncomeSummary.row(userAnswers, Period._2022)(messages(application)),
          ClaimingTaxReliefPensionNotAdjustedIncomeSummary.row(userAnswers, Period._2022)(messages(application)),
          HowMuchTaxReliefPensionSummary.row(userAnswers, Period._2022)(messages(application)),
          AreYouNonDomSummary.row(userAnswers, Period._2022)(messages(application)),
          HasReliefClaimedOnOverseasPensionSummary.row(userAnswers, Period._2022)(messages(application)),
          AmountClaimedOnOverseasPensionSummary.row(userAnswers, Period._2022)(messages(application)),
          DoYouKnowPersonalAllowanceSummary.row(userAnswers, Period._2022)(messages(application)),
          PersonalAllowanceSummary.row(userAnswers, Period._2022)(messages(application)),
          BlindAllowanceSummary.row(userAnswers, Period._2022)(messages(application)),
          BlindPersonsAllowanceAmountSummary.row(userAnswers, Period._2022)(messages(application))
        ).flatten

        val incomeSubJourneySummarySequence = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          incomeSubJourneySummarySequence,
          "checkYourAnswers.aa.period.subHeading.2022",
          controllers.routes.TaskListController.onPageLoad()
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
