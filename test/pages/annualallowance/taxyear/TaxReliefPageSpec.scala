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

import models.{AboveThreshold, CheckMode, NormalMode, Period, ThresholdIncome}
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class TaxReliefPageSpec extends PageBehaviours {

  val period = Period._2019

  val pre2020Periods  = List(Period._2017, Period._2018, Period._2019)
  val post2019Periods = List(Period._2020, Period._2021, Period._2022, Period._2023)

  "TaxReliefPage" - {

    beRetrievable[BigInt](TaxReliefPage(Period._2018))

    beSettable[BigInt](TaxReliefPage(Period._2018))

    beRemovable[BigInt](TaxReliefPage(Period._2018))

    "Normal mode" - {

      "to do you have gift aid page when period = 2016" in {
        val ua     = emptyUserAnswers
          .set(
            TaxReliefPage(Period._2016),
            BigInt(1)
          )
          .success
          .value
        val result = TaxReliefPage(Period._2016).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/donated-via-gift-aid")
      }

      "when not 2016 period" - {

        "to know adjusted income page when threshold income above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
            .success
            .value
            .set(
              TaxReliefPage(period),
              BigInt(1)
            )
            .success
            .value

          val result = TaxReliefPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")

        }

        "to do you have gift aid page when threshold income not above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.No)
            .success
            .value
            .set(
              TaxReliefPage(period),
              BigInt(1)
            )
            .success
            .value

          val result = TaxReliefPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

        }

        "when not sure if threshold income above threshold" - {

          "when pre2020 period" - {

            "to know adjusted income page when threshold value calculated to be above 110000" in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), true)
                .success
                .value

              val result = TaxReliefPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")
            }

            "to do you have gift aid page when threshold value calculated to be below 110000 " in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), false)
                .success
                .value

              val result = TaxReliefPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

            }
          }

          "when post2019 period" - {

            "to know adjusted income page when threshold value calculated to be above 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), true)
                .success
                .value

              val result = TaxReliefPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")
            }

            "to do you have gift aid page when threshold value calculated to be below 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), false)
                .success
                .value

              val result = TaxReliefPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

            }
          }
        }
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = TaxReliefPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode (return user to next page in normal mode)" - {
      "to do you have gift aid page when period = 2016" in {
        val ua     = emptyUserAnswers
          .set(
            TaxReliefPage(Period._2016),
            BigInt(1)
          )
          .success
          .value
        val result = TaxReliefPage(Period._2016).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/donated-via-gift-aid")
      }

      "when not 2016 period" - {

        "to know adjusted income page when threshold income above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
            .success
            .value
            .set(
              TaxReliefPage(period),
              BigInt(1)
            )
            .success
            .value

          val result = TaxReliefPage(period).navigate(CheckMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")

        }

        "to do you have gift aid page when threshold income not above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ThresholdIncomePage(period), ThresholdIncome.No)
            .success
            .value
            .set(
              TaxReliefPage(period),
              BigInt(1)
            )
            .success
            .value

          val result = TaxReliefPage(period).navigate(CheckMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

        }

        "when not sure if threshold income above threshold" - {

          "when pre2020 period" - {

            "to know adjusted income page when threshold value calculated to be above 110000" in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), true)
                .success
                .value

              val result = TaxReliefPage(period).navigate(CheckMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")
            }

            "to do you have gift aid page when threshold value calculated to be below 110000 " in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), false)
                .success
                .value

              val result = TaxReliefPage(period).navigate(CheckMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

            }
          }

          "when post2019 period" - {

            "to know adjusted income page when threshold value calculated to be above 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), true)
                .success
                .value

              val result = TaxReliefPage(period).navigate(CheckMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/know-adjusted-amount")
            }

            "to do you have gift aid when threshold value calculated to be below 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AboveThreshold(period), false)
                .success
                .value

              val result = TaxReliefPage(period).navigate(CheckMode, ua).url

              checkNavigation(result, s"/annual-allowance/$period/donated-via-gift-aid")

            }
          }
        }
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = TaxReliefPage(period).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2022

        val cleanedUserAnswers = TaxReliefPage(Period._2022)
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
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe Some(true)
        cleanedUserAnswers.get(TaxReliefPage(period)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe None
        cleanedUserAnswers.get(AdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) mustBe None
        cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None
        cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(AmountOfGiftAidPage(period)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(DoYouHaveCodeAdjustmentPage(period)) mustBe None
        cleanedUserAnswers.get(PayeCodeAdjustmentPage(period)) mustBe None
        cleanedUserAnswers.get(CodeAdjustmentAmountPage(period)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(period)) mustBe None
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(period)) mustBe None
      }
    }
  }
}
