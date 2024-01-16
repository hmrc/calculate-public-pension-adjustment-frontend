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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{Period, SchemeIndex}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.annualallowance.taxyear._
import viewmodels.govuk.summarylist._
import views.html.annualallowance.taxyear.CheckYourAAPeriodAnswersView

import java.time.LocalDate

class CheckYourAAPeriodAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAAPeriodAnswersView
) extends FrontendBaseController
    with I18nSupport {

  // noinspection ScalaStyle
  def onPageLoad(period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val rowsOne: Seq[Option[SummaryListRow]] = Seq(
        MemberMoreThanOnePensionSummary.row(request.userAnswers, period)
      )

      val schemeIndices = 0.to(4).map(i => SchemeIndex(i))

      val rowsTwo: Seq[Option[SummaryListRow]] = schemeIndices.flatMap(index =>
        Seq(
          PensionSchemeDetailsSummary.row(request.userAnswers, period, index),
          PensionSchemeInputAmountsSummary.row(request.userAnswers, period, index),
          PensionSchemeInput2016preAmountsSummary.row(request.userAnswers, period, index),
          PensionSchemeInput2016postAmountsSummary.row(request.userAnswers, period, index),
          PayAChargeSummary.row(request.userAnswers, period, index),
          WhoPaidAAChargeSummary.row(request.userAnswers, period, index),
          HowMuchAAChargeYouPaidSummary.row(request.userAnswers, period, index),
          HowMuchAAChargeSchemePaidSummary.row(request.userAnswers, period, index)
        )
      )

      val flexibleStartDate = request.userAnswers.get(FlexibleAccessStartDatePage)

      val flexiAccessExistsForPeriod = request.userAnswers.get(FlexibleAccessStartDatePage) match {
        case Some(date) => period.start.minusDays(1).isBefore(date) && period.end.plusDays(1).isAfter(date)
        case None       => false
      }

      val flexiPeriodEndateRows: Seq[Option[SummaryListRow]] =
        Seq(
          OtherDefinedBenefitOrContributionSummary.row(request.userAnswers, period),
          ContributedToDuringRemedyPeriodSummary.row(request.userAnswers, period),
          DefinedContributionAmountSummary.row(request.userAnswers, period),
          DefinedBenefitAmountSummary.row(request.userAnswers, period),
          ThresholdIncomeSummary.row(request.userAnswers, period),
          AdjustedIncomeSummary.row(request.userAnswers, period),
          TotalIncomeSummary.row(request.userAnswers, period)
        )

      val regularRows: Seq[Option[SummaryListRow]] =
        Seq(
          OtherDefinedBenefitOrContributionSummary.row(request.userAnswers, period),
          ContributedToDuringRemedyPeriodSummary.row(request.userAnswers, period),
          DefinedContributionAmountSummary.row(request.userAnswers, period),
          FlexiAccessDefinedContributionAmountSummary.row(request.userAnswers, period),
          DefinedBenefitAmountSummary.row(request.userAnswers, period),
          ThresholdIncomeSummary.row(request.userAnswers, period),
          AdjustedIncomeSummary.row(request.userAnswers, period),
          TotalIncomeSummary.row(request.userAnswers, period)
        )

      val flexiPeriodEndateRows2016: Seq[Option[SummaryListRow]] =
        Seq(
          OtherDefinedBenefitOrContributionSummary.row(request.userAnswers, period),
          ContributedToDuringRemedyPeriodSummary.row(request.userAnswers, period),
          DefinedContribution2016PreAmountSummary.row(request.userAnswers),
          DefinedContribution2016PostAmountSummary.row(request.userAnswers),
          DefinedBenefit2016PreAmountSummary.row(request.userAnswers),
          DefinedBenefit2016PostAmountSummary.row(request.userAnswers),
          TotalIncomeSummary.row(request.userAnswers, period)
        )

      val regularRows2016: Seq[Option[SummaryListRow]] =
        Seq(
          OtherDefinedBenefitOrContributionSummary.row(request.userAnswers, period),
          ContributedToDuringRemedyPeriodSummary.row(request.userAnswers, period),
          DefinedContribution2016PreAmountSummary.row(request.userAnswers),
          DefinedContribution2016PreFlexiAmountSummary.row(request.userAnswers),
          DefinedContribution2016PostAmountSummary.row(request.userAnswers),
          DefinedContribution2016PostFlexiAmountSummary.row(request.userAnswers),
          DefinedBenefit2016PreAmountSummary.row(request.userAnswers),
          DefinedBenefit2016PostAmountSummary.row(request.userAnswers),
          TotalIncomeSummary.row(request.userAnswers, period)
        )

      def is2016Period: Boolean =
        if (period == Period._2016)
          true
        else false

      def maybeFlexiPeriodEndDateRowsStatus: Boolean =
        if (flexiAccessExistsForPeriod) {
          if (period == Period._2016)
            flexibleStartDate match {
              case Some(date) if date == Period.pre2016End || date == Period.post2016End =>
                true
              case _                                                                     =>
                false
            }
          else
            flexibleStartDate match {
              case Some(date) if date == period.end =>
                true
              case _                                =>
                false
            }
        } else false

      val combinedRows: Seq[Option[SummaryListRow]] = Seq(rowsOne ++ rowsTwo).flatten

      Ok(
        view(
          maybeFlexiPeriodEndDateRowsStatus,
          is2016Period,
          s"checkYourAnswers.aa.period.subHeading.$period",
          controllers.routes.TaskListController.onPageLoad(),
          SummaryListViewModel(combinedRows.flatten),
          SummaryListViewModel(regularRows.flatten),
          SummaryListViewModel(flexiPeriodEndateRows.flatten),
          SummaryListViewModel(regularRows2016.flatten),
          SummaryListViewModel(flexiPeriodEndateRows2016.flatten)
        )
      )
  }

}
