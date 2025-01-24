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

import models.{CheckMode, NormalMode, ProtectionType, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object ProtectionTypePage extends QuestionPage[ProtectionType] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "protectionType"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ProtectionTypePage) match {
      case Some(_) => controllers.lifetimeallowance.routes.ProtectionReferenceController.onPageLoad(NormalMode)
      case None    => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ProtectionTypePage) match {
      case Some(_) => controllers.lifetimeallowance.routes.ProtectionReferenceController.onPageLoad(CheckMode)
      case None    => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
