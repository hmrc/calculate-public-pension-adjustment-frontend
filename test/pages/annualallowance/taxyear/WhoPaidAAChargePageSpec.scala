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

package pages.annualallowance.taxyear

import models.{CheckMode, NormalMode, Period, SchemeIndex, WhoPaidAACharge}
import pages.behaviours.PageBehaviours

class WhoPaidAAChargeSpec extends PageBehaviours {

  "WhoPaidAAChargePage" - {

    beRetrievable[WhoPaidAACharge](WhoPaidAAChargePage(Period._2018, SchemeIndex(0)))

    beSettable[WhoPaidAACharge](WhoPaidAAChargePage(Period._2018, SchemeIndex(0)))

    beRemovable[WhoPaidAACharge](WhoPaidAAChargePage(Period._2018, SchemeIndex(0)))

    "must redirect to how much charge you paid page when user selects you" in {
      val page = WhoPaidAAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, WhoPaidAACharge.You)
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/charge-amount-you-paid")
    }

    "must redirect to how much charge scheme paid page when user selects scheme" in {
      val page = WhoPaidAAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, WhoPaidAACharge.Scheme)
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/charge-amount-pension-scheme-paid")
    }

    "must redirect to how much charge you paid page when user selects both" in {
      val page = WhoPaidAAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, WhoPaidAACharge.Both)
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/charge-amount-you-paid")
    }

    "must redirect user to adjust charge when user resubmits answers in check mode" in {
      val page = WhoPaidAAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, WhoPaidAACharge.You)
        .success
        .value

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/change-charge-amount-you-paid")
    }
  }
}
