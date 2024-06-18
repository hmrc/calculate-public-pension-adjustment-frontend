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

class TotalIncomePageSpec extends PageBehaviours {

  "TotalIncomePage" - {

    beRetrievable[BigInt](TotalIncomePage(Period._2013))

    beSettable[BigInt](TotalIncomePage(Period._2013))

    beRemovable[BigInt](TotalIncomePage(Period._2013))

    "must Navigate correctly in normal mode" - {
      "to AnySalarySacrificeArrangements page when not in 15/16 and idk is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/any-salary-sacrifice-arrangements")
      }

      "to claiming-tax-relief-pension page when not in 15/16 and yes is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief")
      }

      "to claiming-tax-relief-pension page when not in 15/16 and no is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief")
      }
    }

    "must Navigate correctly in check mode" - {
      "to AnySalarySacrificeArrangements page in normal mode when not in 15/16 and idk is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/any-salary-sacrifice-arrangements")
      }

      "to claiming-tax-relief-pension page in normal mode when not in 15/16 and yes is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/change-claiming-tax-relief")
      }

      "to claiming-tax-relief-pension page in normal mode when not in 15/16 and no is selected on threshold income" in {
        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
          .success
          .value
          .set(
            TotalIncomePage(Period._2017),
            BigInt(100)
          )
          .success
          .value

        val result = TotalIncomePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/change-claiming-tax-relief")
      }
    }

    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2017

        val userAnswers = emptyUserAnswers
          .set(AnySalarySacrificeArrangementsPage(period), true)
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(FlexibleRemunerationArrangementsPage(period), true)
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
          .success
          .value
          .set(AnyLumpSumDeathBenefitsPage(period), true)
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(ClaimingTaxReliefPensionPage(period), true)
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value
          .set(KnowAdjustedAmountPage(period), true)
          .success
          .value
          .set(AdjustedIncomePage(period), BigInt(1))
          .success
          .value
          .set(ClaimingTaxReliefPensionNotAdjustedIncomePage(period), true)
          .success
          .value
          .set(HowMuchTaxReliefPensionPage(period), BigInt(1))
          .success
          .value
          .set(AreYouNonDomPage(period), true)
          .success
          .value
          .set(HasReliefClaimedOnOverseasPensionPage(period), true)
          .success
          .value
          .set(AmountClaimedOnOverseasPensionPage(period), BigInt(1))
          .success
          .value
          .set(DoYouKnowPersonalAllowancePage(period), true)
          .success
          .value
          .set(PersonalAllowancePage(period), BigInt(1))
          .success
          .value
          .set(MarriageAllowancePage(period), true)
          .success
          .value
          .set(MarriageAllowanceAmountPage(period), BigInt(1))
          .success
          .value
          .set(BlindAllowancePage(period), true)
          .success
          .value

        val cleanedUserAnswers = TotalIncomePage(Period._2017)
          .cleanup(Some(BigInt(1000)), userAnswers)
          .success
          .value

        cleanedUserAnswers.get(AnySalarySacrificeArrangementsPage(period)) mustBe None
        cleanedUserAnswers.get(AmountSalarySacrificeArrangementsPage(period)) mustBe None
        cleanedUserAnswers.get(FlexibleRemunerationArrangementsPage(period)) mustBe None
        cleanedUserAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)) mustBe None
        cleanedUserAnswers.get(HowMuchContributionPensionSchemePage(period)) mustBe None
        cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe None
        cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe None
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe None
        cleanedUserAnswers.get(AdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(AreYouNonDomPage(period)) mustBe None
        cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(MarriageAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(MarriageAllowanceAmountPage(period)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(period)) mustBe None
      }
    }
  }
}
