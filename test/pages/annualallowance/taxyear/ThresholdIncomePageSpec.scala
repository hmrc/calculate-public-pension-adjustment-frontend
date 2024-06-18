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

import models.Period._2013
import models.{CheckMode, NormalMode, Period, ThresholdIncome}
import pages.behaviours.PageBehaviours

class ThresholdIncomePageSpec extends PageBehaviours {

  "ThresholdIncomePage" - {

    beRetrievable[ThresholdIncome](ThresholdIncomePage(Period._2013))

    beSettable[ThresholdIncome](ThresholdIncomePage(Period._2013))

    beRemovable[ThresholdIncome](ThresholdIncomePage(Period._2013))

    "Normal mode" - {

      "to AdjustedIncomePage when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2013),
            ThresholdIncome.Yes
          )
          .success
          .value
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/total-income")
      }

      "to TotalIncomePage when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2013),
            ThresholdIncome.No
          )
          .success
          .value
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/total-income")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "must Navigate correctly to total in normal mode when answered no" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.No
          )
          .success
          .value
        val result = ThresholdIncomePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/total-income")
      }

      "must Navigate correctly to CYA in check mode when answered yes" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2017),
            ThresholdIncome.Yes
          )
          .success
          .value
        val result = ThresholdIncomePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/total-income")
      }

      "must navigate to journey recovery when no answer" in {
        val ua     = emptyUserAnswers
        val result = ThresholdIncomePage(_2013).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

    }

    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2017

        val userAnswers = emptyUserAnswers
          .set(TotalIncomePage(period), BigInt(2000))
          .success
          .value
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

        val cleanedUserAnswers = ThresholdIncomePage(Period._2017)
          .cleanup(Some(ThresholdIncome.Yes), userAnswers)
          .success
          .value

        cleanedUserAnswers.get(TotalIncomePage(period)) mustBe None
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
