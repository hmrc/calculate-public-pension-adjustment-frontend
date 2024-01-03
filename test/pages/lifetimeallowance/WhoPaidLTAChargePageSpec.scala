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

import models.{CheckMode, NormalMode, WhoPaidLTACharge}
import pages.behaviours.PageBehaviours

class WhoPaidLTAChargeSpec extends PageBehaviours {

  "WhoPaidLTAChargePage" - {

    beRetrievable[WhoPaidLTACharge](WhoPaidLTAChargePage)

    beSettable[WhoPaidLTACharge](WhoPaidLTAChargePage)

    beRemovable[WhoPaidLTACharge](WhoPaidLTAChargePage)
  }

  "normal mode navigation" - {

    "when user selects you" in {

      val userAnswers = emptyUserAnswers
        .set(WhoPaidLTAChargePage, WhoPaidLTACharge.You)
        .get

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/scheme-name-reference")
    }

    "when user selects scheme" in {

      val userAnswers = emptyUserAnswers
        .set(WhoPaidLTAChargePage, WhoPaidLTACharge.PensionScheme)
        .get

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/scheme-paid-charge-amount")
    }

    "must navigate to journey recovery when no answer" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user selects you" in {

      val userAnswers = emptyUserAnswers
        .set(WhoPaidLTAChargePage, WhoPaidLTACharge.You)
        .get

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-scheme-name-reference")
    }

    "when user selects scheme" in {

      val userAnswers = emptyUserAnswers
        .set(WhoPaidLTAChargePage, WhoPaidLTACharge.PensionScheme)
        .get

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-scheme-paid-charge-amount")
    }

    "must navigate to journey recovery when no answer" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = WhoPaidLTAChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when you is selected" in {
      val ua = emptyUserAnswers
        .set(
          UserSchemeDetailsPage,
          models.UserSchemeDetails("string1", "ref1")
        )
        .success
        .value
        .set(
          SchemeNameAndTaxRefPage,
          models.SchemeNameAndTaxRef("string2", "ref2")
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
          YearChargePaidPage,
          models.YearChargePaid._2017To2018
        )
        .success
        .value

      val cleanedUserAnswers = WhoPaidLTAChargePage.cleanup(Some(WhoPaidLTACharge.You), ua).success.value

      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe Some(models.UserSchemeDetails("string1", "ref1"))
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe None
      cleanedUserAnswers.get(YearChargePaidPage) mustBe None
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe None
    }

    "must cleanup correctly when scheme is selected" in {
      val ua = emptyUserAnswers
        .set(
          UserSchemeDetailsPage,
          models.UserSchemeDetails("string1", "ref1")
        )
        .success
        .value
        .set(
          SchemeNameAndTaxRefPage,
          models.SchemeNameAndTaxRef("string2", "ref2")
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
          YearChargePaidPage,
          models.YearChargePaid._2017To2018
        )
        .success
        .value

      val cleanedUserAnswers = WhoPaidLTAChargePage.cleanup(Some(WhoPaidLTACharge.PensionScheme), ua).success.value

      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None
      cleanedUserAnswers.get(SchemeNameAndTaxRefPage) mustBe Some(models.SchemeNameAndTaxRef("string2", "ref2"))
      cleanedUserAnswers.get(YearChargePaidPage) mustBe Some(models.YearChargePaid._2017To2018)
      cleanedUserAnswers.get(QuarterChargePaidPage) mustBe Some(models.QuarterChargePaid.JanToApr)
    }

  }
}
