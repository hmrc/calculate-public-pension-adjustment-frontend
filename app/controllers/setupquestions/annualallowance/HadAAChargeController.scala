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

package controllers.setupquestions.annualallowance

import controllers.actions._
import forms.setupquestions.annualallowance.HadAAChargeFormProvider
import models.{AAKickOutStatus, Mode}
import models.tasklist.sections.SetupSection
import pages.setupquestions.annualallowance.{HadAAChargePage, SavingsStatementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.setupquestions.annualallowance.HadAAChargeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HadAAChargeController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: HadAAChargeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: HadAAChargeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form                           = formProvider()
  private val kickOutStatusFalse     = 1
  private val kickOutStatusCompleted = 2

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(HadAAChargePage) match {
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
          value => {
            val revisedPSSStatus = request.userAnswers.get(SavingsStatementPage).get
            val aaKickOutStatus  = if (value && revisedPSSStatus) kickOutStatusCompleted else kickOutStatusFalse
            for {
              updatedAnswers          <- Future.fromTry(request.userAnswers.set(HadAAChargePage, value))
              updatedAnswersWithStatus = AAKickOutStatus().saveAAKickOutStatus(updatedAnswers, aaKickOutStatus)
              redirectUrl              = HadAAChargePage.navigate(mode, updatedAnswersWithStatus).url
              answersWithNav           = SetupSection.saveNavigation(updatedAnswersWithStatus, redirectUrl)
              _                       <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
          }
        )
  }
}