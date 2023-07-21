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

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.{SectionGroupViewModel, SectionStatus, SectionViewModel, TaskListViewModel}
import models.{ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.mvc.Call

import scala.util.Try

class TaskListServiceSpec extends SpecBase {

  val taskListService: TaskListService = new TaskListService

  "Annual allowance reporting" - {

    " is required when specified in UserAnswers" in {

      val reportingChangeWithAA: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                     = emptyUserAnswers.set(ReportingChangePage, reportingChangeWithAA)

      taskListService.isRequired(answers.get, ReportingChange.AnnualAllowance) must be(true)
    }

    "is not required when not specified in UserAnswers" in {

      val reportingChangeWithoutAA: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers                                        = emptyUserAnswers.set(ReportingChangePage, reportingChangeWithoutAA)

      taskListService.isRequired(answers.get, ReportingChange.AnnualAllowance) must be(false)
    }
  }

  "when the view model is constructed" - {

    "groups should exist per reporting change plus setup" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.setupGroup mustNot be(null)
      taskListViewModel.aaGroup.isDefined  must be(true)
      taskListViewModel.ltaGroup.isDefined must be(true)
    }

    "display numbers should be correct" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.setupGroup.displayNumber   must be(1)
      taskListViewModel.ltaGroup.get.displayNumber must be(2)
    }

    "headings must be defined" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.setupGroup.heading   must be("taskList.setup.groupHeading")
      taskListViewModel.aaGroup.get.heading  must be("taskList.aa.groupHeading")
      taskListViewModel.ltaGroup.get.heading must be("taskList.lta.groupHeading")
    }

    "sections must be fully populated" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.setupGroup.sections.size mustBe 1
      taskListViewModel.aaGroup.get.sections.size mustBe 10 // one for each of the remedy periods plus one for the setup questions
      taskListViewModel.ltaGroup.get.sections.size mustBe 1
    }

    "sections must be well formed" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)
      val allSectionsInAllGroups               = taskListViewModel.allGroups.flatMap(groupOption => groupOption.get.sections)

      allSectionsInAllGroups.foreach { sectionViewModel: SectionViewModel =>
        sectionViewModel.status                       must not be null
        sectionViewModel.name.startsWith("taskList.") must be(true)
        sectionViewModel.call                         must not be null
      }
    }
  }

  "task list view model" - {

    "group count should match the number of defined groups" in {
      val section: Seq[SectionViewModel]      =
        Seq(SectionViewModel("name", Call("method", "url"), SectionStatus.NotStarted, "id"))
      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", section)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, Some(sectionGroup), None, None)

      taskListViewModel.groupCount must be(2)
    }

    "completed group count should be correct when a group with a single section is complete" in {
      val sections: Seq[SectionViewModel]     =
        Seq(SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id"))
      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, None)

      taskListViewModel.groupCount          must be(1)
      taskListViewModel.completedGroupCount must be(1)
    }

    "completed group count should be correct for a group with multiple sections of which only one is complete" in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", Call("method", "url"), SectionStatus.InProgress, "id"),
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, None)

      taskListViewModel.groupCount          must be(1)
      taskListViewModel.completedGroupCount must be(0)
    }

    "completed group count should be correct for a group with multiple sections when all are complete" in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id"),
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, None)

      taskListViewModel.groupCount          must be(1)
      taskListViewModel.completedGroupCount must be(1)
    }

    "all counts should be correct when some sections are complete and some are incomplete " in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id"),
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", sections)

      val incompleteSections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", Call("method", "url"), SectionStatus.InProgress, "id"),
        SectionViewModel("name", Call("method", "url"), SectionStatus.Completed, "id")
      )

      val incompleteSectionGroup: SectionGroupViewModel = SectionGroupViewModel(1, "heading", incompleteSections)

      val taskListViewModel: TaskListViewModel =
        TaskListViewModel(sectionGroup, Some(incompleteSectionGroup), None, None)

      taskListViewModel.groupCount          must be(2)
      taskListViewModel.completedGroupCount must be(1)
    }
  }
}
