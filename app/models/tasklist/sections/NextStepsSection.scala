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

package models.tasklist.sections

import config.FrontendAppConfig
import controllers.routes
import models.tasklist.{Section, SectionGroupViewModel, SectionStatus}
import models.{AAKickOutStatus, LTAKickOutStatus, PostTriageFlag, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage

import javax.inject._

@Singleton
class NextStepsSection @Inject() (
  config: FrontendAppConfig
) extends Section {

  def sectionStatus(dataCaptureSections: List[Option[SectionGroupViewModel]], answers: UserAnswers): SectionStatus = {
    val allDataCaptureComplete: Boolean = dataCaptureSections.flatten.forall(_.isComplete)

    if (allDataCaptureComplete) {
      if (answers.get(PostTriageFlag).isDefined) {
        postTriageSectionStatus(answers)
      } else {
        preTriageSectionStatus(answers)
      }
    } else {
      SectionStatus.CannotStartYet
    }
  }

  private def postTriageSectionStatus(answers: UserAnswers) =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs, answers)                                                => SectionStatus.NotStarted
      case Some(rcs)
          if !calculationRequired(rcs, answers) && (LTASection
            .kickoutHasBeenReached(answers) || !answers.get(LTAKickOutStatus()).contains(2)) =>
        SectionStatus.CannotStartYet
      case Some(rcs) if !calculationRequired(rcs, answers) && !LTASection.kickoutHasBeenReached(answers) =>
        SectionStatus.NotStarted
      case _                                                                                             => SectionStatus.CannotStartYet
    }

  private def preTriageSectionStatus(answers: UserAnswers) =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs, answers)                                                => SectionStatus.NotStarted
      case Some(rcs) if !calculationRequired(rcs, answers) && LTASection.kickoutHasBeenReached(answers)  =>
        SectionStatus.CannotStartYet
      case Some(rcs) if !calculationRequired(rcs, answers) && !LTASection.kickoutHasBeenReached(answers) =>
        SectionStatus.NotStarted
      case _                                                                                             => SectionStatus.CannotStartYet
    }

  def navigateTo(answers: UserAnswers): String =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs, answers)  =>
        if (config.calculationReviewEnabled) {
          routes.CalculationReviewController.onPageLoad().url
        } else {
          routes.CalculationResultController.onPageLoad().url
        }
      case Some(rcs) if !calculationRequired(rcs, answers) =>
        routes.SubmissionController.storeAndRedirect().url
      case _                                               => SetupSection.navigateTo(answers)
    }

  def sectionNameOverride(answers: UserAnswers): String =
    if (answers.get(PostTriageFlag).isDefined) {
      postTriageSectionNameOverride(answers)
    } else {
      preTriageSectionNameOverride(answers)
    }

  private def preTriageSectionNameOverride(answers: UserAnswers): String  =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs, answers)                                               => "taskList.nextSteps.calculate"
      case Some(rcs)
          if !calculationRequired(rcs, answers) && !LTASection.kickoutHasBeenReached(
            answers
          ) && answers.authenticated =>
        "taskList.nextSteps.continue"
      case Some(rcs)
          if !calculationRequired(rcs, answers) && !LTASection.kickoutHasBeenReached(
            answers
          ) && !answers.authenticated =>
        "taskList.nextSteps.continueToSignIn"
      case Some(rcs) if !calculationRequired(rcs, answers) && LTASection.kickoutHasBeenReached(answers) =>
        "taskList.nextSteps.noFurtherAction"
      case _                                                                                            => "taskList.nextSteps.setupRequired"
    }
  private def postTriageSectionNameOverride(answers: UserAnswers): String =
    (
      answers.get(AAKickOutStatus()),
      answers.get(LTAKickOutStatus()),
      LTASection.kickoutHasBeenReached(answers),
      answers.authenticated
    ) match {
      case (Some(2), _, _, _)           => "taskList.nextSteps.calculate"
      case (Some(0), Some(2), true, _)  => "taskList.nextSteps.noFurtherAction"
      case (None, Some(2), true, _)     => "taskList.nextSteps.noFurtherAction"
      case (Some(0), Some(2), _, true)  => "taskList.nextSteps.continue"
      case (None, Some(2), _, true)     => "taskList.nextSteps.continue"
      case (Some(0), Some(2), _, false) => "taskList.nextSteps.continueToSignIn"
      case (None, Some(2), _, false)    => "taskList.nextSteps.continueToSignIn"
      case (Some(0), Some(0), _, _)     => "taskList.nextSteps.noFurtherAction"
      case (Some(0), None, _, _)        => "taskList.nextSteps.noFurtherAction"
      case (None, Some(0), _, _)        => "taskList.nextSteps.noFurtherAction"
      case (_, _, _, _)                 => "taskList.nextSteps.setupRequired"
    }

  private def calculationRequired(reportingChangeSet: Set[ReportingChange], answers: UserAnswers): Boolean =
    if (answers.get(PostTriageFlag).isDefined) {
      reportingChangeSet.contains(ReportingChange.AnnualAllowance) && answers.get(AAKickOutStatus()).contains(2)
    } else {
      reportingChangeSet.contains(ReportingChange.AnnualAllowance)
    }
}
