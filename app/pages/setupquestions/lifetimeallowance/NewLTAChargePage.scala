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
import models.{AAKickOutStatus, CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object NewLTAChargePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "lta" \ toString

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(NewLTAChargePage) match {
      case Some(true)  => redirectToNext(answers)
      case Some(false) =>
        controllers.setupquestions.lifetimeallowance.routes.MultipleBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(NewLTAChargePage) match {
      case Some(true)  => redirectToNext(answers)
      case Some(false) =>
        controllers.setupquestions.lifetimeallowance.routes.MultipleBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def redirectToNext(answers: UserAnswers): Call =
    answers.get(AAKickOutStatus()).getOrElse(None) match {
      case 0    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case 1    => controllers.setupquestions.annualallowance.routes.SavingsStatementController.onPageLoad(NormalMode)
      case 2    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case _    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { _ =>
        userAnswers
          .remove(MultipleBenefitCrystallisationEventPage)
          .flatMap(_.remove(OtherSchemeNotificationPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
