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
import forms.annualallowance.taxyear.FlexiAccessDefinedContributionAmountFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.FlexiAccessDefinedContributionAmountPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.FlexiAccessDefinedContributionAmountView

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FlexiAccessDefinedContributionAmountController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FlexiAccessDefinedContributionAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FlexiAccessDefinedContributionAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
      val startEndDate      = getStartEndDate(period, flexibleStartDate)
      val form              = formProvider(Seq(startEndDate))
      val preparedForm      = request.userAnswers.get(FlexiAccessDefinedContributionAmountPage(period)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mode, period, getStartEndDate(period, flexibleStartDate)))
    }

  def onSubmit(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
      val startEndDate      = getStartEndDate(period, flexibleStartDate)
      val form              = formProvider(Seq(startEndDate))
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(formWithErrors, mode, period, getStartEndDate(period, flexibleStartDate)))
            ),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(FlexiAccessDefinedContributionAmountPage(period), value))
              redirectUrl     = FlexiAccessDefinedContributionAmountPage(period).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }

  private def getStartEndDate(period: Period, flexibleStartDate: Option[LocalDate])(implicit
    messages: Messages
  ): String = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(period.start) && date.isBefore(period.end) =>
          date.plusDays(1).format(formatter) + " " + messages("startEndDateAnd") + " " + period.end.format(formatter)
        case _                                                                     =>
          period.start.format(formatter) + " " + messages("startEndDateAnd") + " " + period.end.format(formatter)
      }

    if (flexibleStartDate == Some(period.start)) {
      flexibleStartDate.get.plusDays(1).format(formatter) + " " + messages("startEndDateAnd") + " " + period.end.format(
        formatter
      )
    } else {
      normalDateFormatter
    }
  }
}
