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

package pages.setupquestions.annualallowance

import models.Period.{_2013, _2014, _2015, _2021, _2022}
import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, PensionSchemeDetails, PensionSchemeInputAmounts, SchemeIndex, ThresholdIncome, WhichYearsScottishTaxpayer, WhoPaidAACharge}
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, PIAPreRemedyPage, PayTaxCharge1415Page, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page, StopPayingPublicPensionPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear.{AddAnotherSchemePage, AdjustedIncomePage, AmountOfGiftAidPage, BlindAllowancePage, BlindPersonsAllowanceAmountPage, ClaimingTaxReliefPensionPage, ContributedToDuringRemedyPeriodPage, DefinedBenefitAmountPage, DefinedContributionAmountPage, DidYouContributeToRASSchemePage, DoYouHaveGiftAidPage, DoYouKnowPersonalAllowancePage, FlexiAccessDefinedContributionAmountPage, HowMuchAAChargeSchemePaidPage, HowMuchAAChargeYouPaidPage, KnowAdjustedAmountPage, MemberMoreThanOnePensionPage, OtherDefinedBenefitOrContributionPage, PayAChargePage, PensionSchemeDetailsPage, PensionSchemeInputAmountsPage, PersonalAllowancePage, RASContributionAmountPage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage, WhichSchemePage, WhoPaidAAChargePage}
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class ContributionRefundsPageSpec extends PageBehaviours {

  "ContributionRefundsPage" - {

    beRetrievable[Boolean](ContributionRefundsPage)

    beSettable[Boolean](ContributionRefundsPage)

    beRemovable[Boolean](ContributionRefundsPage)
  }

  "normal mode" - {

    "to aa kickout when no and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "to Net income above 100k 16/17 - 19/20 when yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, true)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-100")
    }

    "to have any PIAs increase 15/16 - 21/22 when no and RPSS yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-amount-increased")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "to aa kickout when no and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "to Net income above 100k 16/17 - 19/20 when yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, true)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-100")
    }

    "to have any PIAs increase 15/16 - 21/22 when no and RPSS yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-amount-increased")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must clean up AA task when false and RPSS fasle" in {

      val userAnswers = testCalulationServiceData
        .set(SavingsStatementPage, false)
        .success
        .value

      val cleanedUserAnswers = ContributionRefundsPage
        .cleanup(Some(false), userAnswers)
        .success
        .value

      // AA Triage Answers
      cleanedUserAnswers.get(HadAAChargePage) mustBe None
      cleanedUserAnswers.get(ContributionRefundsPage) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove100KPage) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KPage) mustBe None
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
      cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
    }

    "must clean up triage only when false and RPSS true" in {

      val cleanedUserAnswers = ContributionRefundsPage
        .cleanup(Some(false), testCalulationServiceData)
        .success
        .value

      // AA Triage Answers
      cleanedUserAnswers.get(NetIncomeAbove100KPage) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KPage) mustBe None
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
      cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
    }

    "must clean up triage only when true " in {

      val cleanedUserAnswers = ContributionRefundsPage
        .cleanup(Some(true), testCalulationServiceData)
        .success
        .value

      // AA Triage Answers
      cleanedUserAnswers.get(NetIncomeAbove100KPage) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KPage) mustBe None
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
      cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
    }
  }
}
