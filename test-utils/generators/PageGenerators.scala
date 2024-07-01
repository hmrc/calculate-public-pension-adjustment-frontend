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

package generators

import org.scalacheck.Arbitrary
import pages.PreviousClaimContinuePage
import pages.annualallowance.preaaquestions.{RegisteredYearPage, ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.ReportingChangePage

trait PageGenerators {

  implicit lazy val arbitraryDidYouContributeToRASSchemePage: Arbitrary[DidYouContributeToRASSchemePage.type] =
    Arbitrary(DidYouContributeToRASSchemePage)

  implicit lazy val arbitraryRASContributionAmountPage: Arbitrary[RASContributionAmountPage.type] =
    Arbitrary(RASContributionAmountPage)

  implicit lazy val arbitraryBlindPersonsAllowanceAmountPage: Arbitrary[BlindPersonsAllowanceAmountPage.type] =
    Arbitrary(BlindPersonsAllowanceAmountPage)

  implicit lazy val arbitraryBlindAllowancePage: Arbitrary[BlindAllowancePage.type] =
    Arbitrary(BlindAllowancePage)

  implicit lazy val arbitraryMarriageAllowanceAmountPage: Arbitrary[MarriageAllowanceAmountPage.type] =
    Arbitrary(MarriageAllowanceAmountPage)

  implicit lazy val arbitraryMarriageAllowancePage: Arbitrary[MarriageAllowancePage.type] =
    Arbitrary(MarriageAllowancePage)

  implicit lazy val arbitraryClaimingTaxReliefPensionPage: Arbitrary[ClaimingTaxReliefPensionPage.type] =
    Arbitrary(ClaimingTaxReliefPensionPage)

  implicit lazy val arbitraryDoYouKnowPersonalAllowancePage: Arbitrary[DoYouKnowPersonalAllowancePage.type] =
    Arbitrary(DoYouKnowPersonalAllowancePage)

  implicit lazy val arbitraryAreYouNonDomPage: Arbitrary[AreYouNonDomPage.type] =
    Arbitrary(AreYouNonDomPage)

  implicit lazy val arbitraryHasReliefClaimedOnOverseasPensionPage
    : Arbitrary[HasReliefClaimedOnOverseasPensionPage.type] =
    Arbitrary(HasReliefClaimedOnOverseasPensionPage)

  implicit lazy val arbitraryAmountClaimedOnOverseasPensionPage: Arbitrary[AmountClaimedOnOverseasPensionPage.type] =
    Arbitrary(AmountClaimedOnOverseasPensionPage)

  implicit lazy val arbitraryFlexibleRemunerationArrangementsPage
    : Arbitrary[FlexibleRemunerationArrangementsPage.type] =
    Arbitrary(FlexibleRemunerationArrangementsPage)

  implicit lazy val arbitraryAnySalarySacrificeArrangementsPage: Arbitrary[AnySalarySacrificeArrangementsPage.type] =
    Arbitrary(AnySalarySacrificeArrangementsPage)

  implicit lazy val arbitraryAmountFlexibleRemunerationArrangementsPage
    : Arbitrary[AmountFlexibleRemunerationArrangementsPage.type] =
    Arbitrary(AmountFlexibleRemunerationArrangementsPage)

  implicit lazy val arbitraryHowMuchContributionPensionSchemePage
    : Arbitrary[HowMuchContributionPensionSchemePage.type] =
    Arbitrary(HowMuchContributionPensionSchemePage)

  implicit lazy val arbitraryAmountSalarySacrificeArrangementsPage
    : Arbitrary[AmountSalarySacrificeArrangementsPage.type] =
    Arbitrary(AmountSalarySacrificeArrangementsPage)

  implicit lazy val arbitraryLumpSumDeathBenefitsValuePage: Arbitrary[LumpSumDeathBenefitsValuePage.type] =
    Arbitrary(LumpSumDeathBenefitsValuePage)

  implicit lazy val arbitraryAnyLumpSumDeathBenefitsPage: Arbitrary[AnyLumpSumDeathBenefitsPage.type] =
    Arbitrary(AnyLumpSumDeathBenefitsPage)

  implicit lazy val arbitraryClaimingTaxReliefPensionNotAdjustedIncomePage
    : Arbitrary[ClaimingTaxReliefPensionNotAdjustedIncomePage.type] =
    Arbitrary(ClaimingTaxReliefPensionNotAdjustedIncomePage)

  implicit lazy val arbitraryHowMuchTaxReliefPensionPage: Arbitrary[HowMuchTaxReliefPensionPage.type] =
    Arbitrary(HowMuchTaxReliefPensionPage)

  implicit lazy val arbitraryKnowAdjustedAmountPage: Arbitrary[KnowAdjustedAmountPage.type] =
    Arbitrary(KnowAdjustedAmountPage)

  implicit lazy val arbitraryTaxReliefPage: Arbitrary[TaxReliefPage.type] =
    Arbitrary(TaxReliefPage)

  implicit lazy val arbitraryPersonalAllowancePage: Arbitrary[PersonalAllowancePage.type] =
    Arbitrary(PersonalAllowancePage)

  implicit lazy val arbitraryPreviousClaimContinuePage: Arbitrary[PreviousClaimContinuePage.type] =
    Arbitrary(PreviousClaimContinuePage)

  implicit lazy val arbitraryDefinedContribution2016PreFlexiAmountPage
    : Arbitrary[DefinedContribution2016PreFlexiAmountPage.type] =
    Arbitrary(DefinedContribution2016PreFlexiAmountPage)

  implicit lazy val arbitraryDefinedContribution2016PostFlexiAmountPage
    : Arbitrary[DefinedContribution2016PostFlexiAmountPage.type] =
    Arbitrary(DefinedContribution2016PostFlexiAmountPage)

  implicit lazy val arbitraryDefinedBenefit2016PostAmountPage: Arbitrary[DefinedBenefit2016PostAmountPage.type] =
    Arbitrary(DefinedBenefit2016PostAmountPage)

  implicit lazy val arbitraryDefinedContribution2016PreAmountPage
    : Arbitrary[DefinedContribution2016PreAmountPage.type] =
    Arbitrary(DefinedContribution2016PreAmountPage)

  implicit lazy val arbitraryDefinedContribution2016PostAmountPage
    : Arbitrary[DefinedContribution2016PostAmountPage.type] =
    Arbitrary(DefinedContribution2016PostAmountPage)

  implicit lazy val arbitraryDefinedBenefit2016PreAmountPage: Arbitrary[DefinedBenefit2016PreAmountPage.type] =
    Arbitrary(DefinedBenefit2016PreAmountPage)

  implicit lazy val arbitraryRegisteredYearPage: Arbitrary[RegisteredYearPage.type] =
    Arbitrary(RegisteredYearPage)

  implicit lazy val arbitraryMultipleBenefitCrystallisationEventPage
    : Arbitrary[MultipleBenefitCrystallisationEventPage.type] =
    Arbitrary(MultipleBenefitCrystallisationEventPage)

  implicit lazy val arbitraryUserSchemeDetailsPage: Arbitrary[UserSchemeDetailsPage.type] =
    Arbitrary(UserSchemeDetailsPage)

  implicit lazy val arbitraryNewExcessLifetimeAllowancePaidPage: Arbitrary[NewExcessLifetimeAllowancePaidPage.type] =
    Arbitrary(NewExcessLifetimeAllowancePaidPage)

  implicit lazy val arbitraryNewLumpSumValuePage: Arbitrary[NewLumpSumValuePage.type] =
    Arbitrary(NewLumpSumValuePage)

  implicit lazy val arbitraryNewAnnualPaymentValuePage: Arbitrary[NewAnnualPaymentValuePage.type] =
    Arbitrary(NewAnnualPaymentValuePage)

  implicit lazy val arbitraryYearChargePaidPage: Arbitrary[YearChargePaidPage.type] =
    Arbitrary(YearChargePaidPage)

  implicit lazy val arbitraryQuarterChargePaidPage: Arbitrary[QuarterChargePaidPage.type] =
    Arbitrary(QuarterChargePaidPage)

  implicit lazy val arbitraryLumpSumValuePage: Arbitrary[LumpSumValuePage.type] =
    Arbitrary(LumpSumValuePage)

  implicit lazy val arbitraryAnnualPaymentValuePage: Arbitrary[AnnualPaymentValuePage.type] =
    Arbitrary(AnnualPaymentValuePage)

  // scala fmt ignore

  implicit lazy val arbitraryPensionCreditReferencePage: Arbitrary[PensionCreditReferencePage.type] =
    Arbitrary(PensionCreditReferencePage)

  implicit lazy val arbitraryInternationalEnhancementReferencePage
    : Arbitrary[InternationalEnhancementReferencePage.type] =
    Arbitrary(InternationalEnhancementReferencePage)

  implicit lazy val arbitraryEnhancementTypePage: Arbitrary[EnhancementTypePage.type] =
    Arbitrary(EnhancementTypePage)

  implicit lazy val arbitraryNewPensionCreditReferencePage: Arbitrary[NewPensionCreditReferencePage.type] =
    Arbitrary(NewPensionCreditReferencePage)

  implicit lazy val arbitraryNewInternationalEnhancementReferencePage
    : Arbitrary[NewInternationalEnhancementReferencePage.type] =
    Arbitrary(NewInternationalEnhancementReferencePage)

  implicit lazy val arbitraryNewEnhancementTypePage: Arbitrary[NewEnhancementTypePage.type] =
    Arbitrary(NewEnhancementTypePage)

  implicit lazy val arbitraryFlexiAccessDefinedContributionAmountPage
    : Arbitrary[FlexiAccessDefinedContributionAmountPage.type] =
    Arbitrary(FlexiAccessDefinedContributionAmountPage)

  implicit lazy val arbitraryThresholdIncomePage: Arbitrary[ThresholdIncomePage.type] =
    Arbitrary(ThresholdIncomePage)

  implicit lazy val arbitraryOtherDefinedBenefitOrContributionPage
    : Arbitrary[OtherDefinedBenefitOrContributionPage.type] =
    Arbitrary(OtherDefinedBenefitOrContributionPage)

  implicit lazy val arbitraryTotalIncomePage: Arbitrary[TotalIncomePage.type] =
    Arbitrary(TotalIncomePage)

  implicit lazy val arbitraryAdjustedIncomePage: Arbitrary[AdjustedIncomePage.type] =
    Arbitrary(AdjustedIncomePage)

  implicit lazy val arbitraryDefinedBenefitAmountPage: Arbitrary[DefinedBenefitAmountPage.type] =
    Arbitrary(DefinedBenefitAmountPage)

  implicit lazy val arbitraryDefinedContributionAmountPage: Arbitrary[DefinedContributionAmountPage.type] =
    Arbitrary(DefinedContributionAmountPage)

  implicit lazy val arbitraryContributedToDuringRemedyPeriodPage: Arbitrary[ContributedToDuringRemedyPeriodPage.type] =
    Arbitrary(ContributedToDuringRemedyPeriodPage)

  implicit lazy val arbitraryWhoPaidAAChargePage: Arbitrary[WhoPaidAAChargePage.type] =
    Arbitrary(WhoPaidAAChargePage)

  implicit lazy val arbitraryHowMuchAAChargeYouPaidPage: Arbitrary[HowMuchAAChargeYouPaidPage.type] =
    Arbitrary(HowMuchAAChargeYouPaidPage)

  implicit lazy val arbitraryHowMuchAAChargeSchemePaidPage: Arbitrary[HowMuchAAChargeSchemePaidPage.type] =
    Arbitrary(HowMuchAAChargeSchemePaidPage)

  implicit lazy val arbitrarySchemeNameAndTaxRefPage: Arbitrary[SchemeNameAndTaxRefPage.type] =
    Arbitrary(SchemeNameAndTaxRefPage)

  implicit lazy val arbitraryWhoPaidLTAChargePage: Arbitrary[WhoPaidLTAChargePage.type] =
    Arbitrary(WhoPaidLTAChargePage)

  implicit lazy val arbitraryLtaPensionSchemeDetailsPage: Arbitrary[LtaPensionSchemeDetailsPage.type] =
    Arbitrary(LtaPensionSchemeDetailsPage)

  implicit lazy val arbitraryWhoPayingExtraLtaChargePage: Arbitrary[WhoPayingExtraLtaChargePage.type] =
    Arbitrary(WhoPayingExtraLtaChargePage)

  implicit lazy val arbitraryProtectionReferencePage: Arbitrary[ProtectionReferencePage.type] =
    Arbitrary(ProtectionReferencePage)

  implicit lazy val arbitraryProtectionTypePage: Arbitrary[ProtectionTypePage.type] =
    Arbitrary(ProtectionTypePage)

  implicit lazy val arbitraryLtaProtectionOrEnhancementsPage: Arbitrary[LtaProtectionOrEnhancementsPage.type] =
    Arbitrary(LtaProtectionOrEnhancementsPage)

  implicit lazy val arbitraryWhatNewProtectionTypeEnhancementPage
    : Arbitrary[WhatNewProtectionTypeEnhancementPage.type] =
    Arbitrary(WhatNewProtectionTypeEnhancementPage)

  implicit lazy val arbitraryReferenceNewProtectionTypeEnhancementPage
    : Arbitrary[ReferenceNewProtectionTypeEnhancementPage.type] =
    Arbitrary(ReferenceNewProtectionTypeEnhancementPage)

  implicit lazy val arbitraryProtectionEnhancedChangedPage: Arbitrary[ProtectionEnhancedChangedPage.type] =
    Arbitrary(ProtectionEnhancedChangedPage)

  implicit lazy val arbitraryExcessLifetimeAllowancePaidPage: Arbitrary[ExcessLifetimeAllowancePaidPage.type] =
    Arbitrary(ExcessLifetimeAllowancePaidPage)

  implicit lazy val arbitraryLifetimeAllowanceChargePage: Arbitrary[LifetimeAllowanceChargePage.type] =
    Arbitrary(LifetimeAllowanceChargePage)

  implicit lazy val arbitraryDateOfBenefitCrystallisationEventPage
    : Arbitrary[DateOfBenefitCrystallisationEventPage.type] =
    Arbitrary(DateOfBenefitCrystallisationEventPage)

  implicit lazy val arbitraryHadBenefitCrystallisationEventPage: Arbitrary[HadBenefitCrystallisationEventPage.type] =
    Arbitrary(HadBenefitCrystallisationEventPage)
  implicit lazy val arbitraryReportingChangePage: Arbitrary[ReportingChangePage.type]                               =
    Arbitrary(ReportingChangePage)

  implicit lazy val arbitraryWhichYearsScottishTaxpayerPage: Arbitrary[WhichYearsScottishTaxpayerPage.type] =
    Arbitrary(WhichYearsScottishTaxpayerPage)

  implicit lazy val arbitraryScottishTaxpayerFrom2016Page: Arbitrary[ScottishTaxpayerFrom2016Page.type] =
    Arbitrary(ScottishTaxpayerFrom2016Page)

  implicit lazy val arbitraryChangeInTaxChargePage: Arbitrary[ChangeInTaxChargePage.type] =
    Arbitrary(ChangeInTaxChargePage)

}
