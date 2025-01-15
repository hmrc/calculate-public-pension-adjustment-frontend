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
import forms.setupquestions.ReportingChangeFormProvider
import models.tasklist.sections.SetupSection
import models.{AAKickOutStatus, LTAKickOutStatus, Mode, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.setupquestions.ReportingChangeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReportingChangeController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReportingChangeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ReportingChangeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  private val kickOutStatusFalse = 1

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(ReportingChangePage) match {
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
              updatedAnswers             <- Future.fromTry(request.userAnswers.set(ReportingChangePage, value))
              updatedAnswersWithAAStatus  = generateAAKickOutStatus(value, updatedAnswers)
              updatedAnswersWithLTAStatus = generateLTAKickOutStatus(value, updatedAnswersWithAAStatus)
              redirectUrl                 = ReportingChangePage.navigate(mode, updatedAnswersWithLTAStatus).url
              answersWithNav              = SetupSection.saveNavigation(updatedAnswersWithLTAStatus, redirectUrl)
              _                          <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

  private def generateAAKickOutStatus(value: Set[ReportingChange], userAnswers: UserAnswers): UserAnswers  =
    if (value.contains(ReportingChange.AnnualAllowance)) {
      AAKickOutStatus().saveAAKickOutStatus(userAnswers, kickOutStatusFalse)
    } else {
      userAnswers
    }
  private def generateLTAKickOutStatus(value: Set[ReportingChange], userAnswers: UserAnswers): UserAnswers =
    if (value.contains(ReportingChange.LifetimeAllowance)) {
      LTAKickOutStatus().saveLTAKickOutStatus(userAnswers, kickOutStatusFalse)
    } else {
      userAnswers
    }
}
