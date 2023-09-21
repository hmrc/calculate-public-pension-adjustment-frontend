/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.lifetimeallowance

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.lifetimeallowance._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

class CheckYourLTAAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val rows: Seq[Option[SummaryListRow]] = Seq(
      HadBenefitCrystallisationEventSummary.row(request.userAnswers),
      DateOfBenefitCrystallisationEventSummary.row(request.userAnswers),
      ChangeInLifetimeAllowanceSummary.row(request.userAnswers),
      ChangeInTaxChargeSummary.row(request.userAnswers),
      MultipleBenefitCrystallisationEventSummary.row(request.userAnswers),
      LtaProtectionOrEnhancementsSummary.row(request.userAnswers),
      ProtectionReferenceSummary.row(request.userAnswers),
      EnhancementTypeSummary.row(request.userAnswers),
      InternationalEnhancementReferenceSummary.row(request.userAnswers),
      PensionCreditReferenceSummary.row(request.userAnswers),
      ProtectionTypeSummary.row(request.userAnswers),
      ProtectionTypeEnhancementChangedSummary.row(request.userAnswers),
      WhatNewProtectionTypeEnhancementSummary.row(request.userAnswers),
      ReferenceNewProtectionTypeEnhancementSummary.row(request.userAnswers),
      LifetimeAllowanceChargeSummary.row(request.userAnswers),
      ExcessLifetimeAllowancePaidSummary.row(request.userAnswers),
      LumpSumValueSummary.row(request.userAnswers),
      AnnualPaymentValueSummary.row(request.userAnswers),
      LifetimeAllowanceChargeAmountSummary.row(request.userAnswers),
      WhoPaidLTAChargeSummary.row(request.userAnswers),
      SchemeNameAndTaxRefSummary.row(request.userAnswers),
      ValueNewLtaChargeSummary.row(request.userAnswers),
      QuarterChargePaidSummary.row(request.userAnswers),
      YearChargePaidSummary.row(request.userAnswers),
      NewExcessLifetimeAllowancePaidSummary.row(request.userAnswers),
      NewLumpSumValueSummary.row(request.userAnswers),
      NewAnnualPaymentValueSummary.row(request.userAnswers),
      WhoPayingExtraLtaChargeSummary.row(request.userAnswers),
      LtaPensionSchemeDetailsSummary.row(request.userAnswers)
    )

    Ok(
      view(
        "checkYourAnswers.lta.subHeading",
        controllers.routes.TaskListController.onPageLoad(),
        SummaryListViewModel(rows.flatten)
      )
    )
  }
}
