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
import scala.util.Random

class PensionProtectedMemberPageSpec extends PageBehaviours {

  "PensionProtectedMemberPage" - {

    beRetrievable[Boolean](PensionProtectedMemberPage)

    beSettable[Boolean](PensionProtectedMemberPage)

    beRemovable[Boolean](PensionProtectedMemberPage)
  }

  "normal mode" - {

    "must go to AA Kickout when yes and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(PensionProtectedMemberPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "must go to 22/23 PIA > 40K page when yes and RPSS yes" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(PensionProtectedMemberPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-above-annual-allowance-limit")
    }

    "must go to AA charge when no" in {

      val randomBoolean = Random.nextBoolean()
      val userAnswer    = emptyUserAnswers
        .set(SavingsStatementPage, randomBoolean)
        .success
        .value
        .set(PensionProtectedMemberPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswer).url

      checkNavigation(nextPageUrl, "/triage-journey/annual-allowance-charge")
    }

    "must go to journey recovery when no answer" in {

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "when answered no must go to AA charge page" in {

      val userAnswer = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(PensionProtectedMemberPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswer).url

      checkNavigation(nextPageUrl, "/triage-journey/annual-allowance-charge")

    }

    "must go to 22/23 PIA > 40K page when yes and RPSS yes" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(PensionProtectedMemberPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-above-annual-allowance-limit")

    }

    "must go to AA Kickout when yes and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(PensionProtectedMemberPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "when no answer must go to journey recovery" in {

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must clean up AA triage only when false" in {

      val cleanedUserAnswers = PensionProtectedMemberPage
        .cleanup(Some(false), testCalulationServiceData)
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
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
    }

    "must cleanup AA task when true and RPSS false" in {

      val userAnswers = testCalulationServiceData
        .set(SavingsStatementPage, false)
        .success
        .value

      val cleanedUserAnswers = PensionProtectedMemberPage
        .cleanup(Some(true), userAnswers)
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

    "must clean up AA triage only when true and RPSS true" in {

      val cleanedUserAnswers = PensionProtectedMemberPage
        .cleanup(Some(true), testCalulationServiceData)
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
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)

    }
  }
}
