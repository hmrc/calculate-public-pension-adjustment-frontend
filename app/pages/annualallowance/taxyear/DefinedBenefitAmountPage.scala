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

import controllers.annualallowance.taxyear.routes.{CheckYourAAPeriodAnswersController, ThresholdIncomeController, TotalIncomeController}
import models.{NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class DefinedBenefitAmountPage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[Int] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "definedBenefitAmount"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(DefinedBenefitAmountPage(period, schemeIndex)) match {
      case Some(_) if period == Period._2016PreAlignment  =>
        CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Some(_) if period == Period._2016PostAlignment =>
        TotalIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case Some(_) => ThresholdIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case None => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    CheckYourAAPeriodAnswersController.onPageLoad(period)
}
