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

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class NewAnnualPaymentValuePageSpec extends PageBehaviours {

  "NewAnnualPaymentValuePage" - {

    beRetrievable[BigInt](NewAnnualPaymentValuePage)

    beSettable[BigInt](NewAnnualPaymentValuePage)

    beRemovable[BigInt](NewAnnualPaymentValuePage)
  }

  "normal mode navigation" - {

    "when user has entered a new Annual Payment Value and there is a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(AnnualPaymentValuePage, BigInt("9000"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(AnnualPaymentValuePage, BigInt("9000"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered a new Annual Payment Value page and there is an increase" in {

      val userAnswers =
        emptyUserAnswers
          .set(AnnualPaymentValuePage, BigInt("1"))
          .get
          .set(NewAnnualPaymentValuePage, BigInt("100"))
          .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value page and there is no old Annual Payment value" in {

      val userAnswers = emptyUserAnswers.set(NewAnnualPaymentValuePage, BigInt("100")).get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("10"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("10"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user hasn't entered Annual payment value page" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has entered a new Annual Payment Value and there is a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(AnnualPaymentValuePage, BigInt("9000"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(AnnualPaymentValuePage, BigInt("9000"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("100"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered for Annual payment value page in Check mode and there is no old Annual Payment value" in {

      val userAnswers = emptyUserAnswers.set(NewAnnualPaymentValuePage, BigInt("200")).get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and an increase" in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("300"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("300"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-extra-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and a decrease and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("10"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/cannot-use-lta-service-no-charge")
    }

    "when user has entered a new Annual Payment Value and there is 2 old values and 1 new value and a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(AnnualPaymentValuePage, BigInt("100"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("50"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered a new Annual Payment Value and there is 2 new values and 1 old value and a decrease and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(NewLumpSumValuePage, BigInt("10"))
        .get
        .set(NewAnnualPaymentValuePage, BigInt("20"))
        .get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user hasn't entered Annual payment value page in Check mode " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when there is an increase" in {
      val ua = emptyUserAnswers
        .set(
          AnnualPaymentValuePage,
          BigInt(1)
        )
        .success
        .value
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Both
        )
        .success
        .value
        .set(
          NewLumpSumValuePage,
          BigInt(20)
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

      val cleanedUserAnswers = NewAnnualPaymentValuePage.cleanup(Some(BigInt(100)), ua).success.value

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
          NewLumpSumValuePage,
          BigInt(20)
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

      val cleanedUserAnswers = NewAnnualPaymentValuePage.cleanup(Some(BigInt(1)), ua).success.value

      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe None
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None
    }
  }
}
