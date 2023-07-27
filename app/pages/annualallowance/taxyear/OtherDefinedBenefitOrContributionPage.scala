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

import controllers.annualallowance.taxyear.routes.{CheckYourAAPeriodAnswersController, ContributedToDuringRemedyPeriodController, ThresholdIncomeController, TotalIncomeController}
import controllers.routes
import models.{NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class OtherDefinedBenefitOrContributionPage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "otherDefinedBenefitOrContribution"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(OtherDefinedBenefitOrContributionPage(period)) match {
      case Some(false) if period == Period._2016PreAlignment  =>
        CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Some(false) if period == Period._2016PostAlignment =>
        TotalIncomeController.onPageLoad(NormalMode, period)
      case Some(false)                                        => ThresholdIncomeController.onPageLoad(NormalMode, period)
      case Some(true)                                         => ContributedToDuringRemedyPeriodController.onPageLoad(NormalMode, period)
      case None                                               => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(OtherDefinedBenefitOrContributionPage(period)) match {
      case Some(false) => CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.ContributedToDuringRemedyPeriodController
          .onPageLoad(NormalMode, period)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          removePages(userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def removePages(userAnswers: UserAnswers) =
    userAnswers
      .remove(ContributedToDuringRemedyPeriodPage(period))
      .flatMap(_.remove(DefinedContributionAmountPage(period)))
      .flatMap(_.remove(DefinedBenefitAmountPage(period)))
}
