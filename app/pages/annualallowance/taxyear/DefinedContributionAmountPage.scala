/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{ContributedToDuringRemedyPeriod, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate

case class DefinedContributionAmountPage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "definedContributionAmount"

  // noinspection ScalaStyle
  override protected def navigateInNormalMode(answers: UserAnswers): Call = {

    val flexiAccessExistsForPeriod = answers.get(FlexibleAccessStartDatePage) match {
      case Some(date) => period.start.minusDays(1).isBefore(date) && period.end.plusDays(1).isAfter(date)
      case None       => false
    }

    val definedBenefitExists = answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }

    def flexiAccessDateEqualsPeriodEndDatePre2016(answers: UserAnswers) = {
      val endDate = LocalDate.of(2015, 7, 8)
      answers.get(FlexibleAccessStartDatePage) match {
        case Some(date) if date == endDate =>
          if (definedBenefitExists) {
            controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
          } else {
            controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period)
          }
        case Some(_)                       =>
          controllers.annualallowance.taxyear.routes.FlexiAccessDefinedContributionAmountController
            .onPageLoad(NormalMode, period)
        case None                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    }

    def flexiAccessDateEqualsPeriodEndDateNotPre2016(answers: UserAnswers) = {
      val endDate = LocalDate.of(period.end.getYear, 4, 5)
      answers.get(FlexibleAccessStartDatePage) match {
        case Some(date) if date == endDate =>
          if (definedBenefitExists)
            controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
          else if (period == Period._2016PostAlignment)
            controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
          else
            controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period)
        case Some(_)                       =>
          controllers.annualallowance.taxyear.routes.FlexiAccessDefinedContributionAmountController
            .onPageLoad(NormalMode, period)
        case None                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    }

    def isPre2016Period =
      if (period == Period._2016PreAlignment)
        flexiAccessDateEqualsPeriodEndDatePre2016(answers)
      else
        flexiAccessDateEqualsPeriodEndDateNotPre2016(answers)

    answers.get(DefinedContributionAmountPage(period)) match {
      case Some(_) if flexiAccessExistsForPeriod          =>
        isPre2016Period
      case Some(_) if definedBenefitExists                =>
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
      case Some(_) if period == Period._2016PostAlignment =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Some(_) if period == Period._2016PreAlignment  =>
        controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period)
      case Some(_)                                        =>
        controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period)
      case None                                           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
}
