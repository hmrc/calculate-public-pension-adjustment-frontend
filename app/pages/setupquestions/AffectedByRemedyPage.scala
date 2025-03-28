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

import models.tasklist.sections.{AASection, LTASection, PreAASection, TriageSection}
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object AffectedByRemedyPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "affectedByRemedy"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(AffectedByRemedyPage) match {
      case Some(true)  => controllers.setupquestions.routes.ReportingChangeController.onPageLoad(NormalMode)
      case Some(false) => controllers.setupquestions.routes.IneligibleController.onPageLoad
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(AffectedByRemedyPage) match {
      case Some(true)  => controllers.setupquestions.routes.ReportingChangeController.onPageLoad(NormalMode)
      case Some(false) => controllers.setupquestions.routes.IneligibleController.onPageLoad
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  => super.cleanup(value, userAnswers)
      case Some(false) =>
        val answersWithNoKickOutStatus = TriageSection.removeAllKickOutStatusUserAnswers(userAnswers)
        val answersWithNoTriageLTA     = TriageSection.removeAllLTAUserAnswers(answersWithNoKickOutStatus)
        val answersWithNoAATriage      = TriageSection.removeAllAAUserAnswers(answersWithNoTriageLTA)
        val answersWithNoPreAA         = PreAASection.removeAllUserAnswersAndNavigation(answersWithNoAATriage)
        val aaSectionRemove            =
          AASection.removeAllAAPeriodAnswersAndNavigation(answersWithNoPreAA).remove(ReportingChangePage).get
        Try(LTASection.removeAllUserAnswersAndNavigation(aaSectionRemove))
      case _           => super.cleanup(value, userAnswers)
    }
}
