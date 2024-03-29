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

import models.{CheckMode, NormalMode, YearChargePaid}
import pages.behaviours.PageBehaviours

class YearChargePaidSpec extends PageBehaviours {

  "YearChargePaidPage" - {

    beRetrievable[YearChargePaid](YearChargePaidPage)

    beSettable[YearChargePaid](YearChargePaidPage)

    beRemovable[YearChargePaid](YearChargePaidPage)
  }

  "normal mode navigation" - {

    "when user has entered value for Year Charge Paid page" in {

      val userAnswers = emptyUserAnswers.set(YearChargePaidPage, models.YearChargePaid._2016To2017).get

      val nextPageUrl: String = YearChargePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-excess-paid")
    }

    "when user hasn't entered value for Year Charge Paid page" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = YearChargePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has entered value for Year Charge Paid in Check mode and NewExcessLifetimeAllowancePaidPage does not exist" in {

      val userAnswers = emptyUserAnswers.set(YearChargePaidPage, models.YearChargePaid._2016To2017).get

      val nextPageUrl: String = YearChargePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-excess-paid")
    }

    "when user has entered value for Year Charge Paid in Check mode and NewExcessLifetimeAllowancePaidPage exists" in {

      val userAnswers = emptyUserAnswers
        .set(YearChargePaidPage, models.YearChargePaid._2016To2017)
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get

      val nextPageUrl: String = YearChargePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user hasn't entered value for Year Charge Paid in Check mode " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = YearChargePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
