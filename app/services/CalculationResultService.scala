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

package services

import connectors.{CalculationResultConnector, SubmissionsConnector}
import models.CalculationResults._
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.submission.{SubmissionRequest, SubmissionResponse}
import models.tasklist.sections.LTASection
import models.{AAKickOutStatus, AnnualAllowance, CalculationAuditEvent, CalculationResults, EnhancementType, ExcessLifetimeAllowancePaid, Income, IncomeSubJourney, LTAKickOutStatus, LifeTimeAllowance, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, NewEnhancementType, NewExcessLifetimeAllowancePaid, NewLifeTimeAllowanceAdditions, PensionSchemeDetails, PensionSchemeInput2016postAmounts, PensionSchemeInputAmounts, Period, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, ReportingChange, SchemeIndex, SchemeNameAndTaxRef, TaxYear, TaxYear2011To2015, TaxYear2016To2023, TaxYearScheme, ThresholdIncome, UserAnswers, UserSchemeDetails, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, PIAPreRemedyPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.annualallowance.{Contribution4000ToDirectContributionSchemePage, ContributionRefundsPage, FlexibleAccessDcSchemePage, HadAAChargePage, MaybePIAIncreasePage, MaybePIAUnchangedOrDecreasedPage, NetIncomeAbove100KPage, NetIncomeAbove190KIn2023Page, NetIncomeAbove190KPage, PIAAboveAnnualAllowanceIn2023Page, PensionProtectedMemberPage, SavingsStatementPage}
import pages.setupquestions.lifetimeallowance._
import pages.setupquestions.{ReasonForResubmissionPage, ReportingChangePage, ResubmittingAdjustmentPage}
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationResultService @Inject() (
  calculationResultConnector: CalculationResultConnector,
  submissionsConnector: SubmissionsConnector,
  auditService: AuditService
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[CalculationResponse] =
    for {
      calculationInputs   <- Future.successful(buildCalculationInputs(userAnswers))
      calculationResponse <- calculationResultConnector.sendRequest(calculationInputs)
      _                   <-
        auditService.auditCalculationRequest(
          CalculationAuditEvent(
            userAnswers.uniqueId,
            userAnswers.authenticated,
            userAnswers.id,
            calculationInputs,
            calculationResponse
          )
        )
    } yield calculationResponse

  def submitUserAnswersAndCalculation(answers: UserAnswers, userId: String)(implicit
    hc: HeaderCarrier
  ): Future[SubmissionResponse] = {
    val calculationInputs: CalculationInputs = buildCalculationInputs(answers)
    for {
      calculationResponse <- calculationResultConnector.sendRequest(calculationInputs)
      submissionResponse  <-
        submissionsConnector.sendSubmissionRequest(
          SubmissionRequest(calculationInputs, Some(calculationResponse), userId, answers.uniqueId)
        )
    } yield submissionResponse
  }

  def submitUserAnswersWithNoCalculation(answers: UserAnswers, userId: String)(implicit
    hc: HeaderCarrier
  ): Future[SubmissionResponse] = {
    val calculationInputs: CalculationInputs = buildCalculationInputs(answers)
    for {
      submissionResponse <-
        submissionsConnector.sendSubmissionRequest(
          SubmissionRequest(calculationInputs, None, userId, answers.uniqueId)
        )
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

    val _2011To2015TaxYears: List[TaxYear2011To2015] =
      List(Period._2011, Period._2012, Period._2013, Period._2014, Period._2015).flatMap(
        toTaxYear2011To2015(userAnswers, _)
      )

    val _2016To2023TaxYears: List[TaxYear2016To2023] =
      List(
        Period._2016,
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

    val tYears: List[TaxYear] = _2011To2015TaxYears ++ _2016To2023TaxYears

    val reportingChange: Option[Set[ReportingChange]] = userAnswers.get(ReportingChangePage)

    CalculationResults.CalculationInputs(
      resubmission,
      buildSetup(userAnswers),
      if (
        reportingChange
          .exists(_.contains(models.ReportingChange.AnnualAllowance)) && userAnswers.get(AAKickOutStatus()).get == 2
      ) {
        Some(AnnualAllowance(scottishTaxYears, tYears))
      } else None,
      if (
        reportingChange
          .exists(_.contains(models.ReportingChange.LifetimeAllowance)) && userAnswers.get(LTAKickOutStatus()).get == 2
      ) {
        buildLifeTimeAllowance(userAnswers)
      } else None
    )
  }

  def buildSetup(userAnswers: UserAnswers): Setup = {

    val annualAllowanceSetup: Option[AnnualAllowanceSetup] =
      if (userAnswers.get(ReportingChangePage).exists(_.contains(ReportingChange.AnnualAllowance))) {
        val savingsStatement: Option[Boolean]                                  = userAnswers.get(SavingsStatementPage).orElse(None)
        val pensionProtectedMember: Option[Boolean]                            = userAnswers.get(PensionProtectedMemberPage).orElse(None)
        val hadAACharge: Option[Boolean]                                       = userAnswers.get(HadAAChargePage).orElse(None)
        val contributionRefunds: Option[Boolean]                               = userAnswers.get(ContributionRefundsPage).orElse(None)
        val netIncomeAbove100K: Option[Boolean]                                = userAnswers.get(NetIncomeAbove100KPage).orElse(None)
        val netIncomeAbove190K: Option[Boolean]                                = userAnswers.get(NetIncomeAbove190KPage).orElse(None)
        val maybePIAIncrease: Option[MaybePIAIncrease]                         = userAnswers.get(MaybePIAIncreasePage).orElse(None)
        val maybePIAUnchangedOrDecreased: Option[MaybePIAUnchangedOrDecreased] =
          userAnswers.get(MaybePIAUnchangedOrDecreasedPage).orElse(None)
        val pIAAboveAnnualAllowanceIn2023: Option[Boolean]                     =
          userAnswers.get(PIAAboveAnnualAllowanceIn2023Page).orElse(None)
        val netIncomeAbove190KIn2023: Option[Boolean]                          = userAnswers.get(NetIncomeAbove190KIn2023Page).orElse(None)
        val flexibleAccessDcScheme: Option[Boolean]                            = userAnswers.get(FlexibleAccessDcSchemePage).orElse(None)
        val contribution4000ToDirectContributionScheme: Option[Boolean]        =
          userAnswers.get(Contribution4000ToDirectContributionSchemePage).orElse(None)

        Some(
          AnnualAllowanceSetup(
            savingsStatement,
            pensionProtectedMember,
            hadAACharge,
            contributionRefunds,
            netIncomeAbove100K,
            netIncomeAbove190K,
            maybePIAIncrease,
            maybePIAUnchangedOrDecreased,
            pIAAboveAnnualAllowanceIn2023,
            netIncomeAbove190KIn2023,
            flexibleAccessDcScheme,
            contribution4000ToDirectContributionScheme
          )
        )
      } else None

    val lifetimeAllowanceSetup: Option[LifetimeAllowanceSetup] =
      if (userAnswers.get(ReportingChangePage).exists(_.contains(ReportingChange.LifetimeAllowance))) {
        val benefitCrystallisationEventFlag: Option[Boolean] =
          userAnswers.get(HadBenefitCrystallisationEventPage).orElse(None)

        val previousLTACharge: Option[Boolean] =
          userAnswers.get(PreviousLTAChargePage).orElse(None)

        val changeInLifetimeAllowancePercentageInformedFlag: Option[Boolean] =
          userAnswers.get(ChangeInLifetimeAllowancePage).orElse(None)

        val increaseInLTACharge: Option[Boolean] =
          userAnswers.get(IncreaseInLTAChargePage).orElse(None)

        val newLTACharge: Option[Boolean] =
          userAnswers.get(NewLTAChargePage).orElse(None)

        val multipleBenefitCrystallisationEventFlag: Option[Boolean] =
          userAnswers.get(MultipleBenefitCrystallisationEventPage).orElse(None)

        val otherSchemeNotification: Option[Boolean] =
          userAnswers.get(OtherSchemeNotificationPage).orElse(None)

        Some(
          LifetimeAllowanceSetup(
            benefitCrystallisationEventFlag,
            previousLTACharge,
            changeInLifetimeAllowancePercentageInformedFlag,
            increaseInLTACharge,
            newLTACharge,
            multipleBenefitCrystallisationEventFlag,
            otherSchemeNotification
          )
        )
      } else None

    Setup(annualAllowanceSetup, lifetimeAllowanceSetup)
  }

  def toTaxYear2011To2015(userAnswers: UserAnswers, period: Period): Option[TaxYear2011To2015] =
    userAnswers.get(PIAPreRemedyPage(period)).map(v => TaxYear2011To2015(v.toInt, period))

  def toTaxYear2016To2023(
    userAnswers: UserAnswers,
    period: Period
  ): Option[TaxYear2016To2023] = {

    val totalIncome: Int = userAnswers.get(TotalIncomePage(period)).map(_.toInt).getOrElse(0)

    val income: Option[Income] =
      if (period == Period._2016)
        None
      else if (userAnswers.get(ThresholdIncomePage(period)).contains(ThresholdIncome.Yes))
        userAnswers.get(KnowAdjustedAmountPage(period)) match {
          case Some(true)  =>
            Some(AboveThreshold(userAnswers.get(AdjustedIncomePage(period)).getOrElse(BigInt(0)).toInt))
          case Some(false) =>
            Some(AboveThreshold(adjustedIncomeCalculation(userAnswers, period).toInt))
          case _           => None
        }
      else if (userAnswers.get(ThresholdIncomePage(period)).contains(ThresholdIncome.IDoNotKnow))
        userAnswers.get(models.AboveThreshold(period)) match {
          case Some(true)  =>
            userAnswers.get(KnowAdjustedAmountPage(period)) match {
              case Some(true)  =>
                Some(AboveThreshold(userAnswers.get(AdjustedIncomePage(period)).getOrElse(BigInt(0)).toInt))
              case Some(false) =>
                Some(AboveThreshold(adjustedIncomeCalculation(userAnswers, period).toInt))
              case _           => None
            }
          case Some(false) => Some(BelowThreshold)
        }
      else
        Some(BelowThreshold)

    val chargePaidByMember: Int =
      userAnswers.get(HowMuchAAChargeYouPaidPage(period, SchemeIndex(0))).map(_.toInt).getOrElse(0)

    val taxYearSchemes: List[TaxYearScheme] =
      Range
        .inclusive(0, 4)
        .flatMap { v =>
          val oPensionSchemeDetails: Option[PensionSchemeDetails] =
            userAnswers.get(PensionSchemeDetailsPage(period, SchemeIndex(v)))

          val oPensionSchemeInputAmounts: Option[PensionSchemeInputAmounts] =
            if (period == Period._2016) {
              userAnswers.get(PensionSchemeInput2016preAmountsPage(period, SchemeIndex(v))).map { pia =>
                PensionSchemeInputAmounts(pia.revisedPIA)
              }
            } else {
              userAnswers.get(PensionSchemeInputAmountsPage(period, SchemeIndex(v)))
            }

          val oPensionSchemeInput2016PostAmounts: Option[PensionSchemeInput2016postAmounts] =
            if (period == Period._2016)
              userAnswers.get(PensionSchemeInput2016postAmountsPage(period, SchemeIndex(v)))
            else
              None

          val oChargePaidByScheme: Option[Int] =
            userAnswers.get(HowMuchAAChargeSchemePaidPage(period, SchemeIndex(v))).map(_.toInt)

          (oPensionSchemeDetails, oPensionSchemeInputAmounts) match {
            case (Some(pensionSchemeDetails), Some(pensionSchemeInputAmounts)) =>
              Some(
                TaxYearScheme(
                  pensionSchemeDetails.schemeName,
                  pensionSchemeDetails.schemeTaxRef,
                  pensionSchemeInputAmounts.revisedPIA.toInt,
                  oChargePaidByScheme.getOrElse(0),
                  oPensionSchemeInput2016PostAmounts.map(_.revisedPIA.toInt)
                )
              )

            case _ => None
          }
        }
        .toList

    if (taxYearSchemes.nonEmpty) {
      val oFlexiAccessDate: Option[LocalDate] =
        userAnswers.get(FlexibleAccessStartDatePage)

      val isFlexiAccessDateInThisPeriod: Option[Boolean] = oFlexiAccessDate.map { flexiAccessDate =>
        (flexiAccessDate.isAfter(period.start) && flexiAccessDate.isBefore(period.end)) ||
        flexiAccessDate.isEqual(period.start) || flexiAccessDate.isEqual(period.end)
      }

      val isFlexiAccessDateBeforeThisPeriod: Option[Boolean] = oFlexiAccessDate.map(_.isBefore(period.start))

      val incomeSubJourney =
        IncomeSubJourney(
          userAnswers.get(AmountSalarySacrificeArrangementsPage(period)).map(_.toInt),
          userAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)).map(_.toInt),
          userAnswers.get(RASContributionAmountPage(period)).map(_.toInt),
          userAnswers.get(LumpSumDeathBenefitsValuePage(period)).map(_.toInt),
          userAnswers.get(models.AboveThreshold(period)),
          userAnswers.get(TaxReliefPage(period)).map(_.toInt),
          userAnswers.get(AdjustedIncomePage(period)).map(_.toInt),
          userAnswers.get(HowMuchTaxReliefPensionPage(period)).map(_.toInt),
          userAnswers.get(HowMuchContributionPensionSchemePage(period)).map(_.toInt),
          userAnswers.get(AmountClaimedOnOverseasPensionPage(period)).map(_.toInt),
          userAnswers.get(AmountOfGiftAidPage(period)).map(_.toInt),
          userAnswers.get(PersonalAllowancePage(period)).map(_.toInt),
          userAnswers.get(UnionPoliceReliefAmountPage(period)).map(_.toInt),
          userAnswers.get(BlindPersonsAllowanceAmountPage(period)).map(_.toInt)
        )

      (isFlexiAccessDateInThisPeriod, isFlexiAccessDateBeforeThisPeriod) match {
        case (Some(true), Some(false)) =>
          val definedBenefitInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedBenefitAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedBenefitInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PostAmountPage).map(_.toInt)
            else
              None

          val definedContributionInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedContributionInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PostAmountPage).map(_.toInt)
            else
              None

          val postAccessDefinedContributionInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PreFlexiAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(FlexiAccessDefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)

          val postAccessDefinedContributionInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PostFlexiAmountPage).map(_.toInt)
            else
              None

          Some(
            InitialFlexiblyAccessedTaxYear(
              definedBenefitInputAmount,
              oFlexiAccessDate,
              definedContributionInputAmount,
              postAccessDefinedContributionInputAmount,
              taxYearSchemes,
              totalIncome,
              chargePaidByMember,
              period,
              incomeSubJourney,
              income,
              definedBenefitInput2016PostAmount,
              definedContributionInput2016PostAmount,
              postAccessDefinedContributionInput2016PostAmount
            )
          )

        case (Some(false), Some(true)) =>
          val definedBenefitInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedBenefitAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedBenefitInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PostAmountPage).map(_.toInt)
            else
              None

          val definedContributionInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedContributionInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PostAmountPage).map(_.toInt)
            else
              None

          Some(
            PostFlexiblyAccessedTaxYear(
              definedBenefitInputAmount,
              definedContributionInputAmount,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              period,
              incomeSubJourney,
              income,
              definedBenefitInput2016PostAmount,
              definedContributionInput2016PostAmount
            )
          )

        case _ =>
          val definedBenefitInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedBenefitAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedBenefitInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedBenefit2016PostAmountPage).map(_.toInt)
            else
              None

          val definedContributionInputAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PreAmountPage).map(_.toInt).getOrElse(0)
            else
              userAnswers.get(DefinedContributionAmountPage(period)).map(_.toInt).getOrElse(0)

          val definedContributionInput2016PostAmount =
            if (period == Period._2016)
              userAnswers.get(DefinedContribution2016PostAmountPage).map(_.toInt)
            else
              None

          (
            definedBenefitInput2016PostAmount,
            definedContributionInput2016PostAmount,
            taxYearSchemes.flatMap(_.revisedPensionInput2016PostAmount)
          ) match {
            case (None, None, Nil) =>
              Some(
                NormalTaxYear(
                  definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
                    .map(_.revisedPensionInputAmount)
                    .sum,
                  taxYearSchemes,
                  totalIncome,
                  chargePaidByMember,
                  period,
                  incomeSubJourney,
                  income
                )
              )

            case _ =>
              Some(
                NormalTaxYear(
                  definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
                    .map(_.revisedPensionInputAmount)
                    .sum,
                  taxYearSchemes,
                  totalIncome,
                  chargePaidByMember,
                  period,
                  incomeSubJourney,
                  income,
                  Some(
                    definedBenefitInput2016PostAmount.getOrElse(0) +
                      definedContributionInput2016PostAmount.getOrElse(0) +
                      taxYearSchemes.map(_.revisedPensionInput2016PostAmount.getOrElse(0)).sum
                  )
                )
              )
          }
      }
    } else
      None
  }

  def buildLifeTimeAllowance(userAnswers: UserAnswers): Option[LifeTimeAllowance] = {
    if (!LTASection.kickoutHasBeenReached(userAnswers)) {
      val benefitCrystallisationEventDate: LocalDate =
        userAnswers.get(DateOfBenefitCrystallisationEventPage).getOrElse(LocalDate.now)

      val lifetimeAllowanceProtectionOrEnhancements: LtaProtectionOrEnhancements =
        userAnswers.get(LtaProtectionOrEnhancementsPage).getOrElse(LtaProtectionOrEnhancements.Protection)

      val protectionType: Option[ProtectionType] = userAnswers.get(ProtectionTypePage)

      val protectionReference: Option[String] = userAnswers.get(ProtectionReferencePage)

      val protectionTypeEnhancementChanged: ProtectionEnhancedChanged =
        userAnswers.get(ProtectionEnhancedChangedPage).getOrElse(ProtectionEnhancedChanged.Protection)

      val newProtectionTypeOrEnhancement: Option[WhatNewProtectionTypeEnhancement] =
        userAnswers.get(WhatNewProtectionTypeEnhancementPage)

      val newProtectionTypeOrEnhancementReference: Option[String] =
        userAnswers.get(ReferenceNewProtectionTypeEnhancementPage)

      val previousLifetimeAllowanceChargeFlag: Boolean = userAnswers.get(LifetimeAllowanceChargePage).getOrElse(false)

      val previousLifetimeAllowanceChargePaymentMethod: Option[ExcessLifetimeAllowancePaid] =
        userAnswers.get(ExcessLifetimeAllowancePaidPage)

      val previousLifetimeAllowanceChargePaidBy: Option[WhoPaidLTACharge] = userAnswers.get(WhoPaidLTAChargePage)

      val previousLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[SchemeNameAndTaxRef] =
        userAnswers.get(SchemeNameAndTaxRefPage)

      val newLifetimeAllowanceChargeWillBePaidBy: Option[WhoPayingExtraLtaCharge] =
        userAnswers.get(WhoPayingExtraLtaChargePage)

      val newLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[LtaPensionSchemeDetails] =
        userAnswers.get(LtaPensionSchemeDetailsPage)

      val enhancementType: Option[EnhancementType] = userAnswers.get(EnhancementTypePage)

      val internationalEnhancementReference: Option[String] = userAnswers.get(InternationalEnhancementReferencePage)

      val pensionCreditReference: Option[String] = userAnswers.get(PensionCreditReferencePage)

      val newEnhancementType: Option[NewEnhancementType] = userAnswers.get(NewEnhancementTypePage)

      val newInternationalEnhancementReference: Option[String] =
        userAnswers.get(NewInternationalEnhancementReferencePage)

      val newPensionCreditReference: Option[String] = userAnswers.get(NewPensionCreditReferencePage)

      val lumpSumValue: Option[Int] = userAnswers.get(LumpSumValuePage).map(_.toInt)

      val annualPaymentValue: Option[Int] = userAnswers.get(AnnualPaymentValuePage).map(_.toInt)

      val userSchemeDetails: Option[UserSchemeDetails] = userAnswers.get(UserSchemeDetailsPage)

      val quarterChargePaid: Option[QuarterChargePaid] = userAnswers.get(QuarterChargePaidPage)

      val yearChargePaid: Option[YearChargePaid] = userAnswers.get(YearChargePaidPage)

      val newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid] =
        userAnswers.get(NewExcessLifetimeAllowancePaidPage)

      val newLumpSumValue: Option[Int] = userAnswers.get(NewLumpSumValuePage).map(_.toInt)

      val newAnnualPaymentValue = userAnswers.get(NewAnnualPaymentValuePage).map(_.toInt)

      val newLifeTimeAllowanceAdditions: NewLifeTimeAllowanceAdditions =
        NewLifeTimeAllowanceAdditions(
          enhancementType,
          internationalEnhancementReference,
          pensionCreditReference,
          newEnhancementType,
          newInternationalEnhancementReference,
          newPensionCreditReference,
          lumpSumValue,
          annualPaymentValue,
          userSchemeDetails,
          quarterChargePaid,
          yearChargePaid,
          newExcessLifetimeAllowancePaid,
          newLumpSumValue,
          newAnnualPaymentValue
        )

      Some(
        LifeTimeAllowance(
          benefitCrystallisationEventDate,
          lifetimeAllowanceProtectionOrEnhancements,
          protectionType,
          protectionReference,
          protectionTypeEnhancementChanged,
          newProtectionTypeOrEnhancement,
          newProtectionTypeOrEnhancementReference,
          previousLifetimeAllowanceChargeFlag,
          previousLifetimeAllowanceChargePaymentMethod,
          previousLifetimeAllowanceChargePaidBy,
          previousLifetimeAllowanceChargeSchemeNameAndTaxRef,
          newLifetimeAllowanceChargeWillBePaidBy,
          newLifetimeAllowanceChargeSchemeNameAndTaxRef,
          newLifeTimeAllowanceAdditions
        )
      )
    } else None
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

  def adjustedIncomeCalculation(userAnswers: UserAnswers, period: Period): BigInt = {

    val netIncomeAfterDeductingTaxRelief = userAnswers.get(TotalIncomePage(period)).getOrElse(BigInt(0)) - userAnswers
      .get(TaxReliefPage(period))
      .getOrElse(BigInt(0))

    val taxReliefClaimedOnPensionContributions =
      userAnswers.get(HowMuchTaxReliefPensionPage(period)).getOrElse(BigInt(0))

    val employeeContributions = userAnswers.get(HowMuchContributionPensionSchemePage(period)).getOrElse(BigInt(0)) -
      userAnswers.get(RASContributionAmountPage(period)).getOrElse(BigInt(0))

    val employerTotalContributions = calculateRevisedPIATotal(userAnswers, period) +
      userAnswers.get(DefinedContributionAmountPage(period)).getOrElse(BigInt(0)) +
      userAnswers.get(FlexiAccessDefinedContributionAmountPage(period)).getOrElse(BigInt(0)) +
      userAnswers.get(DefinedBenefitAmountPage(period)).getOrElse(BigInt(0)) -
      userAnswers.get(HowMuchContributionPensionSchemePage(period)).getOrElse(BigInt(0))

    val reliefClaimedOnOverseasPensions =
      userAnswers.get(AmountClaimedOnOverseasPensionPage(period)).getOrElse(BigInt(0))

    val lumpSumDeathBenefits = userAnswers.get(LumpSumDeathBenefitsValuePage(period)).getOrElse(BigInt(0))

    netIncomeAfterDeductingTaxRelief +
      taxReliefClaimedOnPensionContributions +
      employeeContributions +
      employerTotalContributions +
      reliefClaimedOnOverseasPensions -
      lumpSumDeathBenefits

  }

  private def calculateRevisedPIATotal(userAnswers: UserAnswers, period: Period): BigInt =
    (0 to 4)
      .flatMap(schemeIndexes =>
        userAnswers.get(PensionSchemeInputAmountsPage(period, SchemeIndex.fromString(schemeIndexes.toString).get))
      )
      .map(_.revisedPIA)
      .sum

}
