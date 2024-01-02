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

import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class FlexiAccessDefinedContributionAmountPage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "flexiAccessDefinedContributionAmount"

  // noinspection ScalaStyle
  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val definedBenefitExists = answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }

    if (definedBenefitExists) {
      controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(NormalMode, period)
    } else {
      answers.get(FlexiAccessDefinedContributionAmountPage(period)) match {
        case Some(_) if period == Period._2016PostAlignment =>
          controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
        case Some(_) if period == Period._2016PreAlignment  =>
          controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period)
        case Some(_)                                        =>
          controllers.annualallowance.taxyear.routes.ThresholdIncomeController
            .onPageLoad(NormalMode, period)
        case None                                           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {

    val definedBenefitExists = answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributedTo) if contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit) => true
      case _                                                                                             => false
    }

    if (definedBenefitExists) {
      if (answers.get(DefinedBenefitAmountPage(period)).isDefined) {
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      } else {
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController.onPageLoad(CheckMode, period)
      }
    } else {
      controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
    }
  }
}
