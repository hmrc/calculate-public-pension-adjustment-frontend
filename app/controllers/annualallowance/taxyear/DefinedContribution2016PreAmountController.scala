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
import forms.annualallowance.taxyear.DefinedContribution2016PreAmountFormProvider
import models.requests.DataRequest
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContribution2016PreAmountPage, DefinedContribution2016PreFlexiAmountPage, DefinedContributionAmountPage, FlexiAccessDefinedContributionAmountPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.DefinedContribution2016PreAmountView

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefinedContribution2016PreAmountController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DefinedContribution2016PreAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DefinedContribution2016PreAmountView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)
    val startEndDate      = getStartEndDate(flexibleStartDate)
    val form              = formProvider(Seq(startEndDate))
    val preparedForm      = request.userAnswers.get(DefinedContribution2016PreAmountPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }
    Ok(view(preparedForm, mode, startEndDate))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val flexibleStartDate          = request.userAnswers.get(FlexibleAccessStartDatePage)
      val startEndDate               = getStartEndDate(flexibleStartDate)
      val form                       = formProvider(Seq(startEndDate))
      val flexiAccessExistsForPeriod = request.userAnswers.get(FlexibleAccessStartDatePage) match {
        case Some(date) =>
          Period.pre2016Start.minusDays(1).isBefore(date) && Period.pre2016End.plusDays(1).isAfter(date)
        case None       => false
      }
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, startEndDate))),
          value =>
            if (flexiAccessExistsForPeriod) {
              if (flexibleStartDate == Some(Period.pre2016End)) {
                updateAnswersForEndOfPeriodDate(mode, request, value)
              } else {
                updateAnswersForNormalDate(mode, request, value)
              }
            } else {
              updateAnswersForNormalDate(mode, request, value)
            }
        )
  }

  def updateAnswersForEndOfPeriodDate(mode: Mode, request: DataRequest[AnyContent], value: BigInt) =
    for {
      updatedAnswers <- Future.fromTry(
                          request.userAnswers
                            .set(DefinedContribution2016PreAmountPage, value)
                            .flatMap(_.set(DefinedContribution2016PreFlexiAmountPage, BigInt(0)))
                        )
      redirectUrl     = DefinedContribution2016PreAmountPage.navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(Period._2016).saveNavigation(updatedAnswers, redirectUrl)
      _              <- sessionRepository.set(answersWithNav)
    } yield Redirect(redirectUrl)

  private def updateAnswersForNormalDate(mode: Mode, request: DataRequest[AnyContent], value: BigInt) =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(DefinedContribution2016PreAmountPage, value))
      redirectUrl     = DefinedContribution2016PreAmountPage.navigate(mode, updatedAnswers).url
      answersWithNav  = AASection(Period._2016).saveNavigation(updatedAnswers, redirectUrl)
      _              <- sessionRepository.set(answersWithNav)
    } yield Redirect(redirectUrl)

  private def getStartEndDate(flexibleStartDate: Option[LocalDate])(implicit messages: Messages): String = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(Period.pre2016Start) && date.isBefore(Period.pre2016End) =>
          Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + date.format(formatter)
        case _                                                                                   =>
          Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + Period.pre2016End.format(
            formatter
          )
      }

    if (flexibleStartDate == Some(Period.pre2016Start)) {
      Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + flexibleStartDate.get.format(
        formatter
      )
    } else {
      normalDateFormatter
    }
  }
}
