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
import forms.annualallowance.preaaquestions.FlexibleAccessStartDateFormProvider
import models.Mode
import models.requests.DataRequest
import models.tasklist.sections.PreAASection
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, StopPayingPublicPensionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.preaaquestions.FlexibleAccessStartDateView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FlexibleAccessStartDateController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FlexibleAccessStartDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FlexibleAccessStartDateView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val FLEXIBLE_ACCESS_DATE_MAX_YEAR  = 2023
  private val FLEXIBLE_ACCESS_DATE_MAX_MONTH = 4
  private val FLEXIBLE_ACCESS_DATE_MAX_DAY   = 5

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val form         = formProvider(getFlexibleAccessDateMax(request))
    val preparedForm = request.userAnswers.get(FlexibleAccessStartDatePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      formProvider(getFlexibleAccessDateMax(request))
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FlexibleAccessStartDatePage, value))
              redirectUrl     = FlexibleAccessStartDatePage.navigate(mode, updatedAnswers).url
              answersWithNav  = PreAASection.saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

  private def getFlexibleAccessDateMax(request: DataRequest[AnyContent]): LocalDate = {
    var flexibleAccessDateMax =
      LocalDate.of(FLEXIBLE_ACCESS_DATE_MAX_YEAR, FLEXIBLE_ACCESS_DATE_MAX_MONTH, FLEXIBLE_ACCESS_DATE_MAX_DAY)

    if (request.userAnswers.get(StopPayingPublicPensionPage).isDefined) {
      flexibleAccessDateMax = request.userAnswers.get(StopPayingPublicPensionPage).get
    }

    flexibleAccessDateMax
  }
}
