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

import config.FrontendAppConfig
import controllers.actions._
import forms.ResubmittingAdjustmentFormProvider
import models.requests.{AuthenticatedIdentifierRequest, OptionalDataRequest}
import models.tasklist.sections.SetupSection
import models.{CalculationStartAuditEvent, Mode, UserAnswers}
import pages.setupquestions.{ResubmittingAdjustmentPage, SavingsStatementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AuditService, UserDataService}
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
  config: FrontendAppConfig,
  auditService: AuditService,
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
          for {
            updatedAnswers <- Future.fromTry(
                                request.userAnswers
                                  .getOrElse(constructUserAnswers(request))
                                  .set(SavingsStatementPage(config.optionalAuthEnabled), true)
                                  .get
                                  .set(ResubmittingAdjustmentPage, value)
                              )
            redirectUrl     = ResubmittingAdjustmentPage.navigate(mode, updatedAnswers).url
            answersWithNav  = SetupSection.saveNavigation(updatedAnswers, redirectUrl)
            _              <- userDataService.set(answersWithNav)
            _              <- auditService.auditCalculationStart(
                                CalculationStartAuditEvent(answersWithNav.uniqueId, answersWithNav.authenticated)
                              )
          } yield Redirect(redirectUrl)
      )
  }

  private def constructUserAnswers(request: OptionalDataRequest[AnyContent]) = {
    val authenticated = request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }
    UserAnswers(request.userId, authenticated = authenticated)
  }
}
