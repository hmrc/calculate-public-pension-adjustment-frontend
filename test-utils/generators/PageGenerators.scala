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

package generators

import org.scalacheck.Arbitrary
import pages.annualallowance.preaaquestions.{ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.ReportingChangePage

trait PageGenerators {

  implicit lazy val arbitraryThresholdIncomePage: Arbitrary[ThresholdIncomePage.type] =
    Arbitrary(ThresholdIncomePage)

  implicit lazy val arbitraryOtherDefinedBenefitOrContributionPage
    : Arbitrary[OtherDefinedBenefitOrContributionPage.type] =
    Arbitrary(OtherDefinedBenefitOrContributionPage)

  implicit lazy val arbitraryTotalIncomePage: Arbitrary[TotalIncomePage.type] =
    Arbitrary(TotalIncomePage)

  implicit lazy val arbitraryAdjustedIncomePage: Arbitrary[AdjustedIncomePage.type] =
    Arbitrary(AdjustedIncomePage)

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

  implicit lazy val arbitraryValueNewLtaChargePage: Arbitrary[ValueNewLtaChargePage.type] =
    Arbitrary(ValueNewLtaChargePage)

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

  implicit lazy val arbitraryProtectionTypeEnhancementChangedPage
    : Arbitrary[ProtectionTypeEnhancementChangedPage.type] =
    Arbitrary(ProtectionTypeEnhancementChangedPage)

  implicit lazy val arbitraryLifetimeAllowanceChargeAmountPage: Arbitrary[LifetimeAllowanceChargeAmountPage.type] =
    Arbitrary(LifetimeAllowanceChargeAmountPage)

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
