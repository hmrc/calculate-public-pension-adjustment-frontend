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

package pages.lifetimeallowance

import models.LtaProtectionOrEnhancements.{Both, Enhancements, No, Protection}
import models.{CheckMode, LtaProtectionOrEnhancements, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object LtaProtectionOrEnhancementsPage extends QuestionPage[LtaProtectionOrEnhancements] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "ltaProtectionOrEnhancements"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(LtaProtectionOrEnhancementsPage) match {
      case Some(Enhancements)            => controllers.lifetimeallowance.routes.EnhancementTypeController.onPageLoad(NormalMode)
      case Some(Protection) | Some(Both) =>
        controllers.lifetimeallowance.routes.ProtectionTypeController.onPageLoad(NormalMode)
      case Some(No)                      => controllers.lifetimeallowance.routes.ProtectionEnhancedChangedController.onPageLoad(NormalMode)
      case None                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(LtaProtectionOrEnhancementsPage) match {
      case Some(Enhancements)            => controllers.lifetimeallowance.routes.EnhancementTypeController.onPageLoad(CheckMode)
      case Some(Protection) | Some(Both) =>
        controllers.lifetimeallowance.routes.ProtectionTypeController.onPageLoad(CheckMode)
      case Some(No)                      => controllers.lifetimeallowance.routes.CheckYourLTAAnswersController.onPageLoad()
      case None                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[LtaProtectionOrEnhancements], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case Protection   =>
          userAnswers
            .remove(EnhancementTypePage)
            .flatMap(_.remove(InternationalEnhancementReferencePage))
            .flatMap(_.remove(PensionCreditReferencePage))
        case Enhancements =>
          userAnswers
            .remove(ProtectionTypePage)
            .flatMap(_.remove(ProtectionReferencePage))
        case Both         => super.cleanup(value, userAnswers)
        case No           =>
          userAnswers
            .remove(EnhancementTypePage)
            .flatMap(_.remove(InternationalEnhancementReferencePage))
            .flatMap(_.remove(PensionCreditReferencePage))
            .flatMap(_.remove(ProtectionTypePage))
            .flatMap(_.remove(ProtectionReferencePage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
