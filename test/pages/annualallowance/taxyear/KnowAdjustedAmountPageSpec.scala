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

class KnowAdjustedAmountPageSpec extends PageBehaviours {

  "KnowAdjustedAmountPage" - {

    beRetrievable[Boolean](KnowAdjustedAmountPage(Period._2018))

    beSettable[Boolean](KnowAdjustedAmountPage(Period._2018))

    beRemovable[Boolean](KnowAdjustedAmountPage(Period._2018))

    "Normal mode" - {

      "to AdjustedIncome when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            true
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/adjusted-income")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when false, when threshold income is idk" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.IDoNotKnow
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/claiming-tax-relief-pension")
      }

      "to check if user has availed lump sum death benefit, when threshold income is yes" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.Yes
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/any-lump-sum-death-benefits")
      }

      "to JourneyRecovery when false, when threshold income is anything else" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.No
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "to AdjustedIncome when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            true
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/change-adjusted-income")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when false, when threshold income is idk" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.IDoNotKnow
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/claiming-tax-relief-pension")
      }

      "to any AnyLumpSumDeathBenefit page when false, when threshold income is yes" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.Yes
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/any-lump-sum-death-benefits")
      }

      "to JourneyRecovery when false, when threshold income is anything else" in {
        val ua = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2018),
            ThresholdIncome.No
          )
          .success
          .value
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value

        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "when user answers yes" - {

        "if threshold income = yes, should clean up correctly" in {

          val userAnswers = emptyUserAnswers
            .set(ThresholdIncomePage(Period._2018), ThresholdIncome.Yes)
            .success
            .value
            .set(ClaimingTaxReliefPensionPage(Period._2018), true)
            .success
            .value
            .set(TaxReliefPage(Period._2018), BigInt(1))
            .success
            .value
            .set(DidYouContributeToRASSchemePage(Period._2018), true)
            .success
            .value
            .set(RASContributionAmountPage(Period._2018), BigInt(1))
            .success
            .value
            .set(ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018), true)
            .success
            .value
            .set(HowMuchTaxReliefPensionPage(Period._2018), BigInt(1))
            .success
            .value
            .set(HowMuchContributionPensionSchemePage(Period._2018), BigInt(1))
            .success
            .value
            .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), true)
            .success
            .value
            .set(AmountClaimedOnOverseasPensionPage(Period._2018), BigInt(1))
            .success
            .value

          val cleanedUserAnswers = KnowAdjustedAmountPage(Period._2018)
            .cleanup(Some(true), userAnswers)
            .success
            .value

          cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(Period._2018)) `mustBe` Some(true)
          cleanedUserAnswers.get(TaxReliefPage(Period._2018)) `mustBe` Some(BigInt(1))
          cleanedUserAnswers.get(DidYouContributeToRASSchemePage(Period._2018)) `mustBe` Some(true)
          cleanedUserAnswers.get(RASContributionAmountPage(Period._2018)) `mustBe` Some(BigInt(1))
          cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HowMuchContributionPensionSchemePage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(Period._2018)) `mustBe` None

        }

        "if threshold income = idk, should cleanup correctly" in {

          val userAnswers = emptyUserAnswers
            .set(ThresholdIncomePage(Period._2018), ThresholdIncome.IDoNotKnow)
            .success
            .value
            .set(AnyLumpSumDeathBenefitsPage(Period._2018), true)
            .success
            .value
            .set(LumpSumDeathBenefitsValuePage(Period._2018), BigInt(1))
            .success
            .value
            .set(ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018), true)
            .success
            .value
            .set(HowMuchTaxReliefPensionPage(Period._2018), BigInt(1))
            .success
            .value
            .set(HowMuchContributionPensionSchemePage(Period._2018), BigInt(1))
            .success
            .value
            .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), true)
            .success
            .value
            .set(AmountClaimedOnOverseasPensionPage(Period._2018), BigInt(1))
            .success
            .value

          val cleanedUserAnswers = KnowAdjustedAmountPage(Period._2018)
            .cleanup(Some(true), userAnswers)
            .success
            .value

          cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(Period._2018)) `mustBe` Some(true)
          cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(Period._2018)) `mustBe` Some(BigInt(1))
          cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HowMuchContributionPensionSchemePage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(Period._2018)) `mustBe` None
          cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(Period._2018)) `mustBe` None

        }
      }

      "when user answers no" - {

        "should cleanup correctly" in {

          val userAnswers = emptyUserAnswers
            .set(ThresholdIncomePage(Period._2018), ThresholdIncome.IDoNotKnow)
            .success
            .value
            .set(AnyLumpSumDeathBenefitsPage(Period._2018), true)
            .success
            .value
            .set(LumpSumDeathBenefitsValuePage(Period._2018), BigInt(1))
            .success
            .value
            .set(AdjustedIncomePage(Period._2018), BigInt(1))
            .success
            .value

          val cleanedUserAnswers = KnowAdjustedAmountPage(Period._2018)
            .cleanup(Some(false), userAnswers)
            .success
            .value

          cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(Period._2018)) `mustBe` Some(true)
          cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(Period._2018)) `mustBe` Some(BigInt(1))
          cleanedUserAnswers.get(AdjustedIncomePage(Period._2018)) `mustBe` None

        }
      }
    }
  }
}
