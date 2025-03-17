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
import forms.annualallowance.taxyear.DefinedContribution2016PostAmountFormProvider
import models.requests.DataRequest
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContribution2016PostAmountPage, DefinedContribution2016PostFlexiAmountPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.annualallowance.taxyear.DefinedContribution2016PostAmountView

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefinedContribution2016PostAmountController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DefinedContribution2016PostAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DefinedContribution2016PostAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
    val startEndDate      = getStartEndDate(flexibleStartDate)
    val form              = formProvider(Seq(startEndDate))
    val preparedForm      = request.userAnswers.get(DefinedContribution2016PostAmountPage) match {
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

      val flexiAccessExistsForPeriod = request.userAnswers.get(FlexibleAccessStartDatePage) match {
        case Some(date) =>
          Period.post2016Start.minusDays(1).isBefore(date) && Period.post2016End.plusDays(1).isAfter(date)
        case None       => false
      }

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, startEndDate))),
          value =>
            if (flexiAccessExistsForPeriod) {
              if (flexibleStartDate == Some(Period.post2016End)) {
                updateAnswersForEndOfPeriodDate(mode, request, value)
              } else {
                updateAnswersForNormalDate(mode, request, value)
              }
            } else {
              updateAnswersForNormalDate(mode, request, value)
            }
        )
  }

  def updateAnswersForEndOfPeriodDate(mode: Mode, request: DataRequest[AnyContent], value: BigInt) = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      updatedAnswers <- Future.fromTry(
                          request.userAnswers
                            .set(DefinedContribution2016PostAmountPage, value)
                            .flatMap(_.set(DefinedContribution2016PostFlexiAmountPage, BigInt(0)))
                        )
      redirectUrl     = DefinedContribution2016PostAmountPage.navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(Period._2016).saveNavigation(updatedAnswers, redirectUrl)
      _              <- userDataService.set(answersWithNav)(hc)
    } yield Redirect(redirectUrl)
  }

  private def updateAnswersForNormalDate(mode: Mode, request: DataRequest[AnyContent], value: BigInt) = {
    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(DefinedContribution2016PostAmountPage, value))
      redirectUrl     = DefinedContribution2016PostAmountPage.navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(Period._2016).saveNavigation(updatedAnswers, redirectUrl)
      _              <- userDataService.set(answersWithNav)(hc)
    } yield Redirect(redirectUrl)
  }

  private def getStartEndDate(flexibleStartDate: Option[LocalDate])(implicit messages: Messages): String = {
    val languageTag = if (messages.lang.code == "cy") "cy" else "en"
    val formatter   = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(Period.post2016Start) && date.isBefore(Period.post2016End) =>
          Period.post2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + date.format(formatter)
        case _                                                                                     =>
          Period.post2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + Period.post2016End.format(
            formatter
          )
      }

    if (flexibleStartDate == Some(Period.post2016Start)) {
      Period.post2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + flexibleStartDate.get.format(
        formatter
      )
    } else {
      normalDateFormatter
    }
  }
}
