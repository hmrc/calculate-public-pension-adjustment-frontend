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

import controllers.routes
import models.ContributedToDuringRemedyPeriod.{Definedbenefit, Definedcontribution}
import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class ContributedToDuringRemedyPeriodPage(period: Period)
    extends QuestionPage[Set[ContributedToDuringRemedyPeriod]] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "contributedToDuringRemedyPeriod"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributions) if contributions.contains(Definedcontribution) =>
        controllers.annualallowance.taxyear.routes.DefinedContributionAmountController
          .onPageLoad(NormalMode, period)
      case Some(_)                                                            =>
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController
          .onPageLoad(NormalMode, period)
      case None                                                               =>
        routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributions)
          if !contributions.contains(Definedcontribution) && contributions.contains(Definedbenefit) =>
        onlyDefinedBenefitSelected(answers)
      case Some(contributions)
          if contributions.contains(Definedcontribution) && !contributions.contains(Definedbenefit) =>
        onlyDefinedContributionSelected(answers)
      case Some(contributions)
          if contributions.contains(Definedcontribution) && contributions.contains(Definedbenefit) =>
        bothSelected(answers)
      case None =>
        routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def bothSelected(answers: UserAnswers) =
    answers.get(DefinedContributionAmountPage(period)) match {
      case None    =>
        controllers.annualallowance.taxyear.routes.DefinedContributionAmountController.onPageLoad(CheckMode, period)
      case Some(_) => onlyDefinedBenefitSelected(answers)
    }

  private def onlyDefinedContributionSelected(answers: UserAnswers) =
    answers.get(DefinedContributionAmountPage(period)) match {
      case None    =>
        controllers.annualallowance.taxyear.routes.DefinedContributionAmountController.onPageLoad(CheckMode, period)
      case Some(_) => controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
    }

  private def onlyDefinedBenefitSelected(answers: UserAnswers) =
    answers.get(DefinedBenefitAmountPage(period)) match {
      case None    =>
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(CheckMode, period)
      case Some(_) => controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
    }

  override def cleanup(
    value: Option[Set[ContributedToDuringRemedyPeriod]],
    userAnswers: UserAnswers
  ): Try[UserAnswers] =
    value
      .map { set =>
        if (!set.contains(ContributedToDuringRemedyPeriod.Definedcontribution)) {
          userAnswers.remove(DefinedContributionAmountPage(period))
        } else if (!set.contains(ContributedToDuringRemedyPeriod.Definedbenefit)) {
          userAnswers.remove(DefinedBenefitAmountPage(period))
        } else {
          super.cleanup(value, userAnswers)
        }
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
