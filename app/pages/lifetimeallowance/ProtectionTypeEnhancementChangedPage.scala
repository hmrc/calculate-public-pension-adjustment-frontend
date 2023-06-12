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

import controllers.lifetimeallowance.{routes => ltaRoutes}
import controllers.routes
import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ProtectionTypeEnhancementChangedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "protectionTypeEnhancementChanged"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ProtectionTypeEnhancementChangedPage) match {
      case Some(true)  => ltaRoutes.WhatNewProtectionTypeEnhancementController.onPageLoad(NormalMode)
      case Some(false) => ltaRoutes.LifetimeAllowanceChargeController.onPageLoad(NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ProtectionTypeEnhancementChangedPage) match {
      case Some(true)  => ltaRoutes.WhatNewProtectionTypeEnhancementController.onPageLoad(NormalMode)
      case Some(false) => ltaRoutes.LifetimeAllowanceChargeController.onPageLoad(CheckMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          userAnswers
            .remove(WhatNewProtectionTypeEnhancementPage)
            .flatMap(_.remove(ReferenceNewProtectionTypeEnhancementPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
