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

package pages.setupquestions

import models.tasklist.sections.{AASection, LTASection, PreAASection}
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class SavingsStatementPage(optionalAuthEnabled: Boolean) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "savingsStatement"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(SavingsStatementPage(optionalAuthEnabled)), optionalAuthEnabled, answers.authenticated) match {
      case (Some(true), true, true) =>
        controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode)
      case (Some(true), true, false) =>
        controllers.routes.OptionalSignInController.onPageLoad()
      case (Some(true), false, _)    =>
        controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode)
      case (Some(false), _, _)       => controllers.setupquestions.routes.IneligibleController.onPageLoad
      case (None, _, _)              => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(SavingsStatementPage(optionalAuthEnabled)) match {
      case Some(true)  => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case Some(false) => controllers.setupquestions.routes.IneligibleController.onPageLoad
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          val answersWithNoPreAA = PreAASection.removeAllUserAnswersAndNavigation(userAnswers)
          val answersWithNoAA    = AASection.removeAllAAPeriodAnswersAndNavigation(answersWithNoPreAA)
          val answersWithNoLTA   = LTASection.removeAllUserAnswersAndNavigation(answersWithNoAA)
          answersWithNoLTA
            .remove(ReportingChangePage)
            .get
            .remove(ResubmittingAdjustmentPage)
            .get
            .remove(ReasonForResubmissionPage)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
