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
import models.{NormalMode, Period, ReportingChange, SchemeIndex, UserAnswers}
import pages.setupquestions.ReportingChangePage

import javax.inject.Inject

class TaskListService @Inject() (
) {

  def taskListViewModel(answers: UserAnswers): TaskListViewModel = {
    var counter = 1

    val setupGroup: SectionGroupViewModel = setupGroupSeq(answers, counter)
    counter += 1

    val aaGroup: Option[SectionGroupViewModel] =
      if (isRequired(answers, ReportingChange.AnnualAllowance)) {
        val aaPeriods: Seq[Period]                  = PeriodService.relevantPeriods(answers)
        val aaPeriodSections: Seq[SectionViewModel] = aaPeriodSectionsSeq(answers, aaPeriods)
        Some(aaGroupSeq(answers, aaPeriodSections, counter))
      } else None

    if (aaGroup.isDefined) counter += 1
    val ltaGroup: Option[SectionGroupViewModel] = ltaGroupSeq(answers, counter)

    if (ltaGroup.isDefined) counter += 1
    val adminGroup: Option[SectionGroupViewModel] = adminGroupSeq(answers, counter)

    TaskListViewModel(setupGroup, aaGroup, ltaGroup, adminGroup)
  }

  def isRequired(answers: UserAnswers, reportingChange: ReportingChange): Boolean =
    answers.get(ReportingChangePage) match {
      case Some(set) if set.contains(reportingChange) => true
      case _                                          => false
    }

  private def setupGroupSeq(answers: UserAnswers, displayNumber: Int): SectionGroupViewModel =
    SectionGroupViewModel(
      displayNumber,
      "taskList.setup.groupHeading",
      Seq(
        SectionViewModel(
          "taskList.setup.sectionName",
          SetupSection.returnTo(answers).navigate(NormalMode, answers),
          SetupSection.status(answers),
          "setup-questions"
        )
      )
    )

  private def aaPeriodSectionsSeq(answers: UserAnswers, relevantAAPeriods: Seq[Period]): Seq[SectionViewModel] =
    relevantAAPeriods.map(period =>
      SectionViewModel(
        s"taskList.aa.sectionNameFor${period.toString}",
        AASection(period, SchemeIndex(0)).returnTo(answers).navigate(NormalMode, answers),
        AASection(period, SchemeIndex(0)).status(answers),
        s"annual-allowance-details-${period.toString}"
      )
    )

  private def aaGroupSeq(
    answers: UserAnswers,
    aaPeriodSections: Seq[SectionViewModel],
    displayNumber: Int
  ): SectionGroupViewModel =
    SectionGroupViewModel(
      displayNumber,
      "taskList.aa.groupHeading",
      Seq(
        SectionViewModel(
          s"taskList.aa.setup.sectionName",
          PreAASection.returnTo(answers).navigate(NormalMode, answers),
          PreAASection.status(answers),
          "annual-allowance-setup-questions"
        )
      ) ++ aaPeriodSections
    )

  private def adminGroupSeq(answers: UserAnswers, displayNumber: Int): Option[SectionGroupViewModel] =
    if (isRequired(answers, ReportingChange.OtherCompensation)) {
      Some(
        SectionGroupViewModel(
          displayNumber,
          "taskList.admin.groupHeading",
          Seq(
            SectionViewModel(
              "taskList.admin.sectionName",
              controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad(),
              SectionStatus.Completed,
              "admin-questions"
            )
          )
        )
      )
    } else None

  private def ltaGroupSeq(answers: UserAnswers, displayNumber: Int): Option[SectionGroupViewModel] =
    if (isRequired(answers, ReportingChange.LifetimeAllowance)) {
      Some(
        SectionGroupViewModel(
          displayNumber,
          "taskList.lta.groupHeading",
          Seq(
            SectionViewModel(
              "taskList.lta.sectionName",
              LTASection.returnTo(answers).navigate(NormalMode, answers),
              LTASection.status(answers),
              "lifetime-allowance-questions"
            )
          )
        )
      )
    } else None
}
