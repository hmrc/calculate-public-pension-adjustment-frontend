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

package controllers.annualallowance.preaaquestions

import controllers.actions._
import forms.annualallowance.preaaquestions.ScottishTaxpayerFrom2016FormProvider
import models.Mode
import models.tasklist.sections.PreAASection
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.preaaquestions.ScottishTaxpayerFrom2016View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ScottishTaxpayerFrom2016Controller @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ScottishTaxpayerFrom2016FormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ScottishTaxpayerFrom2016View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(ScottishTaxpayerFrom2016Page) match {
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
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ScottishTaxpayerFrom2016Page, value))
              redirectUrl     = ScottishTaxpayerFrom2016Page.navigate(mode, updatedAnswers).url
              answersWithNav  = PreAASection.saveNavigation(updatedAnswers, redirectUrl)
              _              <- sessionRepository.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

}
