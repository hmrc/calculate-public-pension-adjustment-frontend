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
import models.{NormalMode, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.mvc.Call

case object NextStepsSection extends Section {
  def sectionStatus(dataCaptureSections: List[Option[SectionGroupViewModel]]): SectionStatus = {
    val allDataCaptureComplete: Boolean = dataCaptureSections.flatten.forall(_.isComplete)

    if (allDataCaptureComplete) {
      SectionStatus.NotStarted
    } else {
      SectionStatus.CannotStartYet
    }
  }

  def navigateTo(answers: UserAnswers, dataCaptureSections: List[Option[SectionGroupViewModel]]): Call =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs) => routes.CalculationResultController.onPageLoad()
      case Some(_)                               => routes.SubmissionController.storeAndRedirect()
      case None                                  => controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode)
    }

  def sectionNameOverride(answers: UserAnswers) =
    answers.get(ReportingChangePage) match {
      case Some(rcs) if calculationRequired(rcs) => "taskList.nextSteps.calculate"
      case Some(_)                               => "taskList.nextSteps.continueToSignIn"
      case None                                  => "taskList.nextSteps.setupRequired"
    }

  private def calculationRequired(reportingChangeSet: Set[ReportingChange]): Boolean =
    reportingChangeSet.contains(ReportingChange.AnnualAllowance)
}
