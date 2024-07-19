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

import models.{CheckMode, NormalMode, Period, SchemeIndex, WhoPaidAACharge}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
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

    checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/charge-amount-pension-scheme-paid")
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

    checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/pension-scheme-summary")
  }

  "must redirect to other db/dc page when does have dc scheme and not member more than one scheme in standard period" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(DefinedContributionPensionSchemePage, true)
      .get
      .set(MemberMoreThanOnePensionPage(Period._2018), false)
      .get
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/contributed-to-any-other-dc-or-db-scheme")
  }

  "must redirect to threshold income page when does not have dc scheme and not member more than one scheme in standard period" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(DefinedContributionPensionSchemePage, false)
      .get
      .set(MemberMoreThanOnePensionPage(Period._2018), false)
      .get
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/threshold-income")
  }

  "must redirect to other db/dc page when does have dc scheme and not member more than one scheme in 2016 period" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2016, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(DefinedContributionPensionSchemePage, true)
      .get
      .set(MemberMoreThanOnePensionPage(Period._2016), false)
      .get
      .set(WhoPaidAAChargePage(Period._2016, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016/contributed-to-any-other-dc-or-db-scheme")
  }

  "must redirect to total income page when does not have dc scheme and not member more than one scheme in 2016 period" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2016, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(DefinedContributionPensionSchemePage, false)
      .get
      .set(MemberMoreThanOnePensionPage(Period._2016), false)
      .get
      .set(WhoPaidAAChargePage(Period._2016, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016/taxable-income")
  }

  "must redirect to check your answers page when user submits and answers you to who paid AA charge in check mode" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.You)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/check-answers")
  }

  "must redirect to how much AA charge scheme paid page when user submits and answers both to who paid AA charge in check mode in check mode" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val userAnswers = emptyUserAnswers
      .set(WhoPaidAAChargePage(Period._2018, SchemeIndex(0)), WhoPaidAACharge.Both)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/change-charge-amount-pension-scheme-paid")
  }

  "must redirect to journey recovery when no answer in check mode" in {

    val page = HowMuchAAChargeYouPaidPage(Period._2018, SchemeIndex(0))

    val nextPageUrl: String = page.navigate(CheckMode, emptyUserAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
