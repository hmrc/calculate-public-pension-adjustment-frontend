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

package pages.setupquestions

import models.Period.{_2013, _2014, _2015, _2021, _2022}
import models.{AAKickOutStatus, CheckMode, ContributedToDuringRemedyPeriod, EnhancementType, ExcessLifetimeAllowancePaid, LTAKickOutStatus, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, MaybePIAIncrease, NewEnhancementType, NewExcessLifetimeAllowancePaid, NormalMode, PensionSchemeDetails, PensionSchemeInputAmounts, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, ReportingChange, SchemeIndex, SchemeNameAndTaxRef, ThresholdIncome, WhatNewProtectionTypeEnhancement, WhichYearsScottishTaxpayer, WhoPaidAACharge, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, PIAPreRemedyPage, PayTaxCharge1415Page, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page, StopPayingPublicPensionPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear.{AddAnotherSchemePage, AdjustedIncomePage, AmountOfGiftAidPage, BlindAllowancePage, BlindPersonsAllowanceAmountPage, ClaimingTaxReliefPensionPage, ContributedToDuringRemedyPeriodPage, DefinedBenefitAmountPage, DefinedContributionAmountPage, DidYouContributeToRASSchemePage, DoYouHaveGiftAidPage, DoYouKnowPersonalAllowancePage, FlexiAccessDefinedContributionAmountPage, HowMuchAAChargeSchemePaidPage, HowMuchAAChargeYouPaidPage, KnowAdjustedAmountPage, MemberMoreThanOnePensionPage, OtherDefinedBenefitOrContributionPage, PayAChargePage, PensionSchemeDetailsPage, PensionSchemeInputAmountsPage, PersonalAllowancePage, RASContributionAmountPage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage, WhichSchemePage, WhoPaidAAChargePage}
import pages.behaviours.PageBehaviours
import pages.lifetimeallowance.{AnnualPaymentValuePage, DateOfBenefitCrystallisationEventPage, EnhancementTypePage, ExcessLifetimeAllowancePaidPage, InternationalEnhancementReferencePage, LifetimeAllowanceChargePage, LtaPensionSchemeDetailsPage, LtaProtectionOrEnhancementsPage, LumpSumValuePage, NewAnnualPaymentValuePage, NewEnhancementTypePage, NewExcessLifetimeAllowancePaidPage, NewInternationalEnhancementReferencePage, NewLumpSumValuePage, NewPensionCreditReferencePage, PensionCreditReferencePage, ProtectionEnhancedChangedPage, ProtectionReferencePage, ProtectionTypePage, QuarterChargePaidPage, ReferenceNewProtectionTypeEnhancementPage, SchemeNameAndTaxRefPage, UserSchemeDetailsPage, WhatNewProtectionTypeEnhancementPage, WhoPaidLTAChargePage, WhoPayingExtraLtaChargePage, YearChargePaidPage}
import pages.setupquestions.annualallowance.{Contribution4000ToDirectContributionSchemePage, ContributionRefundsPage, FlexibleAccessDcSchemePage, HadAAChargePage, MaybePIAIncreasePage, MaybePIAUnchangedOrDecreasedPage, NetIncomeAbove100KPage, NetIncomeAbove190KIn2023Page, NetIncomeAbove190KPage, PIAAboveAnnualAllowanceIn2023Page, PensionProtectedMemberPage, SavingsStatementPage}
import pages.setupquestions.lifetimeallowance.{ChangeInLifetimeAllowancePage, HadBenefitCrystallisationEventPage, IncreaseInLTAChargePage, MultipleBenefitCrystallisationEventPage, NewLTAChargePage, OtherSchemeNotificationPage, PreviousLTAChargePage}

import java.time.LocalDate

class ReportingChangePageSpec extends PageBehaviours {

  "ReportingChangePage" - {

    beRetrievable[Set[ReportingChange]](ReportingChangePage)

    beSettable[Set[ReportingChange]](ReportingChangePage)

    beRemovable[Set[ReportingChange]](ReportingChangePage)
  }

  "Normal mode" - {

    "must redirect to RPSS page when user submits an answer for Annual allowance" in {

      val ua                  = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.AnnualAllowance))
        .success
        .value
      val nextPageUrl: String = ReportingChangePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey/received-letter")
    }

    "must redirect to BCE page when user submits an answer for Lifetime allowance" in {

      val ua = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.LifetimeAllowance))
        .success
        .value

      val nextPageUrl: String = ReportingChangePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
    }

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ReportingChangePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must redirect to RPSS page when user submits an answer for Annual allowance" in {

      val ua = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.AnnualAllowance))
        .success
        .value

      val nextPageUrl: String = ReportingChangePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey/received-letter")
    }

    "must redirect to BCE page when user submits an answer for Lifetime allowance" in {

      val ua = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.LifetimeAllowance))
        .success
        .value

      val nextPageUrl: String = ReportingChangePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
    }

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ReportingChangePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user selects AA only, clean up LTA answers" in {

      val ua = testCalulationServiceData

      val cleanedUserAnswers = ReportingChangePage.cleanup(Some(Set(AnnualAllowance)), ua).success.value

      // AA Triage Answers
      cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe Some(false)

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)
      cleanedUserAnswers.get(WhichYearsScottishTaxpayerPage) mustBe Some(Set(WhichYearsScottishTaxpayer._2017))
      cleanedUserAnswers.get(PayingPublicPensionSchemePage) mustBe Some(false)
      cleanedUserAnswers.get(StopPayingPublicPensionPage) mustBe Some(LocalDate.of(2021, 1, 1))
      cleanedUserAnswers.get(DefinedContributionPensionSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(PayTaxCharge1415Page) mustBe Some(false)
      cleanedUserAnswers.get(PIAPreRemedyPage(_2013)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(PIAPreRemedyPage(_2014)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(PIAPreRemedyPage(_2015)) mustBe Some(BigInt(123))

      // Kickout status
      cleanedUserAnswers.get(AAKickOutStatus()) mustBe None
      cleanedUserAnswers.get(LTAKickOutStatus()) mustBe None

      // LTA Triage Answers
      cleanedUserAnswers.get(HadBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(PreviousLTAChargePage) mustBe None
      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe None
      cleanedUserAnswers.get(IncreaseInLTAChargePage) mustBe None
      cleanedUserAnswers.get(NewLTAChargePage) mustBe None
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe None

      // LTA Answers
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(LtaProtectionOrEnhancementsPage) mustBe None
      cleanedUserAnswers.get(ProtectionTypePage) mustBe None
      cleanedUserAnswers.get(ProtectionReferencePage) mustBe None
      cleanedUserAnswers.get(EnhancementTypePage) mustBe None
      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe None
      cleanedUserAnswers.get(ProtectionEnhancedChangedPage) mustBe None
      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe None
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe None
      cleanedUserAnswers.get(LifetimeAllowanceChargePage) mustBe None
      cleanedUserAnswers.get(ExcessLifetimeAllowancePaidPage) mustBe None
      cleanedUserAnswers.get(LumpSumValuePage) mustBe None
      cleanedUserAnswers.get(AnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(WhoPaidLTAChargePage) mustBe None
      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe None
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe None
      cleanedUserAnswers.get(YearChargePaidPage) mustBe None
      cleanedUserAnswers.get(NewExcessLifetimeAllowancePaidPage) mustBe None
      cleanedUserAnswers.get(NewLumpSumValuePage) mustBe None
      cleanedUserAnswers.get(NewAnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe None
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2021, SchemeIndex(0))) mustBe Some(
        PensionSchemeDetails("schemeName", "schemeRef")
      )
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(AddAnotherSchemePage(_2021, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhichSchemePage(_2021, SchemeIndex(1))) mustBe Some("schemeName")
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(1))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(1))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(OtherDefinedBenefitOrContributionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(ContributedToDuringRemedyPeriodPage(_2021)) mustBe Some(
        Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
      )
      cleanedUserAnswers.get(DefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(DefinedBenefitAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ThresholdIncomePage(_2021)) mustBe Some(ThresholdIncome.Yes)
      cleanedUserAnswers.get(TotalIncomePage(_2021)) mustBe Some(BigInt(220000))
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(TaxReliefPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(RASContributionAmountPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(KnowAdjustedAmountPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(AdjustedIncomePage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(PersonalAllowancePage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(BlindAllowancePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2021)) mustBe Some(BigInt(1))

      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2022)) mustBe Some(false)
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2022, SchemeIndex(0))) mustBe Some(
        PensionSchemeDetails("schemeName", "schemeRef")
      )
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2022, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2022, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ThresholdIncomePage(_2022)) mustBe Some(ThresholdIncome.No)
      cleanedUserAnswers.get(TotalIncomePage(_2022)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(TaxReliefPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(RASContributionAmountPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(PersonalAllowancePage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(BlindAllowancePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2022)) mustBe Some(BigInt(1))

    }

    "when user selects LTA only, clean up AA answers and AA setup questions" in {

      val ua = testCalulationServiceData

      val cleanedUserAnswers =
        ReportingChangePage.cleanup(Some(Set(ReportingChange.LifetimeAllowance)), ua).success.value

      // AA Triage Answers
      cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe Some(false)

      // LTA Triage Answers
      cleanedUserAnswers.get(HadBenefitCrystallisationEventPage) mustBe Some(true)
      cleanedUserAnswers.get(PreviousLTAChargePage) mustBe Some(false)
      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe Some(true)
      cleanedUserAnswers.get(IncreaseInLTAChargePage) mustBe Some(true)
      cleanedUserAnswers.get(NewLTAChargePage) mustBe Some(false)
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe Some(true)
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe Some(true)

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None
      cleanedUserAnswers.get(WhichYearsScottishTaxpayerPage) mustBe None
      cleanedUserAnswers.get(PayingPublicPensionSchemePage) mustBe None
      cleanedUserAnswers.get(StopPayingPublicPensionPage) mustBe None
      cleanedUserAnswers.get(DefinedContributionPensionSchemePage) mustBe None
      cleanedUserAnswers.get(PayTaxCharge1415Page) mustBe None
      cleanedUserAnswers.get(PIAPreRemedyPage(_2013)) mustBe None
      cleanedUserAnswers.get(PIAPreRemedyPage(_2014)) mustBe None
      cleanedUserAnswers.get(PIAPreRemedyPage(_2015)) mustBe None

      // Kickout status
      cleanedUserAnswers.get(AAKickOutStatus()) mustBe None
      cleanedUserAnswers.get(LTAKickOutStatus()) mustBe None

      // LTA Answers
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe Some(LocalDate.of(2021, 1, 1))
      cleanedUserAnswers.get(LtaProtectionOrEnhancementsPage) mustBe Some(LtaProtectionOrEnhancements.Both)
      cleanedUserAnswers.get(ProtectionTypePage) mustBe Some(ProtectionType.EnhancedProtection)
      cleanedUserAnswers.get(ProtectionReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(EnhancementTypePage) mustBe Some(EnhancementType.InternationalEnhancement)
      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(ProtectionEnhancedChangedPage) mustBe Some(ProtectionEnhancedChanged.Both)
      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe Some(
        WhatNewProtectionTypeEnhancement.EnhancedProtection
      )
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe Some("123")
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe Some(NewEnhancementType.Both)
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(LifetimeAllowanceChargePage) mustBe Some(true)
      cleanedUserAnswers.get(ExcessLifetimeAllowancePaidPage) mustBe Some(ExcessLifetimeAllowancePaid.Both)
      cleanedUserAnswers.get(LumpSumValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(AnnualPaymentValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(WhoPaidLTAChargePage) mustBe Some(WhoPaidLTACharge.PensionScheme)
      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe Some(SchemeNameAndTaxRef("schemename", "taxref"))
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe Some(QuarterChargePaid.AprToJul)
      cleanedUserAnswers.get(YearChargePaidPage) mustBe Some(YearChargePaid._2020To2021)
      cleanedUserAnswers.get(NewExcessLifetimeAllowancePaidPage) mustBe Some(NewExcessLifetimeAllowancePaid.Both)
      cleanedUserAnswers.get(NewLumpSumValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(NewAnnualPaymentValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe Some(WhoPayingExtraLtaCharge.PensionScheme)
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe Some(LtaPensionSchemeDetails("schemename", "taxref"))

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(AddAnotherSchemePage(_2021, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(WhichSchemePage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1))) mustBe None
      cleanedUserAnswers.get(OtherDefinedBenefitOrContributionPage(_2021)) mustBe None
      cleanedUserAnswers.get(ContributedToDuringRemedyPeriodPage(_2021)) mustBe None
      cleanedUserAnswers.get(DefinedContributionAmountPage(_2021)) mustBe None
      cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(_2021)) mustBe None
      cleanedUserAnswers.get(DefinedBenefitAmountPage(_2021)) mustBe None
      cleanedUserAnswers.get(ThresholdIncomePage(_2021)) mustBe None
      cleanedUserAnswers.get(TotalIncomePage(_2021)) mustBe None
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe None
      cleanedUserAnswers.get(TaxReliefPage(_2021)) mustBe None
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2021)) mustBe None
      cleanedUserAnswers.get(RASContributionAmountPage(_2021)) mustBe None
      cleanedUserAnswers.get(KnowAdjustedAmountPage(_2021)) mustBe None
      cleanedUserAnswers.get(AdjustedIncomePage(_2021)) mustBe None
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2021)) mustBe None
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2021)) mustBe None
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2021)) mustBe None
      cleanedUserAnswers.get(PersonalAllowancePage(_2021)) mustBe None
      cleanedUserAnswers.get(BlindAllowancePage(_2021)) mustBe None
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2021)) mustBe None

      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2022)) mustBe None
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(PayAChargePage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0))) mustBe None
      cleanedUserAnswers.get(ThresholdIncomePage(_2022)) mustBe None
      cleanedUserAnswers.get(TotalIncomePage(_2022)) mustBe None
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe None
      cleanedUserAnswers.get(TaxReliefPage(_2022)) mustBe None
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2022)) mustBe None
      cleanedUserAnswers.get(RASContributionAmountPage(_2022)) mustBe None
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2022)) mustBe None
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2022)) mustBe None
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2022)) mustBe None
      cleanedUserAnswers.get(PersonalAllowancePage(_2022)) mustBe None
      cleanedUserAnswers.get(BlindAllowancePage(_2022)) mustBe None
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2022)) mustBe None
    }

    "When user selects both, must clean up appropriately" in {

      val ua = testCalulationServiceData

      val cleanedUserAnswers =
        ReportingChangePage.cleanup(Some(Set(AnnualAllowance, LifetimeAllowance)), ua).success.value

      // AA Triage Answers
      cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe Some(false)

      // LTA Triage Answers
      cleanedUserAnswers.get(HadBenefitCrystallisationEventPage) mustBe Some(true)
      cleanedUserAnswers.get(PreviousLTAChargePage) mustBe Some(false)
      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe Some(true)
      cleanedUserAnswers.get(IncreaseInLTAChargePage) mustBe Some(true)
      cleanedUserAnswers.get(NewLTAChargePage) mustBe Some(false)
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe Some(true)
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe Some(true)

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)
      cleanedUserAnswers.get(WhichYearsScottishTaxpayerPage) mustBe Some(Set(WhichYearsScottishTaxpayer._2017))
      cleanedUserAnswers.get(PayingPublicPensionSchemePage) mustBe Some(false)
      cleanedUserAnswers.get(StopPayingPublicPensionPage) mustBe Some(LocalDate.of(2021, 1, 1))
      cleanedUserAnswers.get(DefinedContributionPensionSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(PayTaxCharge1415Page) mustBe Some(false)
      cleanedUserAnswers.get(PIAPreRemedyPage(_2013)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(PIAPreRemedyPage(_2014)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(PIAPreRemedyPage(_2015)) mustBe Some(BigInt(123))

      // Kickout status
      cleanedUserAnswers.get(AAKickOutStatus()) mustBe Some(1)
      cleanedUserAnswers.get(LTAKickOutStatus()) mustBe Some(1)

      // LTA Answers
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe Some(LocalDate.of(2021, 1, 1))
      cleanedUserAnswers.get(LtaProtectionOrEnhancementsPage) mustBe Some(LtaProtectionOrEnhancements.Both)
      cleanedUserAnswers.get(ProtectionTypePage) mustBe Some(ProtectionType.EnhancedProtection)
      cleanedUserAnswers.get(ProtectionReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(EnhancementTypePage) mustBe Some(EnhancementType.InternationalEnhancement)
      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(ProtectionEnhancedChangedPage) mustBe Some(ProtectionEnhancedChanged.Both)
      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe Some(
        WhatNewProtectionTypeEnhancement.EnhancedProtection
      )
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe Some("123")
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe Some(NewEnhancementType.Both)
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(LifetimeAllowanceChargePage) mustBe Some(true)
      cleanedUserAnswers.get(ExcessLifetimeAllowancePaidPage) mustBe Some(ExcessLifetimeAllowancePaid.Both)
      cleanedUserAnswers.get(LumpSumValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(AnnualPaymentValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(WhoPaidLTAChargePage) mustBe Some(WhoPaidLTACharge.PensionScheme)
      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe Some(SchemeNameAndTaxRef("schemename", "taxref"))
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe Some(QuarterChargePaid.AprToJul)
      cleanedUserAnswers.get(YearChargePaidPage) mustBe Some(YearChargePaid._2020To2021)
      cleanedUserAnswers.get(NewExcessLifetimeAllowancePaidPage) mustBe Some(NewExcessLifetimeAllowancePaid.Both)
      cleanedUserAnswers.get(NewLumpSumValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(NewAnnualPaymentValuePage) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe Some(WhoPayingExtraLtaCharge.PensionScheme)
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe Some(LtaPensionSchemeDetails("schemename", "taxref"))

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2021, SchemeIndex(0))) mustBe Some(
        PensionSchemeDetails("schemeName", "schemeRef")
      )
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(AddAnotherSchemePage(_2021, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhichSchemePage(_2021, SchemeIndex(1))) mustBe Some("schemeName")
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(1))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(1))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(OtherDefinedBenefitOrContributionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(ContributedToDuringRemedyPeriodPage(_2021)) mustBe Some(
        Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
      )
      cleanedUserAnswers.get(DefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(DefinedBenefitAmountPage(_2021)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ThresholdIncomePage(_2021)) mustBe Some(ThresholdIncome.Yes)
      cleanedUserAnswers.get(TotalIncomePage(_2021)) mustBe Some(BigInt(220000))
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(TaxReliefPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(RASContributionAmountPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(KnowAdjustedAmountPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(AdjustedIncomePage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(PersonalAllowancePage(_2021)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(BlindAllowancePage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2021)) mustBe Some(BigInt(1))

      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2022)) mustBe Some(false)
      cleanedUserAnswers.get(PensionSchemeDetailsPage(_2022, SchemeIndex(0))) mustBe Some(
        PensionSchemeDetails("schemeName", "schemeRef")
      )
      cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0))) mustBe Some(
        PensionSchemeInputAmounts(BigInt(123))
      )
      cleanedUserAnswers.get(PayAChargePage(_2022, SchemeIndex(0))) mustBe Some(true)
      cleanedUserAnswers.get(WhoPaidAAChargePage(_2022, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
      cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ThresholdIncomePage(_2022)) mustBe Some(ThresholdIncome.No)
      cleanedUserAnswers.get(TotalIncomePage(_2022)) mustBe Some(BigInt(123))
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
      cleanedUserAnswers.get(TaxReliefPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(RASContributionAmountPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(AmountOfGiftAidPage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(PersonalAllowancePage(_2022)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(BlindAllowancePage(_2022)) mustBe Some(true)
      cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2022)) mustBe Some(BigInt(1))
    }
  }
}
