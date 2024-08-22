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

import controllers.setupquestions.lifetimeallowance.{routes => setupLTARoutes}
import controllers.lifetimeallowance.{routes => ltaRoutes}
import controllers.{routes => generalRoutes}
import models.tasklist.sections.LTASection
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object HadBenefitCrystallisationEventPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "lta" \ toString

  override def toString: String = "hadBenefitCrystallisationEvent"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(HadBenefitCrystallisationEventPage) match {
      case Some(true)  => setupLTARoutes.PreviousLTAChargeController.onPageLoad(NormalMode)
      case Some(false) => setupLTARoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
      case None        => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(HadBenefitCrystallisationEventPage) match {
      case Some(true)  => setupLTARoutes.PreviousLTAChargeController.onPageLoad(NormalMode)
      case Some(false) => setupLTARoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
      case None        => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }
}