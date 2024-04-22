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

package controllers.annualallowance.preaaquestions

import controllers.actions._
import forms.annualallowance.preaaquestions.StopPayingPublicPensionFormProvider
import models.{CheckMode, Mode}
import models.tasklist.sections.PreAASection
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, FlexiblyAccessedPensionPage, StopPayingPublicPensionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.preaaquestions.StopPayingPublicPensionView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class StopPayingPublicPensionController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: StopPayingPublicPensionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: StopPayingPublicPensionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(StopPayingPublicPensionPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      var userAnswers = request.userAnswers

      if (
        mode.equals(CheckMode) && request.userAnswers.get(FlexiblyAccessedPensionPage).isDefined
        && request.userAnswers.get(FlexiblyAccessedPensionPage).get
      ) {
        userAnswers = request.userAnswers
          .remove(FlexiblyAccessedPensionPage)
          .get
          .remove(FlexibleAccessStartDatePage)
          .get
      }

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(userAnswers.set(StopPayingPublicPensionPage, value))
              redirectUrl     = StopPayingPublicPensionPage.navigate(mode, updatedAnswers).url
              answersWithNav  = PreAASection.saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }
}
