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

import models.{NormalMode, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class PayAChargePageSpec extends PageBehaviours {

  "PayAChargePage" - {

    beRetrievable[Boolean](PayAChargePage(Period._2018, SchemeIndex(0)))

    beSettable[Boolean](PayAChargePage(Period._2018, SchemeIndex(0)))

    beRemovable[Boolean](PayAChargePage(Period._2018, SchemeIndex(0)))
  }

  "normal mode navigation" - {

    "when did not pay charge and member of more than one scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, false).get.set(MemberMoreThanOnePensionPage(Period._2018), true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/add-another-scheme/2018/0") // TODO until onward pages are added
    }

    "when did not pay charge and not member of more than one scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, false).get.set(MemberMoreThanOnePensionPage(Period._2018), false).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-period/2018") // TODO until onward pages are added
    }

    "when did pay charge then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, true).get.set(MemberMoreThanOnePensionPage(Period._2018), true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/who-paid-annual-allowance-charge/2018/0")
    }
  }
}
