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

      "to know personal allowance page when period = 2016" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionPage(Period._2016),
            false
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionPage(Period._2016).navigate(NormalMode, ua).url

        checkNavigation(result, s"/annual-allowance/personal-allowance/2016")
      }

      "when not 2016 period" - {

        "to know adjusted income page when threshold income above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ClaimingTaxReliefPensionPage(period), false)
            .success
            .value
            .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
            .success
            .value

          val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/adjusted-income/know-adjusted-amount/$period")

        }

        "to know personal allowance page when threshold income not above threshold" in {

          val period = Gen.oneOf(pre2020Periods).sample.get

          val ua = emptyUserAnswers
            .set(ClaimingTaxReliefPensionPage(period), false)
            .success
            .value
            .set(ThresholdIncomePage(period), ThresholdIncome.No)
            .success
            .value

          val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/tax-relief")
    }
          checkNavigation(result, s"/annual-allowance/personal-allowance/$period")

        }

        "when not sure if threshold income above threshold" - {

          "when pre2020 period" - {

            "to know adjusted income page when threshold value calculated to be above 110000" in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua     = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TotalIncomePage(period), BigInt(1000000))
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
                .success
                .value
                .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value
              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/adjusted-income/know-adjusted-amount/$period")
            }

            "to know personal allowance page when threshold value calculated to be below 110000 " in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua     = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TotalIncomePage(period), BigInt(100))
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
                .success
                .value
                .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value
              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/personal-allowance/$period")

            }

            "must still navigate correctly when navigation logic missing user answers " in {

              val period = Gen.oneOf(pre2020Periods).sample.get

              val ua     = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TotalIncomePage(period), BigInt(100))
                .success
                .value
                .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value
              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/personal-allowance/$period")

            }
          }

          "when post2019 period" - {

            "to know adjusted income page when threshold value calculated to be above 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua     = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TotalIncomePage(period), BigInt(1000000))
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
                .success
                .value
                .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value
              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/change-tax-relief")
    }
              checkNavigation(result, s"/annual-allowance/adjusted-income/know-adjusted-amount/$period")
            }

            "to know personal allowance page when threshold value calculated to be below 200000" in {

              val period = Gen.oneOf(post2019Periods).sample.get

              val ua     = emptyUserAnswers
                .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
                .success
                .value
                .set(TotalIncomePage(period), BigInt(150000))
                .success
                .value
                .set(TaxReliefPage(period), BigInt(1))
                .success
                .value
                .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
                .success
                .value
                .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
                .success
                .value
                .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
                .success
                .value
                .set(
                  ClaimingTaxReliefPensionPage(period),
                  false
                )
                .success
                .value
              val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

              checkNavigation(result, s"/annual-allowance/personal-allowance/$period")

            }
          }
        }
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }

  "Check mode" - {

    //    "to FUTUREPAGETOBEADDED when user enters data in check mode" in {
    //      val ua     = emptyUserAnswers
    //        .set(
    //          ClaimingTaxReliefPensionNotAdjustedIncomePage(period),
    //          BigInt(100)
    //        )
    //        .success
    //        .value
    //      val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(period).navigate(CheckMode, ua).url
    //
    //      checkNavigation(result, "/annual-allowance/2019/total-income/tax-relief")
    //    }

    "must Navigate correctly to CYA in check mode when user enters data" in {
      val ua     = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(period),
          true
        )
        .success
        .value
      val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/check-answers")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
