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

import controllers.actions.*
import forms.annualallowance.taxyear.DefinedContributionAmountFormProvider
import models.requests.DataRequest
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContributionAmountPage, FlexiAccessDefinedContributionAmountPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.annualallowance.taxyear.DefinedContributionAmountView

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefinedContributionAmountController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DefinedContributionAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DefinedContributionAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
      val startEndDate      = getStartEndDate(period, flexibleStartDate)
      val form              = formProvider(Seq(startEndDate))
      val preparedForm      = request.userAnswers.get(DefinedContributionAmountPage(period)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, period, getStartEndDate(period, flexibleStartDate)))
    }

  // noinspection ScalaStyle
  def onSubmit(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)

      val flexiAccessExistsForPeriod = request.userAnswers.get(FlexibleAccessStartDatePage) match {
        case Some(date) => period.start.minusDays(1).isBefore(date) && period.end.plusDays(1).isAfter(date)
        case None       => false
      }

      val startEndDate = getStartEndDate(period, flexibleStartDate)
      val form         = formProvider(Seq(startEndDate))

      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(formWithErrors, mode, period, getStartEndDate(period, flexibleStartDate)))
            ),
          value =>
            if (flexiAccessExistsForPeriod) {
              if (flexibleStartDate == Some(period.end)) {
                updateAnswersForEndOfPeriodDate(mode, period, request, value)
              } else {
                updateAnswersForNormalDate(mode, period, request, value)
              }
            } else {
              updateAnswersForNormalDate(mode, period, request, value)
            }
        )
    }

  def updateAnswersForEndOfPeriodDate(
    mode: Mode,
    period: Period,
    request: DataRequest[AnyContent],
    value: BigInt
  ) = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      updatedAnswers <- Future.fromTry(
                          request.userAnswers
                            .set(DefinedContributionAmountPage(period), value)
                            .flatMap(_.set(FlexiAccessDefinedContributionAmountPage(period), BigInt(0)))
                        )
      redirectUrl     = DefinedContributionAmountPage(period).navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
      _              <- userDataService.set(answersWithNav)(hc)
    } yield Redirect(redirectUrl)
  }

  private def updateAnswersForNormalDate(
    mode: Mode,
    period: Period,
    request: DataRequest[AnyContent],
    value: BigInt
  ) = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(DefinedContributionAmountPage(period), value))
      redirectUrl     = DefinedContributionAmountPage(period).navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
      _              <- userDataService.set(answersWithNav)(hc)
    } yield Redirect(redirectUrl)
  }

  private def getStartEndDate(period: Period, flexibleStartDate: Option[LocalDate])(implicit
    messages: Messages
  ): String = {
    val languageTag = if (messages.lang.code == "cy") "cy" else "en"
    val formatter   = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(period.start) && date.isBefore(period.end) =>
          period.start.format(formatter) + " " + messages("startEndDateAnd") + " " + date.format(formatter)
        case _                                                                     =>
          period.start.format(formatter) + " " + messages("startEndDateAnd") + " " + period.end.format(formatter)
      }

    if (flexibleStartDate == Some(period.start)) {
      period.start.format(formatter) + " " + messages("startEndDateAnd") + " " + flexibleStartDate.get.format(formatter)
    } else {
      normalDateFormatter
    }
  }
}
