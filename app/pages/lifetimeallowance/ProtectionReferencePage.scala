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

import controllers.lifetimeallowance.routes as ltaRoutes
import controllers.routes as generalRoutes
import models.LtaProtectionOrEnhancements.{Both, Protection}
import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object ProtectionReferencePage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "protectionReference"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(LtaProtectionOrEnhancementsPage) match {
      case Some(Protection) => ltaRoutes.ProtectionEnhancedChangedController.onPageLoad(NormalMode)
      case Some(Both)       => ltaRoutes.EnhancementTypeController.onPageLoad(NormalMode)
      case _                => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(LtaProtectionOrEnhancementsPage) match {
      case Some(Protection) => controllers.lifetimeallowance.routes.CheckYourLTAAnswersController.onPageLoad()
      case Some(Both)       => ltaRoutes.EnhancementTypeController.onPageLoad(CheckMode)
      case _                => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }
}
