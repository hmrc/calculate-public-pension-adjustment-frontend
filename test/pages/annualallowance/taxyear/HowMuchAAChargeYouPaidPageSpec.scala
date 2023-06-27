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

class HowMuchAAChargeYouPaidPageSpec extends PageBehaviours {

  "HowMuchAAChargeYouPaidPage" - {

    beRetrievable[BigInt](HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0)))

    beSettable[BigInt](HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0)))

    beRemovable[BigInt](HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0)))
  }

  "must redirect to how much charge scheme paid when user answers both to who paid AA charge" in {
    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.Both)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/how-much-pension-pay-charge/2018/0")
  }

  "must redirect to add another scheme page when member more than one pension and users answers you to who paid AA charge" in {
    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2018), true)
      .success
      .value
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/add-another-scheme/2018/0")
  }

  "must redirect to check your answers page when not a member of more than one scheme and user answers you to who paid AA charge" in {
    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2018), false)
      .success
      .value
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers-period/2018")
  }

  "must redirect to check your answers page when user submits and answers you to who paid AA charge in check mode" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers-period/2018")
  }

  "must redirect to how much AA charge scheme paid page when user submits and answers both to who paid AA charge in check mode in check mode" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.Both)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/change-how-much-pension-pay-charge/2018/0")
  }
}
