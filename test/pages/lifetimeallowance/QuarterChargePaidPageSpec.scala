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

import models.{CheckMode, NormalMode, QuarterChargePaid}
import pages.behaviours.PageBehaviours

class QuarterChargePaidSpec extends PageBehaviours {

  "QuarterChargePaidPage" - {

    beRetrievable[QuarterChargePaid](QuarterChargePaidPage)

    beSettable[QuarterChargePaid](QuarterChargePaidPage)

    beRemovable[QuarterChargePaid](QuarterChargePaidPage)
  }

  "normal mode navigation" - {

    "when user has entered value for Quarter Charge Paid page" in {

      val userAnswers = emptyUserAnswers.set(QuarterChargePaidPage, models.QuarterChargePaid.JanToMarch).get

      val nextPageUrl: String = QuarterChargePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/year-charge-was-paid")
    }

    "when user hasn't entered value for Quarter Charge Paid page" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = QuarterChargePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has entered value for Quarter Charge Paid in Check mode " in {

      val userAnswers = emptyUserAnswers.set(QuarterChargePaidPage, models.QuarterChargePaid.JanToMarch).get

      val nextPageUrl: String = QuarterChargePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user hasn't entered value for Quarter Charge Paid in Check mode " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = QuarterChargePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
