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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.{AnySalarySacrificeArrangementsPage, FlexibleRemunerationArrangementsPage, PreviousClaimContinuePage}
import pages.annualallowance.preaaquestions.{RegisteredYearPage, ScottishTaxpayerFrom2016Page, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear._
import pages.lifetimeallowance._
import pages.setupquestions.ReportingChangePage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryFlexibleRemunerationArrangementsUserAnswersEntry
    : Arbitrary[(FlexibleRemunerationArrangementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FlexibleRemunerationArrangementsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAnySalarySacrificeArrangementsUserAnswersEntry
    : Arbitrary[(AnySalarySacrificeArrangementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AnySalarySacrificeArrangementsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAmountFlexibleRemunerationArrangementsUserAnswersEntry
    : Arbitrary[(AmountFlexibleRemunerationArrangementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AmountFlexibleRemunerationArrangementsPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchContributionPensionSchemeUserAnswersEntry
    : Arbitrary[(HowMuchContributionPensionSchemePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchContributionPensionSchemePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAmountSalarySacrificeArrangementsUserAnswersEntry
    : Arbitrary[(AmountSalarySacrificeArrangementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AmountSalarySacrificeArrangementsPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxReliefUserAnswersEntry: Arbitrary[(TaxReliefPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxReliefPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPersonalAllowanceUserAnswersEntry: Arbitrary[(PersonalAllowancePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PersonalAllowancePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPreviousClaimContinueUserAnswersEntry
    : Arbitrary[(PreviousClaimContinuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PreviousClaimContinuePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedContribution2016PreFlexiAmountUserAnswersEntry
    : Arbitrary[(DefinedContribution2016PreFlexiAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedContribution2016PreFlexiAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedContribution2016PostFlexiAmountUserAnswersEntry
    : Arbitrary[(DefinedContribution2016PostFlexiAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedContribution2016PostFlexiAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedBenefit2016PostAmountUserAnswersEntry
    : Arbitrary[(DefinedBenefit2016PostAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedBenefit2016PostAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedContribution2016PreAmountUserAnswersEntry
    : Arbitrary[(DefinedContribution2016PreAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedContribution2016PreAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedContribution2016PostAmountUserAnswersEntry
    : Arbitrary[(DefinedContribution2016PostAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedContribution2016PostAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedBenefit2016PreAmountUserAnswersEntry
    : Arbitrary[(DefinedBenefit2016PreAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedBenefit2016PreAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegisteredYearUserAnswersEntry: Arbitrary[(RegisteredYearPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegisteredYearPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUserSchemeDetailsUserAnswersEntry: Arbitrary[(UserSchemeDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UserSchemeDetailsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMultipleBenefitCrystallisationEventUserAnswersEntry
    : Arbitrary[(MultipleBenefitCrystallisationEventPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MultipleBenefitCrystallisationEventPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewExcessLifetimeAllowancePaidUserAnswersEntry
    : Arbitrary[(NewExcessLifetimeAllowancePaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NewExcessLifetimeAllowancePaidPage.type]
        value <- arbitrary[NewExcessLifetimeAllowancePaid].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewLumpSumValueUserAnswersEntry: Arbitrary[(NewLumpSumValuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NewLumpSumValuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewAnnualPaymentValueUserAnswersEntry
    : Arbitrary[(NewAnnualPaymentValuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NewAnnualPaymentValuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYearChargePaidUserAnswersEntry: Arbitrary[(YearChargePaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YearChargePaidPage.type]
        value <- arbitrary[YearChargePaid].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryQuarterChargePaidUserAnswersEntry: Arbitrary[(QuarterChargePaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[QuarterChargePaidPage.type]
        value <- arbitrary[QuarterChargePaid].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLumpSumValueUserAnswersEntry: Arbitrary[(LumpSumValuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LumpSumValuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAnnualPaymentValueUserAnswersEntry: Arbitrary[(AnnualPaymentValuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AnnualPaymentValuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  // scala fmt ignore
  implicit lazy val arbitraryPensionCreditReferenceUserAnswersEntry
    : Arbitrary[(PensionCreditReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionCreditReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewPensionCreditReferenceUserAnswersEntry
    : Arbitrary[(NewPensionCreditReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NewPensionCreditReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInternationalEnhancementReferenceUserAnswersEntry
    : Arbitrary[(InternationalEnhancementReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InternationalEnhancementReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewInternationalEnhancementReferenceUserAnswersEntry
    : Arbitrary[(NewInternationalEnhancementReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[NewInternationalEnhancementReferencePage.type]

        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEnhancementTypeUserAnswersEntry: Arbitrary[(EnhancementTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EnhancementTypePage.type]
        value <- arbitrary[EnhancementType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNewEnhancementTypeUserAnswersEntry: Arbitrary[(NewEnhancementTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NewEnhancementTypePage.type]
        value <- arbitrary[NewEnhancementType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFlexiAccessDefinedContributionAmountUserAnswersEntry
    : Arbitrary[(FlexiAccessDefinedContributionAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FlexiAccessDefinedContributionAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryThresholdIncomeUserAnswersEntry: Arbitrary[(ThresholdIncomePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ThresholdIncomePage.type]
        value <- arbitrary[ThresholdIncome].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOtherDefinedBenefitOrContributionUserAnswersEntry
    : Arbitrary[(OtherDefinedBenefitOrContributionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OtherDefinedBenefitOrContributionPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTotalIncomeUserAnswersEntry: Arbitrary[(TotalIncomePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TotalIncomePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdjustedIncomeUserAnswersEntry: Arbitrary[(AdjustedIncomePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdjustedIncomePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedBenefitAmountUserAnswersEntry: Arbitrary[(DefinedBenefitAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedBenefitAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDefinedContributionAmountUserAnswersEntry
    : Arbitrary[(DefinedContributionAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DefinedContributionAmountPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContributedToDuringRemedyPeriodUserAnswersEntry
    : Arbitrary[(ContributedToDuringRemedyPeriodPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContributedToDuringRemedyPeriodPage.type]
        value <- arbitrary[ContributedToDuringRemedyPeriod].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoPayingExtraLtaChargeUserAnswersEntry
    : Arbitrary[(WhoPayingExtraLtaChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoPayingExtraLtaChargePage.type]
        value <- arbitrary[WhoPayingExtraLtaCharge].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoPaidAAChargeUserAnswersEntry: Arbitrary[(WhoPaidAAChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoPaidAAChargePage.type]
        value <- arbitrary[WhoPaidAACharge].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchAAChargeYouPaidUserAnswersEntry
    : Arbitrary[(HowMuchAAChargeYouPaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchAAChargeYouPaidPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchAAChargeSchemePaidUserAnswersEntry
    : Arbitrary[(HowMuchAAChargeSchemePaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchAAChargeSchemePaidPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProtectionReferenceUserAnswersEntry: Arbitrary[(ProtectionReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProtectionTypeUserAnswersEntry: Arbitrary[(ProtectionTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionTypePage.type]
        value <- arbitrary[ProtectionType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLtaProtectionOrEnhancementsUserAnswersEntry
    : Arbitrary[(LtaProtectionOrEnhancementsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LtaProtectionOrEnhancementsPage.type]
        value <- arbitrary[LtaProtectionOrEnhancements].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatNewProtectionTypeEnhancementUserAnswersEntry
    : Arbitrary[(WhatNewProtectionTypeEnhancementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatNewProtectionTypeEnhancementPage.type]
        value <- arbitrary[WhatNewProtectionTypeEnhancement].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReferenceNewProtectionTypeEnhancementUserAnswersEntry
    : Arbitrary[(ReferenceNewProtectionTypeEnhancementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReferenceNewProtectionTypeEnhancementPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProtectionEnhancedChangedUserAnswersEntry
    : Arbitrary[(ProtectionEnhancedChangedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProtectionEnhancedChangedPage.type]
        value <- arbitrary[ProtectionEnhancedChanged].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySchemeNameAndTaxRefUserAnswersEntry: Arbitrary[(SchemeNameAndTaxRefPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SchemeNameAndTaxRefPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoPaidLTAChargeUserAnswersEntry: Arbitrary[(WhoPaidLTAChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoPaidLTAChargePage.type]
        value <- arbitrary[WhoPaidLTACharge].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryExcessLifetimeAllowancePaidUserAnswersEntry
    : Arbitrary[(ExcessLifetimeAllowancePaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ExcessLifetimeAllowancePaidPage.type]
        value <- arbitrary[ExcessLifetimeAllowancePaid].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLifetimeAllowanceChargeUserAnswersEntry
    : Arbitrary[(LifetimeAllowanceChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LifetimeAllowanceChargePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateOfBenefitCrystallisationEventUserAnswersEntry
    : Arbitrary[(DateOfBenefitCrystallisationEventPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateOfBenefitCrystallisationEventPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHadBenefitCrystallisationEventUserAnswersEntry
    : Arbitrary[(HadBenefitCrystallisationEventPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HadBenefitCrystallisationEventPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
  implicit lazy val arbitraryReportingChangeUserAnswersEntry: Arbitrary[(ReportingChangePage.type, JsValue)]   =
    Arbitrary {
      for {
        page  <- arbitrary[ReportingChangePage.type]
        value <- arbitrary[ReportingChange].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichYearsScottishTaxpayerUserAnswersEntry
    : Arbitrary[(WhichYearsScottishTaxpayerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichYearsScottishTaxpayerPage.type]
        value <- arbitrary[WhichYearsScottishTaxpayer].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryScottishTaxpayerFrom2016UserAnswersEntry
    : Arbitrary[(ScottishTaxpayerFrom2016Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ScottishTaxpayerFrom2016Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChangeInTaxChargeUserAnswersEntry: Arbitrary[(ChangeInTaxChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChangeInTaxChargePage.type]
        value <- arbitrary[ChangeInTaxCharge].map(Json.toJson(_))
      } yield (page, value)
    }
}
