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

package controllers.annualallowance.taxyear

import controllers.actions._
import forms.annualallowance.taxyear.ClaimingTaxReliefPensionNotAdjustedIncomeFormProvider
import models.tasklist.sections.AASection

import javax.inject.Inject
import models.{Mode, Period}
import pages.annualallowance.taxyear.ClaimingTaxReliefPensionNotAdjustedIncomePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import services.UserDataService
import views.html.annualallowance.taxyear.ClaimingTaxReliefPensionNotAdjustedIncomeView

import scala.concurrent.{ExecutionContext, Future}

class ClaimingTaxReliefPensionNotAdjustedIncomeController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ClaimingTaxReliefPensionNotAdjustedIncomeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ClaimingTaxReliefPensionNotAdjustedIncomeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, period))
  }

  def onSubmit(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(ClaimingTaxReliefPensionNotAdjustedIncomePage(period), value))
              redirectUrl     = ClaimingTaxReliefPensionNotAdjustedIncomePage(period).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }
}
