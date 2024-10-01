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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{AAKickOutStatus, NormalMode, ReportingChange}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.setupquestions.ReportingChangePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.setupquestions.annualallowance._
import viewmodels.checkAnswers.setupquestions.{ReasonForResubmissionSummary, ReportingChangeSummary, ResubmittingAdjustmentSummary}
import viewmodels.checkAnswers.{AffectedByRemedySummary, Contribution4000ToDirectContributionSchemeSummary, FlexibleAccessDcSchemeSummary}
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView
import viewmodels.checkAnswers.setupquestions.annualallowance.{ContributionRefundsSummary, HadAAChargeSummary, NetIncomeAbove100KSummary, NetIncomeAbove190KIn2023Summary, PIAAboveAnnualAllowanceIn2023Summary, PensionProtectedMemberSummary, SavingsStatementSummary}
import viewmodels.checkAnswers.setupquestions.lifetimeallowance._

class CheckYourSetupAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val rows: Seq[Option[SummaryListRow]] = Seq(
      ResubmittingAdjustmentSummary.row(request.userAnswers),
      ReasonForResubmissionSummary.row(request.userAnswers),
      AffectedByRemedySummary.row(request.userAnswers),
      ReportingChangeSummary.row(request.userAnswers)
    )

    val aaRows: Seq[Option[SummaryListRow]] = Seq(
      SavingsStatementSummary.row(request.userAnswers),
      PensionProtectedMemberSummary.row(request.userAnswers),
      HadAAChargeSummary.row(request.userAnswers),
      ContributionRefundsSummary.row(request.userAnswers),
      NetIncomeAbove100KSummary.row(request.userAnswers),
      NetIncomeAbove190KSummary.row(request.userAnswers),
      MaybePIAIncreaseSummary.row(request.userAnswers),
      MaybePIAUnchangedOrDecreasedSummary.row(request.userAnswers),
      PIAAboveAnnualAllowanceIn2023Summary.row(request.userAnswers),
      NetIncomeAbove190KIn2023Summary.row(request.userAnswers),
      FlexibleAccessDcSchemeSummary.row(request.userAnswers),
      Contribution4000ToDirectContributionSchemeSummary.row(request.userAnswers)
    )

    val ltaRows: Seq[Option[SummaryListRow]] = Seq(
      HadBenefitCrystallisationEventSummary.row(request.userAnswers),
      PreviousLTAChargeSummary.row(request.userAnswers),
      ChangeInLifetimeAllowanceSummary.row(request.userAnswers),
      IncreaseInLTAChargeSummary.row(request.userAnswers),
      NewLTAChargeSummary.row(request.userAnswers),
      MultipleBenefitCrystallisationEventSummary.row(request.userAnswers),
      OtherSchemeNotificationSummary.row(request.userAnswers)
    )

    val finalRows: Seq[Option[SummaryListRow]] = rows ++ aaRows ++ ltaRows

    val continueURL = controllers.setupquestions.routes.UserEligibility.onPageLoad

    Ok(view("checkYourAnswers.setup.subHeading", continueURL, SummaryListViewModel(finalRows.flatten)))
  }
}
