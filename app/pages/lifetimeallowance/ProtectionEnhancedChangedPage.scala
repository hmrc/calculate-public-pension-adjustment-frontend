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

import models.ProtectionEnhancedChanged.{Both, Enhancement, No, Protection}
import models.{CheckMode, NormalMode, ProtectionEnhancedChanged, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ProtectionEnhancedChangedPage extends QuestionPage[ProtectionEnhancedChanged] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "protectionTypeEnhancementChanged"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ProtectionEnhancedChangedPage) match {
      case Some(Enhancement)             => controllers.lifetimeallowance.routes.NewEnhancementTypeController.onPageLoad(NormalMode)
      case Some(Protection) | Some(Both) =>
        controllers.lifetimeallowance.routes.WhatNewProtectionTypeEnhancementController.onPageLoad(NormalMode)
      case Some(No)                      => controllers.lifetimeallowance.routes.LifetimeAllowanceChargeController.onPageLoad(NormalMode)
      case _                             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ProtectionEnhancedChangedPage) match {
      case Some(Enhancement)             => controllers.lifetimeallowance.routes.NewEnhancementTypeController.onPageLoad(CheckMode)
      case Some(Protection) | Some(Both) =>
        controllers.lifetimeallowance.routes.WhatNewProtectionTypeEnhancementController.onPageLoad(CheckMode)
      case Some(No)                      => controllers.lifetimeallowance.routes.CheckYourLTAAnswersController.onPageLoad()
      case None                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[ProtectionEnhancedChanged], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case Protection  =>
          userAnswers
            .remove(NewEnhancementTypePage)
            .flatMap(_.remove(NewInternationalEnhancementReferencePage))
            .flatMap(_.remove(NewPensionCreditReferencePage))
        case Enhancement =>
          userAnswers
            .remove(WhatNewProtectionTypeEnhancementPage)
            .flatMap(_.remove(ReferenceNewProtectionTypeEnhancementPage))
        case No          =>
          userAnswers
            .remove(NewEnhancementTypePage)
            .flatMap(_.remove(NewInternationalEnhancementReferencePage))
            .flatMap(_.remove(NewPensionCreditReferencePage))
            .flatMap(_.remove(WhatNewProtectionTypeEnhancementPage))
            .flatMap(_.remove(ReferenceNewProtectionTypeEnhancementPage))
        case Both        => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
