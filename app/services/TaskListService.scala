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
import models.tasklist.sections.{AASection, LTASection, NextStepsSection, PreAASection, SetupSection}
import models.{NormalMode, Period, ReportingChange, SchemeIndex, UserAnswers}
import pages.setupquestions.ReportingChangePage

import javax.inject.Inject

class TaskListService @Inject() (
) {

  def taskListViewModel(answers: UserAnswers): TaskListViewModel = {

    val setupGroup: SectionGroupViewModel = setupGroupSeq(answers)

    val aaGroup: Option[SectionGroupViewModel] =
      if (isRequired(answers, ReportingChange.AnnualAllowance)) {
        val aaPeriods: Seq[Period]                  = PeriodService.relevantPeriods(answers)
        val aaPeriodSections: Seq[SectionViewModel] = aaPeriodSectionsSeq(answers, aaPeriods)
        Some(aaGroupSeq(answers, aaPeriodSections))
      } else { None }

    val ltaGroup: Option[SectionGroupViewModel] = ltaGroupSeq(answers)

    val nextStepsGroup: SectionGroupViewModel = nextStepsGroupSeq(answers, List(Some(setupGroup), aaGroup, ltaGroup))

    TaskListViewModel(setupGroup, aaGroup, ltaGroup, nextStepsGroup)
  }

  def isRequired(answers: UserAnswers, reportingChange: ReportingChange): Boolean =
    answers.get(ReportingChangePage) match {
      case Some(set) if set.contains(reportingChange) => true
      case _                                          => false
    }

  private def setupGroupSeq(answers: UserAnswers): SectionGroupViewModel =
    SectionGroupViewModel(
      "taskList.setup.groupHeading",
      Seq(
        SectionViewModel(
          "taskList.setup.sectionName",
          SetupSection.navigateTo(answers).navigate(NormalMode, answers),
          SetupSection.status(answers),
          "setup-questions"
        )
      )
    )

  private def aaPeriodSectionsSeq(answers: UserAnswers, relevantAAPeriods: Seq[Period]): Seq[SectionViewModel] =
    relevantAAPeriods.map(period =>
      SectionViewModel(
        s"taskList.aa.sectionNameFor${period.toString}",
        AASection(period, SchemeIndex(0)).navigateTo(answers).navigate(NormalMode, answers),
        AASection(period, SchemeIndex(0)).status(answers),
        s"annual-allowance-details-${period.toString}"
      )
    )

  private def aaGroupSeq(
    answers: UserAnswers,
    aaPeriodSections: Seq[SectionViewModel]
  ): SectionGroupViewModel =
    SectionGroupViewModel(
      "taskList.aa.groupHeading",
      Seq(
        SectionViewModel(
          "taskList.aa.setup.sectionName",
          PreAASection.navigateTo(answers).navigate(NormalMode, answers),
          PreAASection.status(answers),
          "annual-allowance-setup-questions"
        )
      ) ++ aaPeriodSections
    )

  private def ltaGroupSeq(answers: UserAnswers): Option[SectionGroupViewModel] =
    if (isRequired(answers, ReportingChange.LifetimeAllowance)) {
      Some(
        SectionGroupViewModel(
          "taskList.lta.groupHeading",
          Seq(
            SectionViewModel(
              "taskList.lta.sectionName",
              LTASection.navigateTo(answers).navigate(NormalMode, answers),
              LTASection.status(answers),
              "lifetime-allowance-questions"
            )
          )
        )
      )
    } else { None }

  def nextStepsGroupSeq(
    answers: UserAnswers,
    dataCaptureSections: List[Option[SectionGroupViewModel]]
  ): SectionGroupViewModel = {

    val sectionNameOverride = NextStepsSection.sectionNameOverride(answers)

    SectionGroupViewModel(
      "taskList.nextSteps.groupHeading",
      Seq(
        SectionViewModel(
          sectionNameOverride,
          NextStepsSection.navigateTo(answers),
          NextStepsSection.sectionStatus(dataCaptureSections, answers),
          "next-steps-action",
          Some(sectionNameOverride)
        )
      )
    )
  }
}
