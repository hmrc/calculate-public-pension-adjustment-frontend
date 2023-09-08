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

package pages.lifetimeallowance

import controllers.{routes => generalRoutes}
import controllers.lifetimeallowance.{routes => ltaRoutes}
import models.EnhancementType.{Both, Internationalenhancement, Pensioncredit}
import models.{CheckMode, EnhancementType, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object EnhancementTypePage extends QuestionPage[EnhancementType] {
  // Enhancement type - should the radio be "Both" instead of "None"
  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "enhancementType"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(EnhancementTypePage) match {
      case Some(Pensioncredit)                         => ltaRoutes.PensionCreditReferenceController.onPageLoad(NormalMode)
      case Some(Internationalenhancement) | Some(Both) =>
        ltaRoutes.InternationalEnhancementReferenceController.onPageLoad(NormalMode)
      case None                                        => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(EnhancementTypePage) match {
      case Some(Pensioncredit)                         => ltaRoutes.PensionCreditReferenceController.onPageLoad(CheckMode)
      case Some(Internationalenhancement) | Some(Both) =>
        ltaRoutes.InternationalEnhancementReferenceController.onPageLoad(CheckMode)
      case None                                        => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[EnhancementType], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case Internationalenhancement =>
          userAnswers
            .remove(PensionCreditReferencePage)
        case Pensioncredit            =>
          userAnswers
            .remove(InternationalEnhancementReferencePage)
        case Both                     => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
