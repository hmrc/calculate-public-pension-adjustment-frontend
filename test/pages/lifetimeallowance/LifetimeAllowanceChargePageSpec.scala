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

package pages.lifetimeallowance

import models.{CheckMode, LtaPensionSchemeDetails, NormalMode}
import pages.behaviours.PageBehaviours

class LifetimeAllowanceChargePageSpec extends PageBehaviours {

  "LifetimeAllowanceChargePage" - {

    beRetrievable[Boolean](LifetimeAllowanceChargePage)

    beSettable[Boolean](LifetimeAllowanceChargePage)

    beRemovable[Boolean](LifetimeAllowanceChargePage)
  }

  "normal mode navigation" - {

    "when user selects true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/excess-paid")
    }

    "when user selects false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-excess-paid")
    }

    "must redirect to journey recovery when no answer" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user selects true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/excess-paid")
    }

    "when user selects false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-excess-paid")
    }

    "must redirect to journey recovery when no answer" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = LifetimeAllowanceChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when true" in {
      val ua = emptyUserAnswers
        .set(
          LumpSumValuePage,
          BigInt(1)
        )
        .success
        .value
        .set(
          AnnualPaymentValuePage,
          BigInt(10)
        )
        .success
        .value
        .set(
          UserSchemeDetailsPage,
          models.UserSchemeDetails("string", "ref")
        )
        .success
        .value
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.You
        )
        .success
        .value
        .set(
          SchemeNameAndTaxRefPage,
          models.SchemeNameAndTaxRef("string", "ref")
        )
        .success
        .value
        .set(
          YearChargePaidPage,
          models.YearChargePaid._2017To2018
        )
        .success
        .value
        .set(
          QuarterChargePaidPage,
          models.QuarterChargePaid.JanToApr
        )
        .success
        .value
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Annualpayment
        )
        .success
        .value
        .set(
          NewLumpSumValuePage,
          BigInt(11)
        )
        .success
        .value
        .set(
          NewAnnualPaymentValuePage,
          BigInt(12)
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
          LtaPensionSchemeDetails("string1", "ref1")
        )
        .success
        .value

      val cleanedUserAnswers = LifetimeAllowanceChargePage.cleanup(Some(true), ua).success.value

      cleanedUserAnswers.get(LumpSumValuePage) mustBe None
      cleanedUserAnswers.get(AnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(WhoPaidLTAChargePage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe None
      cleanedUserAnswers.get(YearChargePaidPage) mustBe None
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe None
      cleanedUserAnswers.get(NewExcessLifetimeAllowancePaidPage) mustBe None
      cleanedUserAnswers.get(NewLumpSumValuePage) mustBe None
      cleanedUserAnswers.get(NewAnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe None
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None
    }

    "must cleanup correctly when false" in {
      val ua = emptyUserAnswers
        .set(
          LumpSumValuePage,
          BigInt(1)
        )
        .success
        .value
        .set(
          AnnualPaymentValuePage,
          BigInt(10)
        )
        .success
        .value
        .set(
          UserSchemeDetailsPage,
          models.UserSchemeDetails("string", "ref")
        )
        .success
        .value
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.You
        )
        .success
        .value
        .set(
          SchemeNameAndTaxRefPage,
          models.SchemeNameAndTaxRef("string", "ref")
        )
        .success
        .value
        .set(
          YearChargePaidPage,
          models.YearChargePaid._2017To2018
        )
        .success
        .value
        .set(
          QuarterChargePaidPage,
          models.QuarterChargePaid.JanToApr
        )
        .success
        .value
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Annualpayment
        )
        .success
        .value
        .set(
          NewLumpSumValuePage,
          BigInt(11)
        )
        .success
        .value
        .set(
          NewAnnualPaymentValuePage,
          BigInt(12)
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
          LtaPensionSchemeDetails("string1", "ref1")
        )
        .success
        .value

      val cleanedUserAnswers = LifetimeAllowanceChargePage.cleanup(Some(false), ua).success.value

      cleanedUserAnswers.get(LumpSumValuePage) mustBe None
      cleanedUserAnswers.get(AnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(WhoPaidLTAChargePage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe None
      cleanedUserAnswers.get(YearChargePaidPage) mustBe None
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe None
      cleanedUserAnswers.get(NewExcessLifetimeAllowancePaidPage) mustBe None
      cleanedUserAnswers.get(NewLumpSumValuePage) mustBe None
      cleanedUserAnswers.get(NewAnnualPaymentValuePage) mustBe None
      cleanedUserAnswers.get(WhoPayingExtraLtaChargePage) mustBe None
      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None
    }

  }

}
