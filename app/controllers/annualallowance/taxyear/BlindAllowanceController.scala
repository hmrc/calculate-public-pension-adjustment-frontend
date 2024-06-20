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
import forms.annualallowance.taxyear.BlindAllowanceFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.taxyear.BlindAllowancePage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.BlindAllowanceView

import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BlindAllowanceController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: BlindAllowanceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: BlindAllowanceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val form = formProvider(Seq(startEndDate(period)))

      val preparedForm = request.userAnswers.get(BlindAllowancePage(period)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, period, startEndDate(period)))
  }

  def onSubmit(mode: Mode, period: Period): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      formProvider(Seq(startEndDate(period)))
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period, startEndDate(period)))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(BlindAllowancePage(period), value))
              redirectUrl     = BlindAllowancePage(period).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

  private def startEndDate(period: Period)(implicit messages: Messages): String = {
    val languageTag = if (messages.lang.code == "cy") "cy" else "en"
    val formatter   = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))
    period.start.format(formatter) + " " + messages("startEndDateTo") + " " + period.end.format(formatter)
  }
}
