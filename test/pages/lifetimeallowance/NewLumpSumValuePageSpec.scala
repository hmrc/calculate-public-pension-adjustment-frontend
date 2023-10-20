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

package pages.lifetimeallowance

import models.{CheckMode, NormalMode, WhoPayingExtraLtaCharge}
import pages.behaviours.PageBehaviours

class NewLumpSumValuePageSpec extends PageBehaviours {

  "NewLumpSumValuePage" - {

    beRetrievable[BigInt](NewLumpSumValuePage)

    beSettable[BigInt](NewLumpSumValuePage)

    beRemovable[BigInt](NewLumpSumValuePage)
  }

  "normal mode navigation" - {

    "when user has selected Lump Sum for how excess was paid and there is no old lump sum value" in {

      val userAnswers = emptyUserAnswers
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("1"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("1"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("1"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("20"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("20"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Both for how excess was paid " in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-value-of-annual-payment")
    }

    "when user has entered correct value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Lump Sum for how excess was paid and there is no old lump sum value" in {

      val userAnswers = emptyUserAnswers
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("1"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("1"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("1"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("20"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 old values and 1 new value and an decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Lump Sum for how excess was paid and there is 2 new values and 1 old value and an decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get
        .set(NewLumpSumValuePage, BigInt("20"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Both for how excess was paid in check mode " in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("200"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-value-of-annual-payment")
    }

    "when user has entered correct value in Check Mode" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when there is an increase" in {
      val ua = emptyUserAnswers
        .set(
          LumpSumValuePage,
          BigInt(1)
        )
        .success
        .value
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Lumpsum
        )
        .success
        .value
        .set(
          WhoPayingExtraLtaChargePage,
          models.WhoPayingExtraLtaCharge.PensionScheme
        )
        .success
        .value
        .set(
          LtaPensionSchemeDetailsPage,
          models.LtaPensionSchemeDetails("details3", "ref3")
        )
        .success
        .value

      val cleanedUserAnswers = NewLumpSumValuePage.cleanup(Some(BigInt(100)), ua).success.value

      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe Some(models.WhoPayingExtraLtaCharge.PensionScheme)
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe Some(
        models.LtaPensionSchemeDetails("details3", "ref3")
      )
    }

    "must cleanup correctly when there is an decrease" in {
      val ua = emptyUserAnswers
        .set(
          LumpSumValuePage,
          BigInt(100)
        )
        .success
        .value
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Lumpsum
        )
        .success
        .value
        .set(
          WhoPayingExtraLtaChargePage,
          models.WhoPayingExtraLtaCharge.PensionScheme
        )
        .success
        .value
        .set(
          LtaPensionSchemeDetailsPage,
          models.LtaPensionSchemeDetails("details3", "ref3")
        )
        .success
        .value

      val cleanedUserAnswers = NewLumpSumValuePage.cleanup(Some(BigInt(1)), ua).success.value

      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe None
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None
    }
  }

}
