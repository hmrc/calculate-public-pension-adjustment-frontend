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
import forms.annualallowance.taxyear.AddAnotherSchemeFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period, SchemeIndex}
import pages.annualallowance.taxyear.AddAnotherSchemePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.AddAnotherSchemeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherSchemeController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AddAnotherSchemeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherSchemeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(AddAnotherSchemePage(period, schemeIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, period, schemeIndex))
    }

  def onSubmit(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period, schemeIndex))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(AddAnotherSchemePage(period, schemeIndex), value))
              redirectUrl     = AddAnotherSchemePage(period, schemeIndex).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}
