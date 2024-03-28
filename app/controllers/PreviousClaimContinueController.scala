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

import controllers.actions._
import forms.PreviousClaimContinueFormProvider

import javax.inject.Inject
import models.{Mode, UserAnswers}
import models.requests.{AuthenticatedIdentifierRequest, DataRequest, OptionalDataRequest}
import pages.PreviousClaimContinuePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PreviousClaimContinueView

import scala.concurrent.{ExecutionContext, Future}

class PreviousClaimContinueController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PreviousClaimContinueFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PreviousClaimContinueView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, navigateToSubmission: Boolean): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      Ok(view(form, mode))
    }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value => {

            val userAnswers =
              if (!value) {
                userDataService.clear()
                constructUserAnswers(request)
              } else {
                request.userAnswers
              }

            for {
              updatedAnswers <- Future.fromTry(userAnswers.set(PreviousClaimContinuePage, value))
              _              <- userDataService.set(updatedAnswers)
            } yield Redirect(PreviousClaimContinuePage.navigate(mode, updatedAnswers))
          }
        )
  }

  private def constructUserAnswers(request: DataRequest[AnyContent]) = {
    val authenticated = request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }
    UserAnswers(request.userId, authenticated = authenticated)
  }
}
