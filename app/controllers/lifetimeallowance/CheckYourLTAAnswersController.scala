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

package controllers.lifetimeallowance

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.lifetimeallowance.*
import viewmodels.govuk.summarylist.*
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
      DateOfBenefitCrystallisationEventSummary.row(request.userAnswers, changeAllowed = true),
      LtaProtectionOrEnhancementsSummary.row(request.userAnswers, changeAllowed = true),
      ProtectionTypeSummary.row(request.userAnswers, changeAllowed = true),
      ProtectionReferenceSummary.row(request.userAnswers, changeAllowed = true),
      EnhancementTypeSummary.row(request.userAnswers, changeAllowed = true),
      InternationalEnhancementReferenceSummary.row(request.userAnswers, changeAllowed = true),
      PensionCreditReferenceSummary.row(request.userAnswers, changeAllowed = true),
      ProtectionEnhancedChangedSummary.row(request.userAnswers, changeAllowed = true),
      WhatNewProtectionTypeEnhancementSummary.row(request.userAnswers, changeAllowed = true),
      ReferenceNewProtectionTypeEnhancementSummary.row(request.userAnswers, changeAllowed = true),
      NewEnhancementTypeSummary.row(request.userAnswers, changeAllowed = true),
      NewInternationalEnhancementReferenceSummary.row(request.userAnswers, changeAllowed = true),
      NewPensionCreditReferenceSummary.row(request.userAnswers, changeAllowed = true),
      LifetimeAllowanceChargeSummary.row(request.userAnswers, changeAllowed = true),
      ExcessLifetimeAllowancePaidSummary.row(request.userAnswers, changeAllowed = true),
      LumpSumValueSummary.row(request.userAnswers, changeAllowed = true),
      AnnualPaymentValueSummary.row(request.userAnswers, changeAllowed = true),
      WhoPaidLTAChargeSummary.row(request.userAnswers, changeAllowed = true),
      SchemeNameAndTaxRefSummary.row(request.userAnswers, changeAllowed = true),
      UserSchemeDetailsSummary.row(request.userAnswers, changeAllowed = true),
      QuarterChargePaidSummary.row(request.userAnswers, changeAllowed = true),
      YearChargePaidSummary.row(request.userAnswers, changeAllowed = true),
      NewExcessLifetimeAllowancePaidSummary.row(request.userAnswers, changeAllowed = true),
      NewLumpSumValueSummary.row(request.userAnswers, changeAllowed = true),
      NewAnnualPaymentValueSummary.row(request.userAnswers, changeAllowed = true),
      WhoPayingExtraLtaChargeSummary.row(request.userAnswers, changeAllowed = true),
      LtaPensionSchemeDetailsSummary.row(request.userAnswers, changeAllowed = true)
    )

    Ok(
      view(
        controllers.routes.TaskListController.onPageLoad(),
        SummaryListViewModel(rows.flatten)
      )
    )
  }
}
