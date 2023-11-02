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

package models.tasklist

import base.SpecBase
import pages.behaviours.PageBehaviours

class TaskListViewModelSpec extends SpecBase with PageBehaviours {

  "task list view model" - {

    "group count should match the number of defined groups" in {
      val section: Seq[SectionViewModel]      =
        Seq(SectionViewModel("name", "url", SectionStatus.NotStarted, "id"))
      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", section)

      val taskListViewModel: TaskListViewModel =
        TaskListViewModel(sectionGroup, Some(sectionGroup), None, SectionGroupViewModel("heading", Seq()))

      taskListViewModel.groupCount must be(3)
    }

    "completed group count should be correct when a group with a single section is complete" in {
      val sections: Seq[SectionViewModel]     =
        Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, sectionGroup)

      taskListViewModel.groupCount          must be(2)
      taskListViewModel.completedGroupCount must be(2)
    }

    "completed group count should be correct for a group with multiple sections of which only one is complete" in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", "url", SectionStatus.InProgress, "id"),
        SectionViewModel("name", "url", SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, sectionGroup)

      taskListViewModel.groupCount          must be(2)
      taskListViewModel.completedGroupCount must be(0)
    }

    "completed group count should be correct for a group with multiple sections when all are complete" in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", "url", SectionStatus.Completed, "id"),
        SectionViewModel("name", "url", SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", sections)

      val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, sectionGroup)

      taskListViewModel.groupCount          must be(2)
      taskListViewModel.completedGroupCount must be(2)
    }

    "all counts should be correct when some sections are complete and some are incomplete " in {
      val sections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", "url", SectionStatus.Completed, "id"),
        SectionViewModel("name", "url", SectionStatus.Completed, "id")
      )

      val sectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", sections)

      val incompleteSections: Seq[SectionViewModel] = Seq(
        SectionViewModel("name", "url", SectionStatus.InProgress, "id"),
        SectionViewModel("name", "url", SectionStatus.Completed, "id")
      )

      val incompleteSectionGroup: SectionGroupViewModel = SectionGroupViewModel("heading", incompleteSections)

      val taskListViewModel: TaskListViewModel =
        TaskListViewModel(sectionGroup, Some(incompleteSectionGroup), None, incompleteSectionGroup)

      taskListViewModel.groupCount          must be(3)
      taskListViewModel.completedGroupCount must be(1)
    }
  }
}
