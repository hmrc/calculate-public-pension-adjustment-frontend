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

import connectors.{CalculationResultConnector, ReducedNetIncomeConnector, SubmissionsConnector}
import controllers.annualallowance.taxyear.AboveThresholdController
import models.CalculationResults._
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.submission.{SubmissionRequest, SubmissionResponse}
import models.tasklist.sections.LTASection
import models.{AAKickOutStatus, AnnualAllowance, BeforeCalculationAuditEvent, CalculationAuditEvent, CalculationResults, EnhancementType, ExcessLifetimeAllowancePaid, Income, IncomeSubJourney, LTAKickOutStatus, LifeTimeAllowance, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, NewEnhancementType, NewExcessLifetimeAllowancePaid, NewLifeTimeAllowanceAdditions, PensionSchemeDetails, PensionSchemeInput2016postAmounts, PensionSchemeInputAmounts, Period, PostTriageFlag, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, ReducedNetIncomeRequest, ReducedNetIncomeResponse, ReportingChange, SchemeIndex, SchemeNameAndTaxRef, TaxYear, TaxYear2011To2015, TaxYear2016To2023, TaxYearScheme, ThresholdIncome, UserAnswers, UserSchemeDetails, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, PIAPreRemedyPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.annualallowance._
import pages.setupquestions.lifetimeallowance._
import pages.setupquestions.{ReasonForResubmissionPage, ReportingChangePage, ResubmittingAdjustmentPage}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import utils.CurrencyFormatter.currencyFormat

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationResultService @Inject() (
  calculationResultConnector: CalculationResultConnector,
  submissionsConnector: SubmissionsConnector,
  reducedNetIncomeConnector: ReducedNetIncomeConnector,
  auditService: AuditService,
  aboveThresholdController: AboveThresholdController
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(userAnswers: UserAnswers)(implicit
    hc: HeaderCarrier
  ): Future[CalculationResponse] =
    for {
      calculationInputs   <- buildCalculationInputs(userAnswers)
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
  ): Future[SubmissionResponse] =
    for {
      calculationInputs   <- buildCalculationInputs(answers)
      calculationResponse <- calculationResultConnector.sendRequest(calculationInputs)
      submissionResponse  <-
        submissionsConnector.sendSubmissionRequest(
          SubmissionRequest(calculationInputs, Some(calculationResponse), userId, answers.uniqueId)
        )
    } yield submissionResponse

  def submitUserAnswersWithNoCalculation(answers: UserAnswers, userId: String)(implicit
    hc: HeaderCarrier
  ): Future[SubmissionResponse] =
    for {
      calculationInputs  <- buildCalculationInputs(answers)
      submissionResponse <-
        submissionsConnector.sendSubmissionRequest(
          SubmissionRequest(calculationInputs, None, userId, answers.uniqueId)
        )
    } yield submissionResponse

  def buildCalculationInputs(userAnswers: UserAnswers)(implicit
    hc: HeaderCarrier
  ): Future[CalculationInputs] = for {
    listOf2016To2023TaxYears <- Future.sequence(
                                  List(
                                    Period._2016,
                                    Period._2017,
                                    Period._2018,
                                    Period._2019,
                                    Period._2020,
                                    Period._2021,
                                    Period._2022,
                                    Period._2023
                                  ).map(
                                    toTaxYear2016To2023(userAnswers, _)
                                  )
                                )

    calculationInputs = {
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

      val tYears: List[TaxYear] = _2011To2015TaxYears ++ listOf2016To2023TaxYears.flatten

      val reportingChange: Option[Set[ReportingChange]] = userAnswers.get(ReportingChangePage)

      val postTriageFlagStatus = userAnswers.get(PostTriageFlag).isDefined

      CalculationResults.CalculationInputs(
        resubmission,
        buildSetup(userAnswers),
        if (postTriageFlagStatus) {
          buildSetupAAPostTriage(userAnswers, scottishTaxYears, tYears, reportingChange)
        } else {
          buildSetupAAPreTriage(userAnswers, scottishTaxYears, tYears, reportingChange)
        },
        if (postTriageFlagStatus) {
          buildSetupLTAPostTriage(userAnswers, reportingChange)
        } else {
          buildSetupLTAPreTriage(userAnswers, reportingChange)
        }
      )
    }
  } yield calculationInputs

  private def buildSetupLTAPostTriage(userAnswers: UserAnswers, reportingChange: Option[Set[ReportingChange]]) =
    if (
      reportingChange
        .exists(_.contains(models.ReportingChange.LifetimeAllowance)) && userAnswers.get(LTAKickOutStatus()).get == 2
    ) {
      buildLifeTimeAllowance(userAnswers)
    } else None

  private def buildSetupLTAPreTriage(userAnswers: UserAnswers, reportingChange: Option[Set[ReportingChange]]) =
    if (
      reportingChange
        .exists(_.contains(models.ReportingChange.LifetimeAllowance))
    ) {
      buildLifeTimeAllowance(userAnswers)
    } else None

  private def buildSetupAAPostTriage(
    userAnswers: UserAnswers,
    scottishTaxYears: List[Period],
    tYears: List[TaxYear],
    reportingChange: Option[Set[ReportingChange]]
  ) =
    if (
      reportingChange
        .exists(_.contains(models.ReportingChange.AnnualAllowance)) && userAnswers.get(AAKickOutStatus()).get == 2
    ) {
      Some(AnnualAllowance(scottishTaxYears, tYears))
    } else None

  private def buildSetupAAPreTriage(
    userAnswers: UserAnswers,
    scottishTaxYears: List[Period],
    tYears: List[TaxYear],
    reportingChange: Option[Set[ReportingChange]]
  ) =
    if (
      reportingChange
        .exists(_.contains(models.ReportingChange.AnnualAllowance))
    ) {
      Some(AnnualAllowance(scottishTaxYears, tYears))
    } else None

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
  )(implicit
    hc: HeaderCarrier
  ): Future[Option[TaxYear2016To2023]] = {

    val totalIncome: Int = userAnswers.get(TotalIncomePage(period)).map(_.toInt).getOrElse(0)

    val adjustedIncomeAmount: Option[BigInt] = {
      def getAdjustedIncomeValue =
        userAnswers.get(AdjustedIncomePage(period)) match {
          case a @ Some(_) => a
          case None        => Some(adjustedIncomeCalculation(userAnswers, period))
        }

      if (userAnswers.get(ThresholdIncomePage(period)).contains(ThresholdIncome.Yes))
        getAdjustedIncomeValue
      else if (userAnswers.get(ThresholdIncomePage(period)).contains(ThresholdIncome.IDoNotKnow))
        userAnswers.get(models.AboveThreshold(period)) match {
          case Some(true) => getAdjustedIncomeValue
          case _          => None
        }
      else None
    }

    val income: Option[Income] = period match {
      case Period._2016 => None
      case _            =>
        adjustedIncomeAmount match {
          case Some(amount) => Some(AboveThreshold(amount.toInt))
          case None         => Some(BelowThreshold)
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

      val thresholdIncomeAmount: Option[BigInt] =
        userAnswers.get(ThresholdIncomePage(period)) match {
          case Some(ThresholdIncome.IDoNotKnow) =>
            Some(aboveThresholdController.calculateThresholdStatus(userAnswers, period))
          case _                                => None
        }

      val incomeSubJourney =
        IncomeSubJourney(
          userAnswers.get(AmountSalarySacrificeArrangementsPage(period)).map(_.toInt),
          userAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)).map(_.toInt),
          userAnswers.get(RASContributionAmountPage(period)).map(_.toInt),
          userAnswers.get(LumpSumDeathBenefitsValuePage(period)).map(_.toInt),
          userAnswers.get(models.AboveThreshold(period)),
          userAnswers.get(TaxReliefPage(period)).map(_.toInt),
          adjustedIncomeAmount.map(_.toInt),
          userAnswers.get(HowMuchTaxReliefPensionPage(period)).map(_.toInt),
          userAnswers.get(HowMuchContributionPensionSchemePage(period)).map(_.toInt),
          userAnswers.get(AmountClaimedOnOverseasPensionPage(period)).map(_.toInt),
          userAnswers.get(AmountOfGiftAidPage(period)).map(_.toInt),
          userAnswers.get(PersonalAllowancePage(period)).map(_.toInt),
          userAnswers.get(UnionPoliceReliefAmountPage(period)).map(_.toInt),
          userAnswers.get(BlindPersonsAllowanceAmountPage(period)).map(_.toInt),
          thresholdIncomeAmount.map(_.toInt),
          None
        )

      val scottishTaxYears: List[Period] = userAnswers.data.fields
        .find(_._1 == WhichYearsScottishTaxpayerPage.toString)
        .fold {
          List.empty[Period]
        } {
          _._2.as[List[String]] flatMap { sYear =>
            Period.fromString(sYear)
          }
        }
      val reducedNetIncomeRequest        = ReducedNetIncomeRequest(period, scottishTaxYears, totalIncome, incomeSubJourney)

      def updatedIncomeSubJourney: Future[IncomeSubJourney] =
        reducedNetIncomeConnector.sendReducedNetIncomeRequest(reducedNetIncomeRequest).map {
          case (ReducedNetIncomeResponse(personalAllowance, reducedNetIncome)) =>
            incomeSubJourney.copy(
              reducedNetIncomeAmount = Some(reducedNetIncome),
              personalAllowanceAmount = Some(personalAllowance)
            )
        }

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

          updatedIncomeSubJourney.map { uIncomeSubJourney =>
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
                uIncomeSubJourney,
                income,
                definedBenefitInput2016PostAmount,
                definedContributionInput2016PostAmount,
                postAccessDefinedContributionInput2016PostAmount
              )
            )
          }

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

          updatedIncomeSubJourney.map { uIncomeSubJourney =>
            Some(
              PostFlexiblyAccessedTaxYear(
                definedBenefitInputAmount,
                definedContributionInputAmount,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                period,
                uIncomeSubJourney,
                income,
                definedBenefitInput2016PostAmount,
                definedContributionInput2016PostAmount
              )
            )
          }

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
              updatedIncomeSubJourney.map { uIncomeSubJourney =>
                Some(
                  NormalTaxYear(
                    definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
                      .map(_.revisedPensionInputAmount)
                      .sum,
                    taxYearSchemes,
                    totalIncome,
                    chargePaidByMember,
                    period,
                    uIncomeSubJourney,
                    income
                  )
                )
              }

            case _ =>
              updatedIncomeSubJourney.map { uIncomeSubJourney =>
                Some(
                  NormalTaxYear(
                    definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
                      .map(_.revisedPensionInputAmount)
                      .sum,
                    taxYearSchemes,
                    totalIncome,
                    chargePaidByMember,
                    period,
                    uIncomeSubJourney,
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
      }
    } else
      Future.successful(None)
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

  def calculationReviewViewModel(calculationResponse: CalculationResponse): CalculationReviewViewModel = {
    val outDatesVal: Seq[Seq[ReviewRowViewModel]] = outDatesReview(calculationResponse)
    val inDatesVal: Seq[Seq[ReviewRowViewModel]]  = inDatesReview(calculationResponse)
    val resubmissionVal: Resubmission             = calculationResponse.resubmission
    CalculationReviewViewModel(outDatesVal, inDatesVal, resubmissionVal)
  }

  private def outDatesReview(calculationResponse: CalculationResponse): Seq[Seq[ReviewRowViewModel]] =
    calculationResponse.outDates.map { outDate =>
      Seq(
        ReviewRowViewModel(
          "calculationReview.period." + outDate.period.toString,
          Some(changeInAAOutDateTaxCharge(outDate)),
          controllers.routes.CalculationReviewIndividualAAController.onPageLoad(outDate.period).url,
          outDate.adjustedCompensation.map(Math.abs).orElse(Some(0))
        )
      )
    }

  private def changeInAAOutDateTaxCharge(outDate: OutOfDatesTaxYearsCalculation): String = {
    val totalCharge = outDate.adjustedCompensation
    if (totalCharge.contains(0)) {
      "calculationReview.taxChargeNotChanged"
    } else {
      "calculationReview.taxChargeDecreasedBy"
    }
  }

  private def inDatesReview(calculationResponse: CalculationResponse): Seq[Seq[ReviewRowViewModel]] =
    calculationResponse.inDates.map { inDate =>
      Seq(
        ReviewRowViewModel(
          "calculationReview.period." + inDate.period.toString,
          Some(changeInAAInDateTaxCharge(inDate)),
          controllers.routes.CalculationReviewIndividualAAController.onPageLoad(inDate.period).url,
          inDate.totalCompensation.map(Math.abs).orElse(Some(0))
        )
      )
    }

  private def changeInAAInDateTaxCharge(inDate: InDatesTaxYearsCalculation): String = {
    val totalCharge = inDate.totalCompensation
    if (totalCharge.contains(0)) {
      "calculationReview.taxChargeNotChanged"
    } else if (totalCharge.exists(_ > 0)) {
      "calculationReview.taxChargeDecreasedBy"
    } else {
      "calculationReview.taxChargeIncreasedBy"
    }
  }

  def individualAASummaryModel(calculationResponse: CalculationResponse): Seq[IndividualAASummaryModel] =
    outDatesSummary(calculationResponse) ++ inDatesSummary(calculationResponse)

  def outDatesSummary(calculationResponse: CalculationResponse): Seq[IndividualAASummaryModel] =
    calculationResponse.outDates.map { outDate =>
      val changeInTaxChargeAmount =
        outDate.adjustedCompensation.getOrElse(0)

      val messageKey = if (changeInTaxChargeAmount > 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.decrease."
      } else {
        "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
      }

      def writtenOffAmountFormatter(changeinTaxCharge: Int, writtenOffAmount: Option[Int]): Option[Int] =
        changeinTaxCharge match {
          case 0 => writtenOffAmount
          case _ => None
        }

      IndividualAASummaryModel(
        outDate.period,
        changeInTaxChargeAmount.abs,
        changeInTaxChargeAmount,
        messageKey,
        outDate.revisedChargableAmountBeforeTaxRate,
        outDate.chargePaidByMember,
        outDate.chargePaidBySchemes,
        outDate.revisedChargableAmountAfterTaxRate,
        outDate.chargePaidByMember + outDate.chargePaidBySchemes,
        writtenOffAmountFormatter(
          changeInTaxChargeAmount,
          Some(outDate.revisedChargableAmountAfterTaxRate - (outDate.chargePaidByMember + outDate.chargePaidBySchemes))
        )
      )
    }

  def inDatesSummary(calculationResponse: CalculationResponse): Seq[IndividualAASummaryModel] =
    calculationResponse.inDates.map { inDate =>
      val changeInTaxChargeAmount = inDate.totalCompensation.getOrElse(0)

      val messageKey = if (changeInTaxChargeAmount > 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.decrease."
      } else if (changeInTaxChargeAmount < 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.increase."
      } else {
        "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
      }

      IndividualAASummaryModel(
        inDate.period,
        changeInTaxChargeAmount.abs,
        changeInTaxChargeAmount,
        messageKey,
        inDate.revisedChargableAmountBeforeTaxRate,
        inDate.chargePaidByMember,
        inDate.chargePaidBySchemes,
        inDate.revisedChargableAmountAfterTaxRate,
        inDate.chargePaidByMember + inDate.chargePaidBySchemes,
        None
      )
    }

  def calculationReviewIndividualAAViewModel(
    calculateResponse: CalculationResponse,
    period: Option[String],
    userAnswers: UserAnswers
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CalculationReviewIndividualAAViewModel] = {
    val outDatesFuture: Future[Seq[Seq[RowViewModel]]] = outDatesReviewAA(calculateResponse, period, userAnswers)
    val inDatesFuture: Future[Seq[Seq[RowViewModel]]]  = inDatesReviewAA(calculateResponse, period, userAnswers)

    for {
      outDatesVal <- outDatesFuture
      inDatesVal  <- inDatesFuture
    } yield CalculationReviewIndividualAAViewModel(outDatesVal, inDatesVal)
  }

  def outDatesReviewAA(
    calculateResponse: CalculationResponse,
    period: Option[String],
    userAnswers: UserAnswers
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Seq[RowViewModel]]] = {
    val taxYearsFutures: List[Future[Option[TaxYear2016To2023]]] = List(
      Period._2016,
      Period._2017,
      Period._2018,
      Period._2019,
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    ).map(toTaxYear2016To2023(userAnswers, _))

    val resolvedTaxYears: Future[List[Option[TaxYear2016To2023]]] = Future.sequence(taxYearsFutures)

    resolvedTaxYears.map { _2016To2023TaxYears =>
      outDatesReviewAAFiltered(period, calculateResponse.outDates)
        .map { outDate =>
          val taxYear: IncomeSubJourney = taxYearIncomeSubJourney(_2016To2023TaxYears.flatten, outDate.period)
          Seq(
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember",
              currencyFormat(outDate.chargePaidByMember.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes",
              currencyFormat(outDate.chargePaidBySchemes.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.reducedNetIncome",
              currencyFormat(taxYear.reducedNetIncomeAmount.getOrElse(0).toString)
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.personalAllowance",
              currencyFormat(taxYear.personalAllowanceAmount.getOrElse(0).toString)
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.thresholdIncome",
              currencyFormat(thresholdIncomeMessage(outDate.period, taxYear))
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.adjustedIncome",
              currencyFormat(adjustedIncomeMessage(outDate.period, taxYear))
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
              currencyFormat(outDate.unusedAnnualAllowance.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
              currencyFormat(outDate.revisedChargableAmountBeforeTaxRate.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
              currencyFormat(outDate.revisedChargableAmountAfterTaxRate.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.amountYouOwe",
              currencyFormat("0")
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
              currencyFormat(outDate.directCompensation.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
              currencyFormat(outDate.indirectCompensation.toString())
            )
          )
        }
    }
  }

  private def outDatesReviewAAFiltered(
    period: Option[String],
    outDates: List[OutOfDatesTaxYearsCalculation]
  ): List[OutOfDatesTaxYearsCalculation] =
    if (period.isDefined) {
      outDates.filter(outDate => outDate.period.toString == period.get)
    } else outDates

  private def inDatesReviewAA(
    calculateResponse: CalculationResponse,
    period: Option[String],
    userAnswers: UserAnswers
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Seq[RowViewModel]]] = {
    val taxYearsFutures: List[Future[Option[TaxYear2016To2023]]] = List(
      Period._2016,
      Period._2017,
      Period._2018,
      Period._2019,
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    ).map(toTaxYear2016To2023(userAnswers, _))

    val resolvedTaxYears: Future[List[Option[TaxYear2016To2023]]] = Future.sequence(taxYearsFutures)

    resolvedTaxYears.map { _2016To2023TaxYears =>
      inDatesReviewAAFiltered(period, calculateResponse.inDates)
        .map { inDate =>
          val taxYear: IncomeSubJourney = taxYearIncomeSubJourney(_2016To2023TaxYears.flatten, inDate.period)
          Seq(
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember",
              currencyFormat(inDate.chargePaidByMember.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes",
              currencyFormat(inDate.chargePaidBySchemes.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.reducedNetIncome",
              currencyFormat(taxYear.reducedNetIncomeAmount.getOrElse(0).toString)
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.personalAllowance",
              currencyFormat(taxYear.personalAllowanceAmount.getOrElse(0).toString)
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.thresholdIncome",
              currencyFormat(thresholdIncomeMessage(inDate.period, taxYear))
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.adjustedIncome",
              currencyFormat(adjustedIncomeMessage(inDate.period, taxYear))
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
              currencyFormat(inDate.unusedAnnualAllowance.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
              currencyFormat(inDate.revisedChargableAmountBeforeTaxRate.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
              currencyFormat(inDate.revisedChargableAmountAfterTaxRate.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.debit",
              currencyFormat(inDate.debit.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.memberCredit",
              currencyFormat(inDate.memberCredit.toString())
            ),
            RowViewModel(
              "calculationReviewIndividualAA.annualResults.inDates.schemeCredit",
              currencyFormat(inDate.schemeCredit.toString())
            )
          )
        }
    }
  }

  private def inDatesReviewAAFiltered(
    period: Option[String],
    inDates: List[InDatesTaxYearsCalculation]
  ): List[InDatesTaxYearsCalculation] =
    if (period.isDefined) inDates.filter(inDate => inDate.period.toString == period.get)
    else inDates

  private def thresholdIncomeMessage(
    period: Period,
    incomeSubJourney: IncomeSubJourney
  ): String =
    period match {
      case Period._2016 => "notApplicable"
      case _            => incomeSubJourney.thresholdIncomeAmount.map(_.toString).getOrElse("notApplicable")
    }

  private def adjustedIncomeMessage(period: Period, incomeSubJourney: IncomeSubJourney): String =
    period match {
      case Period._2016 => "notApplicable"
      case _            => incomeSubJourney.adjustedIncomeAmount.map(_.toString).getOrElse("notApplicable")
    }

  private def taxYearIncomeSubJourney(taxYears: List[TaxYear2016To2023], period: Period): IncomeSubJourney =
    taxYears.filter(ty => ty.period == period).head match {
      case ty: NormalTaxYear                  => ty.incomeSubJourney
      case ty: InitialFlexiblyAccessedTaxYear => ty.incomeSubJourney
      case ty: PostFlexiblyAccessedTaxYear    => ty.incomeSubJourney
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
