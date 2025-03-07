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

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.Done
import models.requests.{AuthenticatedIdentifierRequest, DataRequest}
import models.tasklist.TaskListViewModel
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{TaskListService, UserDataService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView,
  taskListService: TaskListService,
  userDataService: UserDataService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    updateAuthFlag(request).map { _ =>
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(request.userAnswers)

      Ok(view(form, taskListViewModel))
    }
  }

  def updateAuthFlag(request: DataRequest[AnyContent])(implicit hc: HeaderCarrier): Future[Done] =
    request.request match {
      case AuthenticatedIdentifierRequest(_, _) => userDataService.set(request.userAnswers.copy(authenticated = true))
      case _                                    => Future.successful(Done)
    }
}
