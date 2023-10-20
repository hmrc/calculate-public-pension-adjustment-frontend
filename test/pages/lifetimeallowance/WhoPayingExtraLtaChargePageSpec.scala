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

class WhoPayingExtraLtaChargeSpec extends PageBehaviours {

  "WhoPayingExtraLtaChargePage" - {

    beRetrievable[WhoPayingExtraLtaCharge](WhoPayingExtraLtaChargePage)

    beSettable[WhoPayingExtraLtaCharge](WhoPayingExtraLtaChargePage)

    beRemovable[WhoPayingExtraLtaCharge](WhoPayingExtraLtaChargePage)
  }

  "normal mode navigation" - {
    "when user selects pension scheme" in {

      val userAnswers =
        emptyUserAnswers.set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.PensionScheme).get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/scheme-paying-extra-charge")
    }

    "when user selects you and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.You)
        .get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user selects you and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.You)
        .get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/scheme-name-reference")
    }
  }

  "check mode navigation" - {
    "when user selects pension scheme" in {

      val userAnswers =
        emptyUserAnswers.set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.PensionScheme).get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-scheme-paying-extra-charge")
    }

    "when user selects you and previous charge is true" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, true)
        .get
        .set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.You)
        .get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user selects you and previous charge is false" in {

      val userAnswers = emptyUserAnswers
        .set(LifetimeAllowanceChargePage, false)
        .get
        .set(WhoPayingExtraLtaChargePage, models.WhoPayingExtraLtaCharge.You)
        .get

      val nextPageUrl: String = WhoPayingExtraLtaChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-scheme-name-reference")
    }
  }

  "cleanup" - {

    "must cleanup correctly when pensionscheme is selected and previous charge is false" in {
      val ua = emptyUserAnswers
        .set(
          UserSchemeDetailsPage,
          models.UserSchemeDetails("details3", "ref3")
        )
        .success
        .value
        .set(
          LifetimeAllowanceChargePage,
          false
        )
        .success
        .value
        .set(
          WhoPayingExtraLtaChargePage,
          models.WhoPayingExtraLtaCharge.PensionScheme
        )
        .success
        .value

      val cleanedUserAnswers =
        WhoPayingExtraLtaChargePage.cleanup(Some(models.WhoPayingExtraLtaCharge.PensionScheme), ua).success.value

      cleanedUserAnswers.get(UserSchemeDetailsPage) mustBe None

    }

    "must cleanup correctly when you is selected" in {
      val ua = emptyUserAnswers
        .set(
          LtaPensionSchemeDetailsPage,
          models.LtaPensionSchemeDetails("details3", "ref3")
        )
        .success
        .value
        .set(
          WhoPayingExtraLtaChargePage,
          models.WhoPayingExtraLtaCharge.You
        )
        .success
        .value

      val cleanedUserAnswers =
        WhoPayingExtraLtaChargePage.cleanup(Some(models.WhoPayingExtraLtaCharge.PensionScheme), ua).success.value

      cleanedUserAnswers.get(LtaPensionSchemeDetailsPage) mustBe None

    }

  }
}
