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

import models.{CheckMode, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object DefinedBenefit2016PreAmountPage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ "2016" \ toString

  override def toString: String = "definedBenefit2016PreAmount"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(DefinedBenefit2016PreAmountPage) match {
      case Some(_) if maybeStopPayingInFirstSubPeriod(answers) =>
        controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, Period._2016)
      case Some(_)                                             =>
        controllers.annualallowance.taxyear.routes.DefinedBenefit2016PostAmountController.onPageLoad(NormalMode)
      case _                                                   => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(DefinedBenefit2016PreAmountPage) match {
      case Some(_) if maybeStopPayingInFirstSubPeriod(answers) =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016)
      case Some(_)                                             =>
        answers.get(DefinedBenefit2016PostAmountPage) match {
          case Some(_) =>
            controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2016)
          case _       =>
            controllers.annualallowance.taxyear.routes.DefinedBenefit2016PostAmountController.onPageLoad(CheckMode)
        }
      case _                                                   => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def maybeStopPayingInFirstSubPeriod(answers: UserAnswers) = answers.get(StopPayingPublicPensionPage) match {
    case Some(date) => Period.pre2016Start.minusDays(1).isBefore(date) && Period.pre2016End.plusDays(1).isAfter(date)
    case None       => false
  }
}
