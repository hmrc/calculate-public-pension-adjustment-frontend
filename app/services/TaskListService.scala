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

package services

import models.tasklist._
import models.{NormalMode, Period, ReportingChange, UserAnswers}

import javax.inject.Inject

class TaskListService @Inject() (
  aaLogicService: AnnualAllowanceLogicService
) {

  def taskListViewModel(answers: UserAnswers): TaskListViewModel = {
    var counter = 1

    val setupGroup: SectionGroupViewModel = setupGroupSeq(answers, counter)
    counter += 1

    val aaGroup: Option[SectionGroupViewModel] =
      if (aaLogicService.isRequired(answers, ReportingChange.AnnualAllowance)) {
        val aaPeriods: Seq[Period]                  = aaLogicService.relevantPeriods(answers)
        val aaPeriodSections: Seq[SectionViewModel] = aaPeriodSectionsSeq(aaPeriods)
        Some(aaGroupSeq(answers, aaPeriodSections, counter))
      } else None

    if (aaGroup.isDefined) counter += 1
    val ltaGroup: Option[SectionGroupViewModel] = ltaGroupSeq(answers, counter)

    if (ltaGroup.isDefined) counter += 1
    val adminGroup: Option[SectionGroupViewModel] = adminGroupSeq(answers, counter)

    TaskListViewModel(setupGroup, aaGroup, ltaGroup, adminGroup)
  }

  private def setupGroupSeq(answers: UserAnswers, displayNumber: Int): SectionGroupViewModel =
    SectionGroupViewModel(
      displayNumber,
      "taskList.startupGroupHeading",
      Seq(
        SectionViewModel(
          "taskList.setupQuestion.changeDetails",
          SetupSection.returnTo(answers).navigate(NormalMode, answers),
          SetupSection.status(answers)
        )
      )
    )

  private def aaPeriodSectionsSeq(relevantAAPeriods: Seq[Period]): Seq[SectionViewModel] =
    relevantAAPeriods.map(period =>
      SectionViewModel(
        s"taskList.aa.detailsFor${period.toString}",
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad,
        SectionStatus.NotStarted
      )
    )

  private def aaGroupSeq(
    answers: UserAnswers,
    aaPeriodSections: Seq[SectionViewModel],
    displayNumber: Int
  ): SectionGroupViewModel =
    SectionGroupViewModel(
      displayNumber,
      "taskList.aaGroupHeading",
      Seq(
        SectionViewModel(
          s"taskList.aa.preAAQuestion",
          PreAASection.returnTo(answers).navigate(NormalMode, answers),
          PreAASection.status(answers)
        )
      ) ++ aaPeriodSections
    )

  private def adminGroupSeq(answers: UserAnswers, displayNumber: Int): Option[SectionGroupViewModel] =
    if (aaLogicService.isRequired(answers, ReportingChange.OtherCompensation)) {
      Some(
        SectionGroupViewModel(
          displayNumber,
          "taskList.adminGroupHeading",
          Seq(
            SectionViewModel(
              "taskList.adminFees.administrationFees",
              controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad,
              SectionStatus.Completed
            )
          )
        )
      )
    } else None

  private def ltaGroupSeq(answers: UserAnswers, displayNumber: Int): Option[SectionGroupViewModel] =
    if (aaLogicService.isRequired(answers, ReportingChange.LifetimeAllowance)) {
      Some(
        SectionGroupViewModel(
          displayNumber,
          "taskList.ltaGroupHeading",
          Seq(
            SectionViewModel(
              "taskList.lta.addDetails",
              controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad,
              SectionStatus.NotStarted
            )
          )
        )
      )
    } else None
}
