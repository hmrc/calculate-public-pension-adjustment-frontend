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

package controllers.annualallowance

import controllers.actions._
import forms.annualallowance.MemberMoreThanOnePensionFormProvider
import models.{Mode, Period}
import pages.annualallowance.MemberMoreThanOnePensionPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.MemberMoreThanOnePensionView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MemberMoreThanOnePensionController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: MemberMoreThanOnePensionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: MemberMoreThanOnePensionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      if (period.isAAPeriod) {
        val preparedForm = request.userAnswers.get(MemberMoreThanOnePensionPage(period)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, period))
      } else BadRequest(view(form.withGlobalError("error.invalid_aa_period"), mode, period))
  }

  def onSubmit(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      if (period.isAAPeriod) {
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(MemberMoreThanOnePensionPage(period), value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(MemberMoreThanOnePensionPage(period).navigate(mode, updatedAnswers))
          )
      } else Future.successful(BadRequest(view(form.withGlobalError("error.invalid_aa_period"), mode, period)))
  }
}
