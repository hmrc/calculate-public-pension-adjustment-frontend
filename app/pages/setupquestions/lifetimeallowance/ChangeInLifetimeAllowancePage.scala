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

import controllers.routes
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ChangeInLifetimeAllowancePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "lta" \ toString

  override def toString: String = "changeInLifetimeAllowance"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val previousLTACharge = answers.get(PreviousLTAChargePage)
    (answers.get(ChangeInLifetimeAllowancePage), previousLTACharge) match {
      case (Some(true), Some(true))  =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case (Some(true), Some(false)) =>
        controllers.setupquestions.lifetimeallowance.routes.IncreaseInLTAChargeController
          .onPageLoad(NormalMode)
      case (Some(false), Some(_))    =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad() // TODO Needs to be changed
      case _                         => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    val previousLTACharge = answers.get(PreviousLTAChargePage)
    (answers.get(ChangeInLifetimeAllowancePage), previousLTACharge) match {
      case (Some(true), Some(true))  =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case (Some(true), Some(false)) =>
        controllers.setupquestions.lifetimeallowance.routes.IncreaseInLTAChargeController
          .onPageLoad(NormalMode)
      case (Some(false), Some(_))    =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad() // TODO Needs to be changed
      case _                         => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val previousLTACharge = userAnswers.get(PreviousLTAChargePage).getOrElse(false)
    value
      .map {
        case true  =>
          if (previousLTACharge) {
            userAnswers
              .remove(IncreaseInLTAChargePage)
              .flatMap(_.remove(NewLTAChargePage))
              .flatMap(_.remove(MultipleBenefitCrystallisationEventPage))
          } else {
            super.cleanup(value, userAnswers)
          }
        case false =>
          userAnswers
            .remove(IncreaseInLTAChargePage)
            .flatMap(_.remove(NewLTAChargePage))
            .flatMap(_.remove(MultipleBenefitCrystallisationEventPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }
}
