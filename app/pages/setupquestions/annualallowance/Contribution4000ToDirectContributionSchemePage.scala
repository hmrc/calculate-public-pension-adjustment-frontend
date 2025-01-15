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

import models.tasklist.sections.{AASection, PreAASection}
import models.{LTAKickOutStatus, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object Contribution4000ToDirectContributionSchemePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "aa" \ toString

  override def toString: String = "contribution4000ToDirectContributionScheme"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(Contribution4000ToDirectContributionSchemePage) match {
      case Some(true)  =>
        answers.get(LTAKickOutStatus()) match {
          case Some(0)    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case Some(1)    =>
            controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
              .onPageLoad(NormalMode)
          case Some(2)    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case _    => controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      case Some(false) =>
        controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController.onPageLoad()
      case _           =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(Contribution4000ToDirectContributionSchemePage) match {
      case Some(true)  =>
        answers.get(LTAKickOutStatus()) match {
          case Some(0)    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case Some(1)   =>
            controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
              .onPageLoad(NormalMode)
          case Some(2)    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case _    => controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      case Some(false) =>
        controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController.onPageLoad()
      case _           =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  =>
          super.cleanup(value, userAnswers)
        case false =>
          for {
            answersNoAASetup <- Try(PreAASection.removeAllUserAnswersAndNavigation(userAnswers))
            answersNoAATask  <- Try(AASection.removeAllAAPeriodAnswersAndNavigation(answersNoAASetup))
          } yield answersNoAATask
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
