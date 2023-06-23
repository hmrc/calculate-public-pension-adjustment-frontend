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

import controllers.annualallowance.taxyear.routes.{DefinedBenefitAmountController, FlexiAccessDefinedContributionAmountController, ThresholdIncomeController}
import models.{ContributedToDuringRemedyPeriod, NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class DefinedContributionAmountPage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[Int] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "definedContributionAmount"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val flexiAccessExists    = answers.get(FlexibleAccessStartDatePage).isDefined
    val definedBenefitExists = (answers.get(ContributedToDuringRemedyPeriodPage(period, schemeIndex)) map {
      contributedTo => contributedTo.contains(ContributedToDuringRemedyPeriod.Definedbenefit)
    }).isDefined

    answers.get(DefinedContributionAmountPage(period, schemeIndex)) match {
      case Some(_) if flexiAccessExists                   =>
        FlexiAccessDefinedContributionAmountController.onPageLoad(NormalMode, period, schemeIndex)
      case Some(_) if definedBenefitExists                =>
        DefinedBenefitAmountController.onPageLoad(NormalMode, period, schemeIndex)
      case Some(_) if period == Period._2016PreAlignment  =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Some(_) if period == Period._2016PostAlignment =>
        controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case Some(_)                                        =>
        controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case None                                           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
}
