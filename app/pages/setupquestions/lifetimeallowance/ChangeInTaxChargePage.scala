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

package pages.setupquestions.lifetimeallowance

import models.ChangeInTaxCharge.{DecreasedCharge, IncreasedCharge, NewCharge}
import models.{ChangeInTaxCharge, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ChangeInTaxChargePage extends QuestionPage[ChangeInTaxCharge] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "changeInTaxCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.IncreasedCharge) =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case Some(ChangeInTaxCharge.DecreasedCharge)                                     =>
        checkPreviousLTACharge(answers)
      case _                                                                           =>
        controllers.setupquestions.lifetimeallowance.routes.MultipleBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
    }

  private def checkPreviousLTACharge(answers: UserAnswers) =
    answers.get(PreviousLTAChargePage) match {
      case Some(true)  =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case Some(false) =>
        controllers.setupquestions.lifetimeallowance.routes.MultipleBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.IncreasedCharge) =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case Some(ChangeInTaxCharge.DecreasedCharge)                                     =>
        checkPreviousLTACharge(answers)
      case _                                                                           =>
        controllers.setupquestions.lifetimeallowance.routes.MultipleBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
    }
}
