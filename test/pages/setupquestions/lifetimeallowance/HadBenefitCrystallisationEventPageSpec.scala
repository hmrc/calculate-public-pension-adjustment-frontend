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

package pages.setupquestions.lifetimeallowance

import models.{ChangeInTaxCharge, CheckMode, EnhancementType, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, NewEnhancementType, NewExcessLifetimeAllowancePaid, NormalMode, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, SchemeNameAndTaxRef, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import pages.behaviours.PageBehaviours
import pages.setupquestions.lifetimeallowance.{ChangeInLifetimeAllowancePage, ChangeInTaxChargePage, MultipleBenefitCrystallisationEventPage}
import pages.lifetimeallowance._

import java.time.LocalDate

class HadBenefitCrystallisationEventPageSpec extends PageBehaviours {

  "HadBenefitCrystallisationEventPage" - {

    beRetrievable[Boolean](HadBenefitCrystallisationEventPage)

    beSettable[Boolean](HadBenefitCrystallisationEventPage)

    beRemovable[Boolean](HadBenefitCrystallisationEventPage)
  }

  "normal mode" - {

    "to previous LTA charge page when user answers yes" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/LTA-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, false)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "Check mode" - {

    "to LTA charge page when user answers true" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/LTA-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, false)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "cleanup" - {

    "must cleanup correctly when user answers false" in {

      val ua = testCalulationServiceData

      val cleanedUserAnswers = HadBenefitCrystallisationEventPage.cleanup(Some(false), ua).success.value

      cleanedUserAnswers.get(HadBenefitCrystallisationEventPage) mustBe Some(false)
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe None
      cleanedUserAnswers.get(ChangeInTaxChargePage) mustBe None
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe None
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
    }

    "must cleanup correctly when user answers true" in {

      val ua = testCalulationServiceData

      val cleanedUserAnswers = HadBenefitCrystallisationEventPage.cleanup(Some(true), ua).success.value

      cleanedUserAnswers.get(HadBenefitCrystallisationEventPage) mustBe Some(true)
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe Some(LocalDate.of(2021, 1, 1))
      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe Some(true)
      cleanedUserAnswers.get(ChangeInTaxChargePage) mustBe Some(ChangeInTaxCharge.IncreasedCharge)
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe Some(false)
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
    }
  }
}
