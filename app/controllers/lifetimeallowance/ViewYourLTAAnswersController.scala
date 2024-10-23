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
import viewmodels.checkAnswers.lifetimeallowance._
import viewmodels.govuk.summarylist._
import views.html.lifetimeallowance.ViewYourAnswersView

class ViewYourLTAAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ViewYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val rows: Seq[Option[SummaryListRow]] = Seq(
      DateOfBenefitCrystallisationEventSummary.row(request.userAnswers, changeAllowed = false),
      LtaProtectionOrEnhancementsSummary.row(request.userAnswers, changeAllowed = false),
      ProtectionTypeSummary.row(request.userAnswers, changeAllowed = false),
      ProtectionReferenceSummary.row(request.userAnswers, changeAllowed = false),
      EnhancementTypeSummary.row(request.userAnswers, changeAllowed = false),
      InternationalEnhancementReferenceSummary.row(request.userAnswers, changeAllowed = false),
      PensionCreditReferenceSummary.row(request.userAnswers, changeAllowed = false),
      ProtectionEnhancedChangedSummary.row(request.userAnswers, changeAllowed = false),
      WhatNewProtectionTypeEnhancementSummary.row(request.userAnswers, changeAllowed = false),
      ReferenceNewProtectionTypeEnhancementSummary.row(request.userAnswers, changeAllowed = false),
      NewEnhancementTypeSummary.row(request.userAnswers, changeAllowed = false),
      NewInternationalEnhancementReferenceSummary.row(request.userAnswers, changeAllowed = false),
      NewPensionCreditReferenceSummary.row(request.userAnswers, changeAllowed = false),
      LifetimeAllowanceChargeSummary.row(request.userAnswers, changeAllowed = false),
      ExcessLifetimeAllowancePaidSummary.row(request.userAnswers, changeAllowed = false),
      LumpSumValueSummary.row(request.userAnswers, changeAllowed = false),
      AnnualPaymentValueSummary.row(request.userAnswers, changeAllowed = false),
      WhoPaidLTAChargeSummary.row(request.userAnswers, changeAllowed = false),
      SchemeNameAndTaxRefSummary.row(request.userAnswers, changeAllowed = false),
      UserSchemeDetailsSummary.row(request.userAnswers, changeAllowed = false),
      QuarterChargePaidSummary.row(request.userAnswers, changeAllowed = false),
      YearChargePaidSummary.row(request.userAnswers, changeAllowed = false),
      NewExcessLifetimeAllowancePaidSummary.row(request.userAnswers, false),
      NewLumpSumValueSummary.row(request.userAnswers, changeAllowed = false),
      NewAnnualPaymentValueSummary.row(request.userAnswers, changeAllowed = false),
      WhoPayingExtraLtaChargeSummary.row(request.userAnswers, changeAllowed = false),
      LtaPensionSchemeDetailsSummary.row(request.userAnswers, changeAllowed = false)
    )

    Ok(
      view(
        controllers.routes.CalculationReviewController.onPageLoad(),
        SummaryListViewModel(rows.flatten)
      )
    )
  }
}
