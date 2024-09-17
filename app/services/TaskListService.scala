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

package services

import models.tasklist._
import models.tasklist.sections._
import models.{AAKickOutStatus, LTAKickOutStatus, Period, PostTriageFlag, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage

import javax.inject.Inject

class TaskListService @Inject() (
) {

  def taskListViewModel(answers: UserAnswers): TaskListViewModel = {

    val setupGroup: SectionGroupViewModel = setupGroupSeq(answers)

    val aaGroup: Option[SectionGroupViewModel] =
      if (answers.get(PostTriageFlag).isDefined) {
        aaGroupPostTriageGroupGenerator(answers)
      } else {
        aaGroupPreTriageGroupGenerator(answers)
      }

    val ltaGroup: Option[SectionGroupViewModel] =
      if (answers.get(PostTriageFlag).isDefined) {
        postTriageLTAGroupGenerator(answers)
      } else {
        preTriageLTAGroupGenerator(answers)
      }

    val nextStepsGroup: SectionGroupViewModel = nextStepsGroupSeq(answers, List(Some(setupGroup), aaGroup, ltaGroup))

    TaskListViewModel(setupGroup, aaGroup, ltaGroup, nextStepsGroup)
  }

  private def postTriageLTAGroupGenerator(answers: UserAnswers) =
    if (answers.get(LTAKickOutStatus()).contains(2)) {
      ltaGroupSeq(answers)
    } else {
      None
    }

  private def preTriageLTAGroupGenerator(answers: UserAnswers) =
    ltaGroupSeq(answers)

  private def aaGroupPostTriageGroupGenerator(answers: UserAnswers) =
    if (isRequired(answers, ReportingChange.AnnualAllowance) && answers.get(AAKickOutStatus()).contains(2)) {
      val aaPeriods: Seq[Period]                  = PeriodService.relevantPeriods(answers)
      val aaPeriodSections: Seq[SectionViewModel] = aaPeriodSectionsSeq(answers, aaPeriods)
      Some(aaGroupSeq(answers, aaPeriodSections))
    } else {
      None
    }

  private def aaGroupPreTriageGroupGenerator(answers: UserAnswers) =
    if (isRequired(answers, ReportingChange.AnnualAllowance)) {
      val aaPeriods: Seq[Period]                  = PeriodService.relevantPeriods(answers)
      val aaPeriodSections: Seq[SectionViewModel] = aaPeriodSectionsSeq(answers, aaPeriods)
      Some(aaGroupSeq(answers, aaPeriodSections))
    } else {
      None
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
          SetupSection.navigateTo(answers),
          SetupSection.status(answers),
          "setup-questions"
        )
      )
    )

  private def aaPeriodSectionsSeq(answers: UserAnswers, relevantAAPeriods: Seq[Period]): Seq[SectionViewModel] =
    relevantAAPeriods.map(period =>
      SectionViewModel(
        s"taskList.aa.sectionNameFor${period.toString}",
        AASection(period).navigateTo(answers),
        AASection(period).status(answers),
        s"annual-allowance-details-${period.toString}"
      )
    )

  private def aaGroupSeq(
    answers: UserAnswers,
    aaPeriodSections: Seq[SectionViewModel]
  ): SectionGroupViewModel = {

    val preAASection =
      Seq(
        SectionViewModel(
          "taskList.aa.setup.sectionName",
          PreAASection.navigateTo(answers),
          PreAASection.status(answers),
          "annual-allowance-setup-questions"
        )
      )

    val aaSections = if (PreAASection.status(answers) == SectionStatus.Completed) {
      preAASection ++ aaPeriodSections
    } else {
      preAASection
    }

    SectionGroupViewModel(
      "taskList.aa.groupHeading",
      aaSections
    )
  }

  private def ltaGroupSeq(answers: UserAnswers): Option[SectionGroupViewModel] =
    if (isRequired(answers, ReportingChange.LifetimeAllowance)) {
      Some(
        SectionGroupViewModel(
          "taskList.lta.groupHeading",
          Seq(
            SectionViewModel(
              "taskList.lta.sectionName",
              LTASection.navigateTo(answers),
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
