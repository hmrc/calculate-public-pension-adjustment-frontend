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
import controllers.routes
import forms.annualallowance.taxyear.DoYouKnowPersonalAllowanceFormProvider
import models.{Mode, NormalMode, Period}
import models.tasklist.sections.AASection
import pages.annualallowance.taxyear.DoYouKnowPersonalAllowancePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.DoYouKnowPersonalAllowanceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DoYouKnowPersonalAllowanceController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  calculateBackendService: CalculateBackendService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DoYouKnowPersonalAllowanceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DoYouKnowPersonalAllowanceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(DoYouKnowPersonalAllowancePage(period)) match {
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
              updatedAnswers <- Future.fromTry(request.userAnswers.set(DoYouKnowPersonalAllowancePage(period), value))
              redirectUrl     = DoYouKnowPersonalAllowancePage(period).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

  def checkBasicRate(period: Period): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      calculateBackendService.findTaxRateStatus(request.userAnswers, period).map {
        case true  =>
          Redirect(
            controllers.annualallowance.taxyear.routes.MarriageAllowanceController.onPageLoad(NormalMode, period)
          )
        case false =>
          Redirect(controllers.annualallowance.taxyear.routes.BlindAllowanceController.onPageLoad(NormalMode, period))
      }
  }
}
