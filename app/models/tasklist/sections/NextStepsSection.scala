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

package models.tasklist.sections

import controllers.routes
import models.tasklist.{Section, SectionGroupViewModel, SectionStatus}
import models.{ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage

case object NextStepsSection extends Section {

  def sectionStatus(dataCaptureSections: List[Option[SectionGroupViewModel]], answers: UserAnswers): SectionStatus = {
    val allDataCaptureComplete: Boolean = dataCaptureSections.flatten.forall(_.isComplete)

    if (allDataCaptureComplete) {
      answers.get(ReportingChangePage) match {
        case Some(rcs) if calculationRequired(rcs)                                                => SectionStatus.NotStarted
        case Some(rcs) if !calculationRequired(rcs) && LTASection.kickoutHasBeenReached(answers)  =>
          SectionStatus.CannotStartYet
        case Some(rcs) if !calculationRequired(rcs) && !LTASection.kickoutHasBeenReached(answers) =>
          SectionStatus.NotStarted
        case _                                                                                    => SectionStatus.CannotStartYet
      }
    } else {
      SectionStatus.CannotStartYet
    }

  }

  def navigateTo(answers: UserAnswers): String =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs)  => routes.CalculationResultController.onPageLoad().url
      case Some(rcs) if !calculationRequired(rcs) => routes.SubmissionController.storeAndRedirect().url
      case _                                      => SetupSection.navigateTo(answers)
    }

  def sectionNameOverride(answers: UserAnswers) =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs)                                                => "taskList.nextSteps.calculate"
      case Some(rcs) if !calculationRequired(rcs) && !LTASection.kickoutHasBeenReached(answers) =>
        "taskList.nextSteps.continueToSignIn"
      case Some(rcs) if !calculationRequired(rcs) && LTASection.kickoutHasBeenReached(answers)  =>
        "taskList.nextSteps.noFurtherAction"
      case _                                                                                    => "taskList.nextSteps.setupRequired"
    }

  private def calculationRequired(reportingChangeSet: Set[ReportingChange]): Boolean =
    reportingChangeSet.contains(ReportingChange.AnnualAllowance)

}
