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

package pages.annualallowance.taxyear

import models.{CheckMode, NormalMode, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
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

      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2018), true)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/pension-scheme-summary")
    }

    "when did not pay charge in standard period and not member of more than one scheme and does have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2018), false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/contributed-to-any-other-dc-or-db-scheme")
    }

    "when did not pay charge in standard period and not member of more than one scheme and does not have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2018), false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/threshold-income")
    }

    "when did not pay charge in 2016 period and not member of more than one scheme and does have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016), false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/contributed-to-any-other-dc-or-db-scheme")
    }

    "when did not pay charge in 2016 period and not member of more than one scheme and does not have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016), false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/taxable-income")
    }

    "when did not pay charge, has entered 5 schemes and does not have DC scheme" in {
      val page = PayAChargePage(Period._2017, SchemeIndex(4))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2017), true)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2017/threshold-income")
    }

    "when did not pay charge, has entered 5 schemes and does have DC scheme" in {
      val page = PayAChargePage(Period._2017, SchemeIndex(4))

      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2017), true)
        .get
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2017/contributed-to-any-other-dc-or-db-scheme")
    }

    "when did not pay charge, has entered 5 schemes and does not have DC scheme for 2016" in {
      val page = PayAChargePage(Period._2016, SchemeIndex(4))

      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2016), true)
        .get
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/taxable-income")
    }

    "when did pay charge then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2018), true).get.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/who-paid-charge")
    }

    "must redirect to journey recovery when no  in normal mode" in {

      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val nextPageUrl = page.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }

    "must redirect to journey recovery when no  in check mode" in {

      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val nextPageUrl = page.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }

    "must navigate to how much aa charge scheme paid when user answers true and not first scheme within period" in {

      val page = PayAChargePage(Period._2018, SchemeIndex(1))

      val userAnswers = emptyUserAnswers
        .set(page, true)
        .success
        .value

      val nextPageUrl = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-1/charge-amount-pension-scheme-paid")
    }
  }
}
