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

import controllers.annualallowance.taxyear.routes.{AdjustedIncomeController, CheckYourAAPeriodAnswersController, TotalIncomeController}
import models.{CheckMode, NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class ThresholdIncomePage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "thresholdIncome"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          userAnswers
            .remove(AdjustedIncomePage(period, schemeIndex))
      }
      .getOrElse(super.cleanup(value, userAnswers))

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ThresholdIncomePage(period, schemeIndex)) match {
      case Some(true)  => AdjustedIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case Some(false) => TotalIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ThresholdIncomePage(period, schemeIndex)) match {
      case Some(true)  => AdjustedIncomeController.onPageLoad(CheckMode, period, schemeIndex)
      case Some(false) => CheckYourAAPeriodAnswersController.onPageLoad(period)
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
