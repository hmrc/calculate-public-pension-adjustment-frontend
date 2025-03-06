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

import models.{Period, UserAnswers}
import pages.annualallowance.taxyear._

class AboveThresholdController {

  def thresholdStatus(answers: UserAnswers, period: Period): Boolean =
    if (
      period == Period._2016 || period == Period._2017 || period == Period._2018 || period == Period._2019 || period == Period._2020
    ) {
      thresholdRoutingPre2021(answers, period)
    } else {
      thresholdRoutingPost2020(answers, period)
    }

  private def thresholdRoutingPre2021(answers: UserAnswers, period: Period) =
    if (calculateThresholdStatus(answers, period) > 110000) {
      true
    } else {
      false
    }

  private def thresholdRoutingPost2020(answers: UserAnswers, period: Period) =
    if (calculateThresholdStatus(answers, period) > 200000) {
      true
    } else {
      false
    }

  def calculateThresholdStatus(answers: UserAnswers, period: Period): BigInt =
    (answers.get(TotalIncomePage(period)).getOrElse(BigInt(0)) -
      answers.get(TaxReliefPage(period)).getOrElse(BigInt(0)) +
      answers.get(AmountSalarySacrificeArrangementsPage(period)).getOrElse(BigInt(0)) +
      answers.get(AmountFlexibleRemunerationArrangementsPage(period)).getOrElse(BigInt(0)) -
      answers.get(RASContributionAmountPage(period)).getOrElse(BigInt(0)) -
      answers.get(LumpSumDeathBenefitsValuePage(period)).getOrElse(BigInt(0))).max(0)

}
