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

import models.{NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class AdjustedIncomePage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "adjustedIncome"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(AdjustedIncomePage(period)) match {
      case Some(_) if period != Period._2016 =>
        controllers.annualallowance.taxyear.routes.DoYouHaveGiftAidController.onPageLoad(NormalMode, period)
      case _                                 => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(AdjustedIncomePage(period)) match {
      case Some(_) if period != Period._2016 =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case _                                 => controllers.routes.JourneyRecoveryController.onPageLoad()
    }
}
