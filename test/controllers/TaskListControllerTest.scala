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

package controllers

import base.SpecBase
import models.UserAnswers
import models.tasklist.{SectionGroupViewModel, SectionStatus, SectionViewModel, TaskListViewModel}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import repositories.SessionRepository
import services.TaskListService

import scala.concurrent.Future
class TaskListControllerTest extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val normalRoute = routes.TaskListController.onPageLoad.url

  "TaskList Controller" - {

    "submission must succeed when all sections are complete" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val mockTaskListService: TaskListService = whenAllSectionsAre(SectionStatus.Completed)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Your calculation will be available soon.") mustBe true
      }
    }

    "submission must fail if sections are incomplete" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val mockTaskListService: TaskListService = whenAllSectionsAre(SectionStatus.InProgress)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result).contains("You have tasks that you need to complete") mustBe true
      }
    }
  }

  private def whenAllSectionsAre(sectionStatus: SectionStatus): TaskListService = {
    val mockTaskListService: TaskListService = mock[TaskListService]

    val taskListViewModel: TaskListViewModel = constructTaskListViewModel(sectionStatus)

    when(mockTaskListService.taskListViewModel(any[UserAnswers])).thenReturn(taskListViewModel)

    mockTaskListService
  }

  private def constructTaskListViewModel(sectionStatus: SectionStatus) = {
    val sections: Seq[SectionViewModel]      = Seq(SectionViewModel("name", Call("method", "url"), sectionStatus))
    val sectionGroup: SectionGroupViewModel  = SectionGroupViewModel(1, "heading", sections)
    val taskListViewModel: TaskListViewModel = TaskListViewModel(sectionGroup, None, None, None)
    taskListViewModel
  }
}
