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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.DataRequest
import models.tasklist.SectionStatus.NotStarted
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import services.TaskListService

import scala.concurrent.{ExecutionContext, Future}

class RequireTasksCompletedActionImpl @Inject() (
  config: FrontendAppConfig,
  taskListService: TaskListService
)(implicit val executionContext: ExecutionContext)
    extends RequireTasksCompletedAction {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    val maybeReadyToCalculate = taskListService
      .taskListViewModel(request.userAnswers)
      .nextStepsGroup
      .sections
      .map(section => section.status == NotStarted)
      .head
    maybeReadyToCalculate match {
      case true => Future.successful(Right(DataRequest(request.request, request.userId, request.userAnswers)))
      case _    => Future.successful(Left(Redirect(config.redirectToOutstandingTasksPage)))
    }
  }
}

trait RequireTasksCompletedAction extends ActionRefiner[DataRequest, DataRequest]
