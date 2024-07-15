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

import models.Period._2013
import models.{CheckMode, NormalMode, PayeCodeAdjustment, Period, ThresholdIncome, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class PayeCodeAdjustmentPage(period: Period) extends QuestionPage[PayeCodeAdjustment] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "payeCodeAdjustment"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PayeCodeAdjustmentPage(period)) match {
      case Some(_) =>
        controllers.annualallowance.taxyear.routes.CodeAdjustmentAmountController.onPageLoad(NormalMode, period)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PayeCodeAdjustmentPage(period)) match {
      case Some(_) =>
        if (answers.get(CodeAdjustmentAmountPage(period)).isEmpty) {
          controllers.annualallowance.taxyear.routes.CodeAdjustmentAmountController.onPageLoad(CheckMode, period)
        } else { controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period) }
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
