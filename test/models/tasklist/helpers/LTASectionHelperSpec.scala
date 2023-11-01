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

import base.SpecBase
import models.NewExcessLifetimeAllowancePaid._
import models.tasklist.SectionStatus
import models.tasklist.helpers.LTASectionHelper
import models.{ChangeInTaxCharge, WhoPayingExtraLtaCharge}
import pages.lifetimeallowance._

class LTASectionHelperSpec extends SpecBase {

  "LTASectionHelper" - {

    ".anyLtaKickoutReached" - {

      "should return false when all conditions are met to not have entered a kickout" in {
        val userAnswers = emptyUserAnswers
          .set(HadBenefitCrystallisationEventPage, true)
          .get
          .set(ChangeInLifetimeAllowancePage, true)
          .get
          .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
          .get
          .set(LifetimeAllowanceChargePage, false)
          .get
          .set(NewExcessLifetimeAllowancePaidPage, Annualpayment)
          .get
          .set(NewAnnualPaymentValuePage, BigInt(1))
          .get

        val result = LTASectionHelper.anyLtaKickoutReached(userAnswers)
        result mustBe false
      }

      "should return true if statusOfHadBCE is false" in {
        val userAnswers = emptyUserAnswers
          .set(HadBenefitCrystallisationEventPage, false)
          .get

        val result = LTASectionHelper.anyLtaKickoutReached(userAnswers)
        result mustBe true
      }

      "should return true if statusOfInformedBCEChange is false" in {
        val userAnswers = emptyUserAnswers
          .set(HadBenefitCrystallisationEventPage, true)
          .get
          .set(ChangeInLifetimeAllowancePage, false)
          .get

        val result = LTASectionHelper.anyLtaKickoutReached(userAnswers)
        result mustBe true
      }

      "should return true if statusOfChangeInTaxCharge is false" in {
        val userAnswers = emptyUserAnswers
          .set(HadBenefitCrystallisationEventPage, true)
          .get
          .set(ChangeInLifetimeAllowancePage, true)
          .get
          .set(ChangeInTaxChargePage, ChangeInTaxCharge.None)
          .get

        val result = LTASectionHelper.anyLtaKickoutReached(userAnswers)
        result mustBe true
      }

      "should return true if noPreviousChargeKickoutReached is true" in {
        val userAnswers = emptyUserAnswers
          .set(HadBenefitCrystallisationEventPage, true)
          .get
          .set(ChangeInLifetimeAllowancePage, true)
          .get
          .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
          .get
          .set(LifetimeAllowanceChargePage, false)
          .get
          .set(NewExcessLifetimeAllowancePaidPage, Annualpayment)
          .get
          .set(NewAnnualPaymentValuePage, BigInt(0))
          .get

        val result = LTASectionHelper.anyLtaKickoutReached(userAnswers)
        result mustBe true
      }
    }

    ".isLastPageAnswered" - {

      "return true when WhoPayingExtraLtaChargePage is You" in {
        val userAnswers = emptyUserAnswers
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.You)
          .get

        LTASectionHelper.isLastPageAnswered(userAnswers) mustBe true
      }

      "return true when WhoPayingExtraLtaChargePage is PensionScheme and LtaPensionSchemeDetailsPage is set" in {
        val userAnswers = emptyUserAnswers
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.PensionScheme)
          .get
          .set(LtaPensionSchemeDetailsPage, models.LtaPensionSchemeDetails("Scheme1", "00348916RT"))
          .get

        LTASectionHelper.isLastPageAnswered(userAnswers) mustBe true
      }

      "return false when WhoPayingExtraLtaChargePage is PensionScheme and LtaPensionSchemeDetailsPage is not set" in {
        val userAnswers = emptyUserAnswers
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.PensionScheme)
          .get

        LTASectionHelper.isLastPageAnswered(userAnswers) mustBe false
      }

      "should return true if WhoPayingExtraLtaChargePage is none there is no value increase and previous charge is answered with true" in {
        val userAnswers = emptyUserAnswers
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

        val result = LTASectionHelper.isLastPageAnswered(userAnswers)
        result mustBe true
      }

      "should return false if WhoPayingExtraLtaChargePage is none there is no value increase and previous charge is answered with false" in {
        val userAnswers = emptyUserAnswers
          .set(LifetimeAllowanceChargePage, false)
          .get
          .set(LumpSumValuePage, BigInt(500))
          .get
          .set(NewExcessLifetimeAllowancePaidPage, Both)
          .get
          .set(NewLumpSumValuePage, BigInt(300))
          .get
          .set(NewAnnualPaymentValuePage, BigInt(100))
          .get

        val result = LTASectionHelper.isLastPageAnswered(userAnswers)
        result mustBe false
      }
    }

    ".isLTAEligible" - {

      "when all conditions are true" - {
        "return SectionStatus.InProgress" in {
          val answers = emptyUserAnswers
            .set(HadBenefitCrystallisationEventPage, true)
            .get
            .set(ChangeInLifetimeAllowancePage, true)
            .get
            .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
            .get
            .set(LifetimeAllowanceChargePage, false)
            .get
            .set(NewExcessLifetimeAllowancePaidPage, Both)
            .get
            .set(NewAnnualPaymentValuePage, BigInt(10))
            .get
            .set(NewLumpSumValuePage, BigInt(10))
            .get

          LTASectionHelper.isLTAEligible(answers) mustBe SectionStatus.InProgress
        }
      }

      "when any condition is false" - {
        "return SectionStatus.Completed" in {
          val answers = emptyUserAnswers
            .set(HadBenefitCrystallisationEventPage, false)
            .get
            .set(ChangeInLifetimeAllowancePage, true)
            .get
            .set(ChangeInTaxChargePage, ChangeInTaxCharge.IncreasedCharge)
            .get

          LTASectionHelper.isLTAEligible(answers) mustBe SectionStatus.Completed
        }
      }
    }
  }
}
