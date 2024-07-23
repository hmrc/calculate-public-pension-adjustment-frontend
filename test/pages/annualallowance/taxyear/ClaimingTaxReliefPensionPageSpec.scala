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
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class ClaimingTaxReliefPensionPageSpec extends PageBehaviours {

  val period = Period._2019

  val pre2020Periods  = List(Period._2017, Period._2018, Period._2019)
  val post2019Periods = List(Period._2020, Period._2021, Period._2022, Period._2023)

  "ClaimingTaxReliefPensionPage" - {

    beRetrievable[Boolean](ClaimingTaxReliefPensionPage(period))

    beSettable[Boolean](ClaimingTaxReliefPensionPage(period))

    beRemovable[Boolean](ClaimingTaxReliefPensionPage(period))
  }

  "Normal mode" - {

    "when user answers true" in {

      val ua = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(Period._2016),
          true
        )
        .success
        .value

      val result = ClaimingTaxReliefPensionPage(Period._2016).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/tax-relief")

    }

    "when user answers false" - {

      "to do you have gift aid page when period = 2016" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionPage(Period._2016),
            false
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionPage(Period._2016).navigate(NormalMode, ua).url

        checkNavigation(result, s"/annual-allowance/2016/contribute-to-relief-at-source-scheme")
      }

      "when not 2016 period" - {

        "to check if user has claimed relief at source page when user has selected above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
            .success
            .value
            .set(ClaimingTaxReliefPensionPage(period), false)
            .success
            .value

          val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/contribute-to-relief-at-source-scheme")

        }

        "to check if user has claimed relief at source page when threshold income not above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.No)
            .success
            .value
            .set(ClaimingTaxReliefPensionPage(period), false)
            .success
            .value

          val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/contribute-to-relief-at-source-scheme")

        }

        "when not sure if threshold income above threshold" - {

          "when pre2020 period" - {

            "to check if user has claimed relief at source page" in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value

              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/contribute-to-relief-at-source-scheme")
            }
          }
        }
      }
    }
  }

  "Check mode (returns user to next page in normal mode)" - {

    "when user answers true" in {

      val ua = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(Period._2016),
          true
        )
        .success
        .value

      val result = ClaimingTaxReliefPensionPage(Period._2016).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/tax-relief")

    }

    "when user answers false" - {

      "to do you have gift aid page when period = 2016" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionPage(Period._2016),
            false
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionPage(Period._2016).navigate(CheckMode, ua).url

        checkNavigation(result, s"/annual-allowance/2016/contribute-to-relief-at-source-scheme")
      }

      "when not 2016 period" - {

        "to check if user has claimed for relief at source" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
            .success
            .value
            .set(ClaimingTaxReliefPensionPage(period), false)
            .success
            .value

          val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/contribute-to-relief-at-source-scheme")

        }
      }
    }
  }

  "cleanup" - {

    "must cleanup correctly" in {

      val period = Period._2022

      val cleanedUserAnswers = ClaimingTaxReliefPensionPage(Period._2022)
        .cleanup(Some(true), incomeSubJourneyData)
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
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe Some(true)
      cleanedUserAnswers.get(TaxReliefPage(period)) mustBe None
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(period)) mustBe None
      cleanedUserAnswers.get(RASContributionAmountPage(period)) mustBe None
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
  }
}
