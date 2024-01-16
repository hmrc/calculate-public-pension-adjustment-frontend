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
import forms.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountView

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefinedContribution2016PreFlexiAmountController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DefinedContribution2016PreFlexiAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DefinedContribution2016PreFlexiAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
    val startEndDate      = getStartEndDate(flexibleStartDate)
    val form              = formProvider(Seq(startEndDate))
    val preparedForm      = request.userAnswers.get(DefinedContribution2016PreFlexiAmountPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, startEndDate))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
      val startEndDate      = getStartEndDate(flexibleStartDate)
      val form              = formProvider(Seq(startEndDate))
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, startEndDate))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(DefinedContribution2016PreFlexiAmountPage, value))
              redirectUrl     = DefinedContribution2016PreFlexiAmountPage.navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(Period._2016).saveNavigation(updatedAnswers, redirectUrl)
              _              <- sessionRepository.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
  }

  private def getStartEndDate(flexibleStartDate: Option[LocalDate])(implicit messages: Messages): String = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(Period.pre2016Start) && date.isBefore(Period.pre2016End) =>
          date.plusDays(1).format(formatter) + " " + messages("startEndDateAnd") + " " + Period.pre2016End.format(
            formatter
          )
        case _                                                                                   =>
          Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + Period.pre2016End.format(
            formatter
          )
      }

    if (flexibleStartDate == Some(Period.pre2016Start)) {
      flexibleStartDate.get.plusDays(1).format(formatter) + " " + messages("startEndDateAnd") + " " + Period.pre2016End
        .format(formatter)
    } else {
      normalDateFormatter
    }
  }
}
