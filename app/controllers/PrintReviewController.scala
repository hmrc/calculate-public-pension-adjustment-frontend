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

package controllers

import controllers.actions._
import models.CalculationResults.IndividualAASummaryModel
import models.tasklist.SectionStatus
import models.tasklist.sections.LTASection
import play.api.data.Form
import play.api.data.Forms.ignored

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.PrintReviewView
import services.CalculationResultService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.lifetimeallowance.{AnnualPaymentValueSummary, DateOfBenefitCrystallisationEventSummary, EnhancementTypeSummary, ExcessLifetimeAllowancePaidSummary, InternationalEnhancementReferenceSummary, LifetimeAllowanceChargeSummary, LtaPensionSchemeDetailsSummary, LtaProtectionOrEnhancementsSummary, LumpSumValueSummary, NewAnnualPaymentValueSummary, NewEnhancementTypeSummary, NewExcessLifetimeAllowancePaidSummary, NewInternationalEnhancementReferenceSummary, NewLumpSumValueSummary, NewPensionCreditReferenceSummary, PensionCreditReferenceSummary, ProtectionEnhancedChangedSummary, ProtectionReferenceSummary, ProtectionTypeSummary, QuarterChargePaidSummary, ReferenceNewProtectionTypeEnhancementSummary, SchemeNameAndTaxRefSummary, UserSchemeDetailsSummary, WhatNewProtectionTypeEnhancementSummary, WhoPaidLTAChargeSummary, WhoPayingExtraLtaChargeSummary, YearChargePaidSummary}
import viewmodels.govuk.all.SummaryListViewModel

import scala.concurrent.ExecutionContext

class PrintReviewController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  requireTasksCompleted: RequireTasksCompletedAction,
  view: PrintReviewView,
  calculationResultService: CalculationResultService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad: Action[AnyContent] =
    (identify andThen getData andThen requireData andThen requireTasksCompleted).async { implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

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

      val isLTACompleteWithoutKickout = LTASection.status(request.userAnswers) == SectionStatus.Completed && !LTASection
        .kickoutHasBeenReached(request.userAnswers)

      calculationResultService.sendRequest(request.userAnswers).flatMap { calculationResponse =>
        val outDatesStringValues = calculationResultService.outDatesSummary(calculationResponse)
        val inDatesStringValues  = calculationResultService.inDatesSummary(calculationResponse)
        val hasinDates: Boolean  = calculationResponse.inDates.isDefinedAt(0)

        calculationResultService
          .calculationReviewIndividualAAViewModel(calculationResponse, None, request.userAnswers)
          .map { calculationReviewIndividualAAViewModel =>
            val isInCredit: Boolean              = calculationResponse.totalAmounts.inDatesCredit > 0
            val isInDebit: Boolean               = calculationResponse.totalAmounts.inDatesDebit > 0
            val includeCompensation2015: Boolean = calculationResponse.totalAmounts.outDatesCompensation > 0
            Ok(
              view(
                form,
                calculationReviewIndividualAAViewModel,
                isInCredit,
                isInDebit,
                outDatesStringValues,
                inDatesStringValues,
                calculationResultService.calculationReviewViewModel(calculationResponse),
                SummaryListViewModel(rows.flatten),
                isLTACompleteWithoutKickout,
                includeCompensation2015,
                controllers.routes.CalculationReviewController.onPageLoad(),
                hasinDates
              )
            )
          }
      }
    }

}
