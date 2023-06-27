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

      val userAnswers = emptyUserAnswers.set(page, false).get.set(MemberMoreThanOnePensionPage(Period._2018), true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/add-another-scheme/2018/0")
    }

    "when did not pay charge in standard period and not member of more than one scheme and does have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2018), false)
        .get
        .set(DefinedContributionPensionSchemePage, true)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/otherDefinedBenefitOrContribution/2018/0")
    }

    "when did not pay charge in standard period and not member of more than one scheme and does not have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2018), false)
        .get
        .set(DefinedContributionPensionSchemePage, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/thresholdIncome/2018/0")
    }

    "when did not pay charge in 2016-pre period and not member of more than one scheme and does have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016PreAlignment, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false)
        .get
        .set(DefinedContributionPensionSchemePage, true)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/otherDefinedBenefitOrContribution/2016-pre/0")
    }

    "when did not pay charge in 2016-pre period and not member of more than one scheme and does not have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016PreAlignment, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false)
        .get
        .set(DefinedContributionPensionSchemePage, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-period/2016-pre")
    }

    "when did not pay charge in 2016-post period and not member of more than one scheme and does have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016PostAlignment, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PostAlignment), false)
        .get
        .set(DefinedContributionPensionSchemePage, true)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/otherDefinedBenefitOrContribution/2016-post/0")
    }

    "when did not pay charge in 2016-post period and not member of more than one scheme and does not have DC scheme then check onward navigation" in {
      val page = PayAChargePage(Period._2016PostAlignment, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PostAlignment), false)
        .get
        .set(DefinedContributionPensionSchemePage, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/totalIncome/2016-post/0")
    }

    "when did pay charge then check onward navigation" in {
      val page = PayAChargePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, true).get.set(MemberMoreThanOnePensionPage(Period._2018), true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/who-paid-annual-allowance-charge/2018/0")
    }
  }
}
