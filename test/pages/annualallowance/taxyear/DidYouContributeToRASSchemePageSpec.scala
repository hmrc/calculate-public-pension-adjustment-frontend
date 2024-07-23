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

package pages.annualallowance.taxyear

import models.{CheckMode, NormalMode, Period, ThresholdIncome}
import pages.behaviours.PageBehaviours

class DidYouContributeToRASSchemePageSpec extends PageBehaviours {

  "DidYouContributeToRASSchemePage" - {

    beRetrievable[Boolean](DidYouContributeToRASSchemePage(Period._2018))

    beSettable[Boolean](DidYouContributeToRASSchemePage(Period._2018))

    beRemovable[Boolean](DidYouContributeToRASSchemePage(Period._2018))

    "Normal Mode" - {

      "must redirect to RASContributionAmountPage page when true" in {

        val userAnswers = emptyUserAnswers
          .set(DidYouContributeToRASSchemePage(Period._2018), true)
          .success
          .value

        val result = DidYouContributeToRASSchemePage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/how-much-contribution-relief-at-source")
      }

      "must redirect to AnyLumpSumDeathBenefitsController page when false" in {

        val userAnswers = emptyUserAnswers
          .set(DidYouContributeToRASSchemePage(Period._2018), false)
          .success
          .value

        val result = DidYouContributeToRASSchemePage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/any-lump-sum-death-benefits")
      }

    }

    "Check mode" - {

      "must redirect to RASContributionAmountPage page when true" in {

        val userAnswers = emptyUserAnswers
          .set(DidYouContributeToRASSchemePage(Period._2018), true)
          .success
          .value

        val result = DidYouContributeToRASSchemePage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/how-much-contribution-relief-at-source")
      }

      "must redirect to CYA page when false" in {

        val userAnswers = emptyUserAnswers
          .set(DidYouContributeToRASSchemePage(Period._2018), false)
          .success
          .value

        val result = DidYouContributeToRASSchemePage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/any-lump-sum-death-benefits")
      }
    }

    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2022

        val cleanedUserAnswers = DidYouContributeToRASSchemePage(Period._2022)
          .cleanup(Some(true), incomeSubJourneyData)
          .success
          .value

        cleanedUserAnswers.get(ThresholdIncomePage(period)) mustBe Some(ThresholdIncome.IDoNotKnow)
        cleanedUserAnswers.get(TotalIncomePage(period)) mustBe Some(BigInt(2000))
        cleanedUserAnswers.get(AnySalarySacrificeArrangementsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(AmountSalarySacrificeArrangementsPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(FlexibleRemunerationArrangementsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(period)) mustBe Some(true)
        cleanedUserAnswers.get(RASContributionAmountPage(period)) mustBe None
        cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe None
        cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe None
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe None
        cleanedUserAnswers.get(AdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(AmountOfGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(period)) mustBe None
      }

      "if threshold income page is yes do not clean up claiming tax relief, tax relief amount and know adjusted income pages" in {

        val period = Period._2022

        val cleanedUserAnswers = DidYouContributeToRASSchemePage(period)
          .cleanup(Some(true), incomeSubJourneyDataThresholdIncomeYes)
          .success
          .value

        cleanedUserAnswers.get(ThresholdIncomePage(period)) mustBe Some(ThresholdIncome.Yes)
        cleanedUserAnswers.get(TotalIncomePage(period)) mustBe Some(BigInt(2000))
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe Some(false)
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(period)) mustBe Some(true)
        cleanedUserAnswers.get(RASContributionAmountPage(period)) mustBe None
        cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe None
        cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe None
        cleanedUserAnswers.get(AdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(AmountOfGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(period)) mustBe None
      }
    }
  }
}
