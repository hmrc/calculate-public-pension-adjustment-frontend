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

package controllers.setupquestions

import controllers.actions._
import forms.ResubmittingAdjustmentFormProvider
import models.requests.{AuthenticatedIdentifierRequest, OptionalDataRequest}
import models.tasklist.sections.SetupSection
import models.{Mode, NormalMode, PostTriageFlag, UserAnswers}
import pages.setupquestions.ResubmittingAdjustmentPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.setupquestions.ResubmittingAdjustmentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ResubmittingAdjustmentController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: ResubmittingAdjustmentFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ResubmittingAdjustmentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    val preparedForm =
      request.userAnswers.getOrElse(constructUserAnswers(request)).get(ResubmittingAdjustmentPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          if (mode == NormalMode) {
            normalModeRedirectGenerator(mode, request, value)
          } else {
            checkModeRedirectGenerator(mode, request, value)
          }
      )
  }

  private def normalModeRedirectGenerator(mode: Mode, request: OptionalDataRequest[AnyContent], value: Boolean)(implicit
    hc: HeaderCarrier
  ) =
    for {
      updatedAnswers <- Future.fromTry(
                          request.userAnswers
                            .getOrElse(constructUserAnswers(request))
                            .set(ResubmittingAdjustmentPage, value)
                        )
      redirectUrl     = ResubmittingAdjustmentPage.navigate(mode, updatedAnswers).url
      answersWithNav  = SetupSection.saveNavigation(updatedAnswers, redirectUrl)
      answersWithFlag = PostTriageFlag.setStatusTrue(answersWithNav)
      _              <- userDataService.set(answersWithFlag)
    } yield Redirect(redirectUrl)

  private def checkModeRedirectGenerator(mode: Mode, request: OptionalDataRequest[AnyContent], value: Boolean)(implicit
    hc: HeaderCarrier
  ) =
    for {
      updatedAnswers <- Future.fromTry(
                          request.userAnswers
                            .getOrElse(constructUserAnswers(request))
                            .set(ResubmittingAdjustmentPage, value)
                        )
      redirectUrl     = ResubmittingAdjustmentPage.navigate(mode, updatedAnswers).url
      answersWithNav  = SetupSection.saveNavigation(updatedAnswers, redirectUrl)
      _              <- userDataService.set(answersWithNav)
    } yield Redirect(redirectUrl)

  private def constructUserAnswers(request: OptionalDataRequest[AnyContent]) = {
    val authenticated = request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }
    UserAnswers(request.userId, authenticated = authenticated)
  }
}
