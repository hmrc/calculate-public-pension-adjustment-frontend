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

package controllers.lifetimeallowance

import controllers.actions._
import forms.lifetimeallowance.ChangeInTaxChargeFormProvider
import models.Mode
import models.tasklist.sections.LTASection
import pages.lifetimeallowance.ChangeInTaxChargePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.lifetimeallowance.ChangeInTaxChargeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChangeInTaxChargeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ChangeInTaxChargeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ChangeInTaxChargeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(ChangeInTaxChargePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangeInTaxChargePage, value))
              redirectUrl     = ChangeInTaxChargePage.navigate(mode, updatedAnswers).url
              answersWithNav  = LTASection.saveNavigation(updatedAnswers, redirectUrl)
              _              <- sessionRepository.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }
}
