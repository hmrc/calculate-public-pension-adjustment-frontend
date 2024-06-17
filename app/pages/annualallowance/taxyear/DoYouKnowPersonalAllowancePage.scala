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

import models.{CheckMode, Mode, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class DoYouKnowPersonalAllowancePage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "doYouKnowPersonalAllowance"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(DoYouKnowPersonalAllowancePage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.PersonalAllowanceController.onPageLoad(NormalMode, period)
      case Some(false) =>
        if (isNetIncomeOver100K(answers)) {
          controllers.annualallowance.taxyear.routes.BlindAllowanceController.onPageLoad(NormalMode, period)
        } else {
          controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController.checkBasicRate(period)
        }
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(DoYouKnowPersonalAllowancePage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.PersonalAllowanceController.onPageLoad(CheckMode, period)
      case Some(false) =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => userAnswers.remove(PersonalAllowancePage(period))
        case true  => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def isNetIncomeOver100K(answers: UserAnswers): Boolean =
    answers.get(TotalIncomePage(period)) match {
      case Some(value) if value > BigInt(100000) => true
      case _                                     => false
    }

}
