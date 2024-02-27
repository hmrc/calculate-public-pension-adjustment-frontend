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
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, StopPayingPublicPensionPage}
import play.api.mvc.Call

object DefinedContribution2016PreAmountNormalModeNavigation {

  def navigate(answers: UserAnswers): Call =
    answers.get(DefinedContribution2016PreAmountPage) match {
      case Some(_) if flexiAccessExistsForSubPeriod(answers)   => flexiStandardNavigation(answers)
      case Some(_) if maybeStopPayingInFirstSubPeriod(answers) => maybeDefinedBenefitExistsNavigation(answers)
      case Some(_)                                             =>
        controllers.annualallowance.taxyear.routes.DefinedContribution2016PostAmountController.onPageLoad(NormalMode)
      case None                                                => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiStandardNavigation(answers: UserAnswers) =
    answers.get(FlexibleAccessStartDatePage) match {
      case Some(date) if date == Period.pre2016End =>
        if (maybeStopPayingInFirstSubPeriod(answers)) {
          maybeDefinedBenefitExistsNavigation(answers)
        } else {
          controllers.annualallowance.taxyear.routes.DefinedContribution2016PostAmountController.onPageLoad(NormalMode)
        }
      case Some(_)                                 =>
        controllers.annualallowance.taxyear.routes.DefinedContribution2016PreFlexiAmountController
          .onPageLoad(NormalMode)
      case None                                    => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiAccessExistsForSubPeriod(answers: UserAnswers) = answers.get(FlexibleAccessStartDatePage) match {
    case Some(date) => Period.pre2016Start.minusDays(1).isBefore(date) && Period.pre2016End.plusDays(1).isAfter(date)
    case None       => false
  }

  private def maybeStopPayingInFirstSubPeriod(answers: UserAnswers) = answers.get(StopPayingPublicPensionPage) match {
    case Some(date) => Period.pre2016Start.minusDays(1).isBefore(date) && Period.pre2016End.plusDays(1).isAfter(date)
    case None       => false
  }

  private def definedBenefitExists(answers: UserAnswers) =
    answers.get(ContributedToDuringRemedyPeriodPage(Period._2016)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }

  private def maybeDefinedBenefitExistsNavigation(answers: UserAnswers) =
    if (definedBenefitExists(answers)) {
      controllers.annualallowance.taxyear.routes.DefinedBenefit2016PreAmountController.onPageLoad(NormalMode)
    } else {
      controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, Period._2016)
    }
}
