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

import models.{CheckMode, ContributedToDuringRemedyPeriod, Period, UserAnswers}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import play.api.mvc.Call

object DefinedContribution2016PostAmountCheckModeNavigation {

  def navigate(answers: UserAnswers): Call =
    answers.get(DefinedContribution2016PostAmountPage) match {
      case Some(_) if flexiAccessExistsForSubPeriod(answers) => flexiNavigation(answers)
      case Some(_)                                           =>
        maybeDefinedBenefitExistsNavigation(answers)
      case None                                              => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiNavigation(answers: UserAnswers) =
    answers.get(FlexibleAccessStartDatePage) match {
      case Some(date) if date == Period.post2016End =>
        maybeDefinedBenefitExistsNavigation(answers)
      case Some(_)                                  =>
        if (answers.get(DefinedContribution2016PostFlexiAmountPage).isDefined) {
          maybeDefinedBenefitExistsNavigation(answers)
        } else {
          controllers.annualallowance.taxyear.routes.DefinedContribution2016PostFlexiAmountController
            .onPageLoad(CheckMode)
        }
      case None                                     => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def flexiAccessExistsForSubPeriod(answers: UserAnswers) = answers.get(FlexibleAccessStartDatePage) match {
    case Some(date) => Period.post2016Start.minusDays(1).isBefore(date) && Period.post2016End.plusDays(1).isAfter(date)
    case None       => false
  }

  private def definedBenefitExists(answers: UserAnswers) =
    answers.get(ContributedToDuringRemedyPeriodPage(Period._2016)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }

  private def maybeDefinedBenefitExistsNavigation(answers: UserAnswers) =
    if (definedBenefitExists(answers)) {
      if (answers.get(DefinedBenefit2016PreAmountPage).isDefined) {
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016)
      } else {
        controllers.annualallowance.taxyear.routes.DefinedBenefit2016PreAmountController.onPageLoad(CheckMode)
      }
    } else {
      controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016)
    }
}
