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

package models.tasklist

import base.SpecBase
import models.NewExcessLifetimeAllowancePaid.{Annualpayment, Both, Lumpsum}
import models.{ChangeInTaxCharge, WhoPayingExtraLtaCharge}
import models.tasklist.sections.LTASection
import pages.lifetimeallowance.{ChangeInLifetimeAllowancePage, ChangeInTaxChargePage, HadBenefitCrystallisationEventPage, LifetimeAllowanceChargePage, LtaPensionSchemeDetailsPage, LumpSumValuePage, NewAnnualPaymentValuePage, NewExcessLifetimeAllowancePaidPage, NewLumpSumValuePage, WhoPayingExtraLtaChargePage}

class LTASectionSpec extends SpecBase {

  "When user has not answered first page" in {
    val userAnswers = emptyUserAnswers

    val status = LTASection.status(userAnswers)

    status mustBe (SectionStatus.NotStarted)
  }

  "LTA Kickouts" - {

    "When user has not had a BCE" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, false)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has had their BCE but not been informed of BCE change" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(ChangeInLifetimeAllowancePage, false)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has had their BCE and informed of BCE change but no change to charge" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(ChangeInLifetimeAllowancePage, true)
        .get
        .set(ChangeInTaxChargePage, ChangeInTaxCharge.None)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has reached noPreviousChargeKickout from NewLumpSumpage" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(NewExcessLifetimeAllowancePaidPage, Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt(0))
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has reached noPreviousChargeKickout from NewAnnualPaymentValuePage" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(NewExcessLifetimeAllowancePaidPage, Annualpayment)
        .get
        .set(NewAnnualPaymentValuePage, BigInt(0))
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has reached noPreviousChargeKickout after answering both" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(NewExcessLifetimeAllowancePaidPage, Both)
        .get
        .set(NewAnnualPaymentValuePage, BigInt(0))
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has completed the journey with no value increase but has previous charge" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt(500))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, Both)
        .get
        .set(NewLumpSumValuePage, BigInt(300))
        .get
        .set(NewAnnualPaymentValuePage, BigInt(100))
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "When user has had a BCE but not answered all eligibility questions" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "When user has had a BCE and informed of BCE change but not answered all eligibility questions" in {
      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(ChangeInLifetimeAllowancePage, true)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "When user is eligible for LTA service but not completed entire LTA section" in {

      val userAnswers = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .get
        .set(ChangeInLifetimeAllowancePage, true)
        .get
        .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
        .get

      val status = LTASection.status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }
  }

  "When user answers You to who is paying extra LTA charge" in {
    val userAnswers = emptyUserAnswers
      .set(HadBenefitCrystallisationEventPage, true)
      .get
      .set(ChangeInLifetimeAllowancePage, true)
      .get
      .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
      .get
      .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.You)
      .get

    val status = LTASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }

  "When user answers Scheme to who is paying extra LTA charge but has not completed pension scheme details" in {
    val userAnswers = emptyUserAnswers
      .set(HadBenefitCrystallisationEventPage, true)
      .get
      .set(ChangeInLifetimeAllowancePage, true)
      .get
      .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
      .get
      .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.PensionScheme)
      .get

    val status = LTASection.status(userAnswers)

    status mustBe (SectionStatus.InProgress)
  }

  "When user answers Scheme to who is paying extra LTA charge and has completed pension scheme details" in {
    val userAnswers = emptyUserAnswers
      .set(HadBenefitCrystallisationEventPage, true)
      .get
      .set(ChangeInLifetimeAllowancePage, true)
      .get
      .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
      .get
      .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.PensionScheme)
      .get
      .set(LtaPensionSchemeDetailsPage, models.LtaPensionSchemeDetails("Scheme1", "00348916RT"))
      .get

    val status = LTASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }

}
