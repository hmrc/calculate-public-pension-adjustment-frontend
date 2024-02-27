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

import models.{ContributedToDuringRemedyPeriod, NormalMode, Period, UserAnswers}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import play.api.mvc.Call

import java.time.LocalDate

class DefinedContributionNormalModeNavigation(period: Period) {

  // noinspection ScalaStyle
  def navigate(answers: UserAnswers): Call =
    answers.get(DefinedContributionAmountPage(period)) match {
      case Some(_) if flexiAccessExistsForPeriod(answers) => flexiStandardNavigation(answers)
      case Some(_) if definedBenefitExists(answers)       =>
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
      case Some(_)                                        =>
        controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period)
      case None                                           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiStandardNavigation(answers: UserAnswers) =
    answers.get(FlexibleAccessStartDatePage) match {
      case Some(date) if date == period.end =>
        flexiStandardWhenFlexiDateIsPeriodEndDate(answers)
      case Some(_)                          =>
        controllers.annualallowance.taxyear.routes.FlexiAccessDefinedContributionAmountController
          .onPageLoad(NormalMode, period)
      case None                             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiStandardWhenFlexiDateIsPeriodEndDate(answers: UserAnswers) =
    if (definedBenefitExists(answers)) {
      controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
    } else {
      controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period)
    }

  private def flexiAccessExistsForPeriod(answers: UserAnswers) = answers.get(FlexibleAccessStartDatePage) match {
    case Some(date) => period.start.minusDays(1).isBefore(date) && period.end.plusDays(1).isAfter(date)
    case None       => false
  }

  private def definedBenefitExists(answers: UserAnswers) =
    answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }
}
