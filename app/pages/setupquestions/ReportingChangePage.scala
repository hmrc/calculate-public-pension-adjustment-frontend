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

import controllers.routes
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.sections.{AASection, LTASection, PreAASection, TriageSection}
import models.{NormalMode, ReportingChange, UserAnswers}
import pages.QuestionPage
import pages.lifetimeallowance.DateOfBenefitCrystallisationEventPage
import pages.setupquestions.lifetimeallowance.HadBenefitCrystallisationEventPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ReportingChangePage extends QuestionPage[Set[ReportingChange]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reportingChange"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(reportingChange) if reportingChange.contains(AnnualAllowance)   =>
      controllers.setupquestions.annualallowance.routes.SavingsStatementController.onPageLoad(NormalMode)
    case Some(reportingChange) if reportingChange.contains(LifetimeAllowance) =>
      controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
        .onPageLoad(NormalMode)
    case _                                                                    =>
      routes.JourneyRecoveryController.onPageLoad(None)
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(reportingChange) if reportingChange.contains(AnnualAllowance)   =>
      controllers.setupquestions.annualallowance.routes.SavingsStatementController.onPageLoad(NormalMode)
    case Some(reportingChange) if reportingChange.contains(LifetimeAllowance) =>
      controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
        .onPageLoad(NormalMode)
    case _                                                                    => routes.JourneyRecoveryController.onPageLoad(None)
  }

  override def cleanup(
    value: Option[Set[ReportingChange]],
    userAnswers: UserAnswers
  ): Try[UserAnswers] =
    value
      .map { set =>
        if (!set.contains(ReportingChange.LifetimeAllowance) && set.contains(ReportingChange.AnnualAllowance)) {
          val answersWithNoKickOutStatus = TriageSection.removeAllKickOutStatusUserAnswers(userAnswers)
          val answersWithNoTriageLTA     = TriageSection.removeAllLTAUserAnswers(answersWithNoKickOutStatus)
          Try(LTASection.removeAllUserAnswersAndNavigation(answersWithNoTriageLTA))
        } else if (!set.contains(ReportingChange.AnnualAllowance) && set.contains(ReportingChange.LifetimeAllowance)) {
          val answersWithNoKickOutStatus = TriageSection.removeAllKickOutStatusUserAnswers(userAnswers)
          val answersWithNoAATriage      = TriageSection.removeAllAAUserAnswers(answersWithNoKickOutStatus)
          val answersWithNoPreAA         = PreAASection.removeAllUserAnswersAndNavigation(answersWithNoAATriage)
          Try(AASection.removeAllAAPeriodAnswersAndNavigation(answersWithNoPreAA))
        } else {
          super.cleanup(value, userAnswers)
        }
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
