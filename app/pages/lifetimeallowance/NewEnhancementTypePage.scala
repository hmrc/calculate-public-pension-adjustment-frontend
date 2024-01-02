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

import controllers.{routes => generalRoutes}
import controllers.lifetimeallowance.{routes => ltaRoutes}
import models.NewEnhancementType.{Both, InternationalEnhancement, PensionCredit}
import models.{CheckMode, NewEnhancementType, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object NewEnhancementTypePage extends QuestionPage[NewEnhancementType] {
  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "newEnhancementType"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(NewEnhancementTypePage) match {
      case Some(PensionCredit)                         => ltaRoutes.NewPensionCreditReferenceController.onPageLoad(NormalMode)
      case Some(InternationalEnhancement) | Some(Both) =>
        ltaRoutes.NewInternationalEnhancementReferenceController.onPageLoad(NormalMode)
      case _                                           => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(NewEnhancementTypePage) match {
      case Some(PensionCredit)                         => ltaRoutes.NewPensionCreditReferenceController.onPageLoad(CheckMode)
      case Some(InternationalEnhancement) | Some(Both) =>
        ltaRoutes.NewInternationalEnhancementReferenceController.onPageLoad(CheckMode)
      case _                                           => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[NewEnhancementType], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case InternationalEnhancement =>
          userAnswers
            .remove(NewPensionCreditReferencePage)
        case PensionCredit            =>
          userAnswers
            .remove(NewInternationalEnhancementReferencePage)
        case Both                     => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
