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

class LumpSumDeathBenefitsValuePageSpec extends PageBehaviours {

  "LumpSumDeathBenefitsValuePage" - {

    beRetrievable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    beSettable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    beRemovable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    "Normal mode" - {

      "to ClaimingTaxRelief when answered, when period not 2016, when threshold income is idk" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.IDoNotKnow
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief")
      }

      "to ClaimingTaxReliefPensionNotAdjusted when answered, when period not 2016, when threshold income is yes" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.Yes
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief-pension")
      }

      "to journeyrecovery when answered, when period not 2016, when threshold income is anything else" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.No
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "to ClaimingTaxReliefPension in normal mode when period not 2016 and threshold income is IDoNotKnow" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.IDoNotKnow
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome in normal mode when period not 2016 and threshold income is Yes" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.Yes
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief-pension")
      }

      "to journeyrecovery when answered, when period not 2016, when threshold income is anything else" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.No
          )
          .success
          .value
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(10)
          )
          .success
          .value

        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2022

        val cleanedUserAnswers = LumpSumDeathBenefitsValuePage(Period._2022)
          .cleanup(Some(BigInt(1)), incomeSubJourneyData)
          .success
          .value

        cleanedUserAnswers.get(ThresholdIncomePage(period)) mustBe Some(ThresholdIncome.IDoNotKnow)
        cleanedUserAnswers.get(TotalIncomePage(period)) mustBe Some(BigInt(2000))
        cleanedUserAnswers.get(AnySalarySacrificeArrangementsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(AmountSalarySacrificeArrangementsPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(FlexibleRemunerationArrangementsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe None
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe None
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

      "if threshold income page is yes do not clean up claiming tax relief, tax relief amount and know adjusted income pages" in {

        val period = Period._2022

        val cleanedUserAnswers = LumpSumDeathBenefitsValuePage(period)
          .cleanup(Some(BigInt(1)), incomeSubJourneyDataThresholdIncomeYes)
          .success
          .value

        cleanedUserAnswers.get(ThresholdIncomePage(period)) mustBe Some(ThresholdIncome.Yes)
        cleanedUserAnswers.get(TotalIncomePage(period)) mustBe Some(BigInt(2000))
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(period)) mustBe Some(true)
        cleanedUserAnswers.get(RASContributionAmountPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe Some(false)
        cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe Some(BigInt(1))
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
