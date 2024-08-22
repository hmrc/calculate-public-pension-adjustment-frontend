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

package pages.setupquestions.annualallowance

import models.{CheckMode, LTAKickOutStatus, NormalMode, UserAnswers}
import org.apache.pekko.actor.FSM.Normal
import pages.QuestionPage
import pages.annualallowance.taxyear.AmountOfGiftAidPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object PIAAboveAnnualAllowanceIn2023Page extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "aa" \ toString

  override def toString: String = "pIAAboveAnnualAllowanceIn2023"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PIAAboveAnnualAllowanceIn2023Page) match {
      case Some(true)  =>
        answers.get(LTAKickOutStatus()).getOrElse(None) match {
          case 0    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case 1    =>
            controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
              .onPageLoad(NormalMode)
          case 2    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case _    => controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      case Some(false) =>
        controllers.setupquestions.annualallowance.routes.NetIncomeAbove190KIn2023Controller.onPageLoad(NormalMode)
      case _           =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => super.cleanup(value, userAnswers)
        case true  => userAnswers.remove(NetIncomeAbove190KIn2023Page)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
