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

package controllers.annualallowance.taxyear

import base.SpecBase
import models.{Period, ThresholdIncome}
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import pages.annualallowance.taxyear._

class AboveThresholdControllerSpec extends AnyFreeSpec with SpecBase {

  val period: Period = Period._2019

  val pre2021Periods: List[Period]  = List(Period._2017, Period._2018, Period._2019, Period._2020)
  val post2020Periods: List[Period] = List(Period._2021, Period._2022, Period._2023)

  "AboveThresholdController" - {

    "thresholdStatus" - {

      "must return false when below 110000 before 2021" in {

        val period = Gen.oneOf(pre2021Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(1))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.thresholdStatus(ua, period) mustBe false

      }

      "must return true when above 110000 before 2020" in {

        val period = Gen.oneOf(pre2021Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(1000000))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.thresholdStatus(ua, period) mustBe true

      }

      "must return false when below 200000 after and including 2020" in {

        val period = Gen.oneOf(post2020Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(1))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.thresholdStatus(ua, period) mustBe false

      }

      "must return true when above 200000 after and including 2020" in {

        val period = Gen.oneOf(post2020Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(10000000))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.thresholdStatus(ua, period) mustBe true

      }
    }

    "calculateThresholdStatus" - {
      "must return 0 if a negative number is calculated" in {
        val period = Gen.oneOf(pre2021Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(-1000))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.calculateThresholdStatus(ua, period) mustBe 0

      }
      "must return the correct value if a positive value is calculated" in {
        val period = Gen.oneOf(pre2021Periods).sample.get

        val ua = emptyUserAnswers
          .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
          .success
          .value
          .set(TotalIncomePage(period), BigInt(1000))
          .success
          .value
          .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
          .success
          .value
          .set(RASContributionAmountPage(period), BigInt(1))
          .success
          .value
          .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
          .success
          .value
          .set(TaxReliefPage(period), BigInt(1))
          .success
          .value

        val controller = new AboveThresholdController
        controller.calculateThresholdStatus(ua, period) mustBe 1000

      }

    }
  }

}
