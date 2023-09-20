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

package services

import connectors.{BackendConnector, CalculationResultConnector}
import models.CalculationResults._
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.submission.{SubmissionRequest, SubmissionResponse}
import models.{AnnualAllowance, CalculationAuditEvent, CalculationResults, ChangeInTaxCharge, ExcessLifetimeAllowancePaid, Income, LifeTimeAllowance, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, PensionSchemeDetails, PensionSchemeInputAmounts, Period, ProtectionEnhancedChanged, ProtectionType, SchemeIndex, SchemeNameAndTaxRef, TaxYear, TaxYear2013To2015, TaxYear2016To2023, TaxYearScheme, UserAnswers, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, PIAPreRemedyPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.{ReasonForResubmissionPage, ResubmittingAdjustmentPage}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationResultService @Inject() (
  calculationResultConnector: CalculationResultConnector,
  backendConnector: BackendConnector,
  auditService: AuditService
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[CalculationResponse] =
    for {
      calculationInputs   <- Future.successful(buildCalculationInputs(userAnswers))
      calculationResponse <- calculationResultConnector.sendRequest(calculationInputs)
      _                   <- auditService.auditCalculationRequest(
                               CalculationAuditEvent(calculationInputs, calculationResponse)
                             )
    } yield calculationResponse

  def submitUserAnswersAndCalculation(answers: UserAnswers): Future[SubmissionResponse] = {
    val calculationInputs: CalculationInputs = buildCalculationInputs(answers)
    for {
      calculationResponse <- calculationResultConnector.sendRequest(calculationInputs)
      submissionResponse  <-
        backendConnector.sendSubmissionRequest(SubmissionRequest(calculationInputs, Some(calculationResponse)))
    } yield submissionResponse
  }

  def submitUserAnswersWithNoCalculation(answers: UserAnswers): Future[SubmissionResponse] = {
    val calculationInputs: CalculationInputs = buildCalculationInputs(answers)
    for {
      submissionResponse <-
        backendConnector.sendSubmissionRequest(SubmissionRequest(calculationInputs, None))
    } yield submissionResponse
  }

  def buildCalculationInputs(userAnswers: UserAnswers): CalculationInputs = {

    val resubmission: Resubmission = userAnswers
      .get(ResubmittingAdjustmentPage)
      .map {
        case true  => Resubmission(true, userAnswers.get(ReasonForResubmissionPage))
        case false => Resubmission(false, None)
      }
      .getOrElse(Resubmission(false, None))

    val scottishTaxYears: List[Period] = userAnswers.data.fields
      .find(_._1 == WhichYearsScottishTaxpayerPage.toString)
      .fold {
        List.empty[Period]
      } {
        _._2.as[List[String]] flatMap { sYear =>
          Period.fromString(sYear)
        }
      }

    val _2013To2015TaxYears: List[TaxYear2013To2015] =
      List(Period._2013, Period._2014, Period._2015).flatMap(toTaxYear2013To2015(userAnswers, _))

    val _2016To2023TaxYears: List[TaxYear2016To2023] =
      List(
        Period._2016PreAlignment,
        Period._2016PostAlignment,
        Period._2017,
        Period._2018,
        Period._2019,
        Period._2020,
        Period._2021,
        Period._2022,
        Period._2023
      ).flatMap(
        toTaxYear2016To2023(userAnswers, _)
      )

    val tYears: List[TaxYear] = _2013To2015TaxYears ++ _2016To2023TaxYears

    CalculationResults.CalculationInputs(
      resubmission,
      Some(AnnualAllowance(scottishTaxYears, tYears)),
      buildLifeTimeAllowance(userAnswers)
    )
  }

  def toTaxYear2013To2015(userAnswers: UserAnswers, period: Period): Option[TaxYear2013To2015] =
    userAnswers.get(PIAPreRemedyPage(period)).map(v => TaxYear2013To2015(v.toInt, period))

  def toTaxYear2016To2023(
    userAnswers: UserAnswers,
    period: Period
  ): Option[TaxYear2016To2023] = {
    val totalIncome: Int =
      if (period == Period._2016PreAlignment)
        userAnswers.get(TotalIncomePage(Period._2016PostAlignment)).map(_.toInt).getOrElse(0)
      else
        userAnswers.get(TotalIncomePage(period)).map(_.toInt).getOrElse(0)

    val income: Option[Income] =
      if (period == Period._2016PreAlignment | period == Period._2016PostAlignment)
        None
      else {
        if (userAnswers.get(ThresholdIncomePage(period)).getOrElse(false)) {
          Some(userAnswers.get(AdjustedIncomePage(period)).map(v => AboveThreshold(v.toInt)).getOrElse(BelowThreshold))
        } else {
          Some(BelowThreshold)
        }

      }

    val chargePaidByMember: Int =
      userAnswers.get(HowMuchAAChargeYouPaidPage(period, SchemeIndex(0))).map(_.toInt).getOrElse(0)

    val taxYearSchemes: List[TaxYearScheme] =
      Range
        .inclusive(0, 4)
        .flatMap { v =>
          val oPensionSchemeDetails: Option[PensionSchemeDetails] =
            userAnswers.get(PensionSchemeDetailsPage(period, SchemeIndex(v)))

          val oPensionSchemeInputAmounts: Option[PensionSchemeInputAmounts] =
            userAnswers.get(PensionSchemeInputAmountsPage(period, SchemeIndex(v)))

          val oChargePaidByScheme: Option[Int] =
            userAnswers.get(HowMuchAAChargeSchemePaidPage(period, SchemeIndex(v))).map(_.toInt)

          (oPensionSchemeDetails, oPensionSchemeInputAmounts) match {
            case (Some(pensionSchemeDetails), Some(pensionSchemeInputAmounts)) =>
              Some(
                TaxYearScheme(
                  pensionSchemeDetails.schemeName,
                  pensionSchemeDetails.schemeTaxRef,
                  pensionSchemeInputAmounts.originalPIA.toInt,
                  pensionSchemeInputAmounts.revisedPIA.toInt,
                  oChargePaidByScheme.getOrElse(0)
                )
              )

            case _ => None
          }
        }
        .toList

    if (taxYearSchemes.nonEmpty) {
      (
        userAnswers.get(OtherDefinedBenefitOrContributionPage(period)),
        userAnswers.get(FlexiAccessDefinedContributionAmountPage(period))
      ) match {
        case (Some(true), Some(postAccessDefinedContributionInputAmount)) =>
          val definedBenefitInputAmount      =
            userAnswers.get(DefinedBenefitAmountPage(period)).map(_.toInt).getOrElse(0)
          val definedContributionInputAmount =
            userAnswers.get(DefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)

          val flexiAccessDate: LocalDate =
            userAnswers.get(FlexibleAccessStartDatePage).getOrElse(LocalDate.parse("2010-01-01"))

          Some(
            InitialFlexiblyAccessedTaxYear(
              definedBenefitInputAmount,
              flexiAccessDate,
              definedContributionInputAmount,
              postAccessDefinedContributionInputAmount.toInt,
              taxYearSchemes,
              totalIncome,
              chargePaidByMember,
              period,
              income
            )
          )

        case (Some(true), None) =>
          val definedBenefitInputAmount      =
            userAnswers.get(DefinedBenefitAmountPage(period)).map(_.toInt).getOrElse(0)
          val definedContributionInputAmount =
            userAnswers.get(DefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)
          Some(
            PostFlexiblyAccessedTaxYear(
              definedBenefitInputAmount,
              definedContributionInputAmount,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              period,
              income
            )
          )

        case (Some(false), _) =>
          Some(
            NormalTaxYear(
              taxYearSchemes.map(_.revisedPensionInputAmount).sum,
              taxYearSchemes,
              totalIncome,
              chargePaidByMember,
              period,
              income
            )
          )

        case _ =>
          Some(
            NormalTaxYear(
              taxYearSchemes.map(_.revisedPensionInputAmount).sum,
              taxYearSchemes,
              totalIncome,
              chargePaidByMember,
              period,
              income
            )
          )
      }
    } else
      None
  }

  def buildLifeTimeAllowance(userAnswers: UserAnswers): Option[LifeTimeAllowance] = {

    val benefitCrystallisationEventFlag: Option[Boolean] = userAnswers.get(HadBenefitCrystallisationEventPage)

    val changeInLifetimeAllowancePercentageInformedFlag: Option[Boolean] =
      userAnswers.get(ChangeInLifetimeAllowancePage)

    val changeInTaxCharge: Option[ChangeInTaxCharge] = userAnswers.get(ChangeInTaxChargePage)

    (benefitCrystallisationEventFlag, changeInLifetimeAllowancePercentageInformedFlag, changeInTaxCharge) match {
      case (Some(true), Some(true), Some(changeInTaxChargeType)) if changeInTaxChargeType != ChangeInTaxCharge.None =>
        val benefitCrystallisationEventDate: LocalDate =
          userAnswers.get(DateOfBenefitCrystallisationEventPage).getOrElse(LocalDate.now)

        val lifetimeAllowanceProtectionOrEnhancements: LtaProtectionOrEnhancements =
          userAnswers.get(LtaProtectionOrEnhancementsPage).getOrElse(LtaProtectionOrEnhancements.Protection)

        val protectionType: ProtectionType =
          userAnswers.get(ProtectionTypePage).getOrElse(ProtectionType.EnhancedProtection)

        val protectionReference: String = userAnswers.get(ProtectionReferencePage).getOrElse("")

        val protectionTypeOrEnhancementChangedFlag: ProtectionEnhancedChanged =
          userAnswers.get(ProtectionEnhancedChangedPage).getOrElse(ProtectionEnhancedChanged.Protection)

        val newProtectionTypeOrEnhancement: Option[WhatNewProtectionTypeEnhancement] =
          userAnswers.get(WhatNewProtectionTypeEnhancementPage)

        val newProtectionTypeOrEnhancementReference: Option[String] =
          userAnswers.get(ReferenceNewProtectionTypeEnhancementPage)

        val previousLifetimeAllowanceChargeFlag: Boolean = userAnswers.get(LifetimeAllowanceChargePage).getOrElse(false)

        val previousLifetimeAllowanceChargePaymentMethod: Option[ExcessLifetimeAllowancePaid] =
          userAnswers.get(ExcessLifetimeAllowancePaidPage)

        val previousLifetimeAllowanceChargeAmount: Option[Int] =
          userAnswers.get(LifetimeAllowanceChargeAmountPage).map(_.toInt)

        val previousLifetimeAllowanceChargePaidBy: Option[WhoPaidLTACharge] = userAnswers.get(WhoPaidLTAChargePage)

        val previousLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[SchemeNameAndTaxRef] =
          userAnswers.get(SchemeNameAndTaxRefPage)

        val newLifetimeAllowanceChargeAmount: Int = userAnswers.get(ValueNewLtaChargePage).getOrElse(BigInt(0)).toInt

        val newLifetimeAllowanceChargeWillBePaidBy: Option[WhoPayingExtraLtaCharge] =
          userAnswers.get(WhoPayingExtraLtaChargePage)

        val newLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[LtaPensionSchemeDetails] =
          userAnswers.get(LtaPensionSchemeDetailsPage)

        Some(
          LifeTimeAllowance(
            true,
            benefitCrystallisationEventDate,
            true,
            changeInTaxChargeType,
            lifetimeAllowanceProtectionOrEnhancements,
            protectionType,
            protectionReference,
            protectionTypeOrEnhancementChangedFlag,
            newProtectionTypeOrEnhancement,
            newProtectionTypeOrEnhancementReference,
            previousLifetimeAllowanceChargeFlag,
            previousLifetimeAllowanceChargePaymentMethod,
            previousLifetimeAllowanceChargeAmount,
            previousLifetimeAllowanceChargePaidBy,
            previousLifetimeAllowanceChargeSchemeNameAndTaxRef,
            newLifetimeAllowanceChargeAmount,
            newLifetimeAllowanceChargeWillBePaidBy,
            newLifetimeAllowanceChargeSchemeNameAndTaxRef
          )
        )

      case _ =>
        None
    }

  }

  def calculationResultsViewModel(calculateResponse: CalculationResponse): CalculationResultsViewModel = {
    val resubmissionVal: Seq[RowViewModel]  = resubmission(calculateResponse)
    val totalAmountVal: Seq[RowViewModel]   = totalAmount(calculateResponse)
    val outDatesVal: Seq[Seq[RowViewModel]] = outDates(calculateResponse)
    val inDatesVal: Seq[Seq[RowViewModel]]  = inDates(calculateResponse)
    CalculationResultsViewModel(totalAmountVal, resubmissionVal, outDatesVal, inDatesVal)
  }

  private def totalAmount(calculateResponse: CalculationResponse): Seq[RowViewModel] =
    Seq(
      RowViewModel(
        "calculationResults.outDatesCompensation",
        calculateResponse.totalAmounts.outDatesCompensation.toString()
      )
    ) ++
      Seq(RowViewModel("calculationResults.inDatesDebit", calculateResponse.totalAmounts.inDatesDebit.toString())) ++
      Seq(RowViewModel("calculationResults.inDatesCredit", calculateResponse.totalAmounts.inDatesCredit.toString()))

  private def resubmission(calculateResponse: CalculationResponse): Seq[RowViewModel] =
    if (calculateResponse.resubmission.isResubmission) {
      Seq(RowViewModel("calculationResults.annualResults.isResubmission", "")) ++
        Seq(
          RowViewModel("calculationResults.annualResults.reason", calculateResponse.resubmission.reason.getOrElse(""))
        )
    } else {
      Seq(RowViewModel("calculationResults.annualResults.notResubmission", ""))
    }

  private def outDates(calculateResponse: CalculationResponse): Seq[Seq[RowViewModel]] =
    calculateResponse.outDates.map { outDate =>
      Seq(
        RowViewModel("periodDateRangeAA." + outDate.period.toString, outDate.period.toString),
        RowViewModel("calculationResults.annualResults.chargePaidBySchemes", outDate.chargePaidBySchemes.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidByMember", outDate.chargePaidByMember.toString()),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate",
          outDate.revisedChargableAmountAfterTaxRate.toString()
        ),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate",
          outDate.revisedChargableAmountBeforeTaxRate.toString()
        ),
        RowViewModel("calculationResults.annualResults.directCompensation", outDate.directCompensation.toString()),
        RowViewModel("calculationResults.annualResults.indirectCompensation", outDate.indirectCompensation.toString()),
        RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", outDate.unusedAnnualAllowance.toString())
      )
    }

  private def inDates(calculateResponse: CalculationResponse): Seq[Seq[RowViewModel]] =
    calculateResponse.inDates.map { inDate =>
      Seq(
        RowViewModel("periodDateRangeAA." + inDate.period.toString(), inDate.period.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidBySchemes", inDate.chargePaidBySchemes.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidByMember", inDate.chargePaidByMember.toString()),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate",
          inDate.revisedChargableAmountAfterTaxRate.toString()
        ),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate",
          inDate.revisedChargableAmountBeforeTaxRate.toString()
        ),
        RowViewModel("calculationResults.annualResults.memberCredit", inDate.memberCredit.toString()),
        RowViewModel("calculationResults.annualResults.schemeCredit", inDate.schemeCredit.toString()),
        RowViewModel("calculationResults.annualResults.debit", inDate.debit.toString()),
        RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", inDate.unusedAnnualAllowance.toString())
      )
    }

}
