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

import models.{CheckMode, NormalMode, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.behaviours.PageBehaviours

class HowMuchAAChargeSchemePaidPageSpec extends PageBehaviours {

  "HowMuchAAChargeSchemePaidPage" - {

    beRetrievable[BigInt](HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0)))

    beSettable[BigInt](HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0)))

    beRemovable[BigInt](HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0)))
  }

  "must redirect to add another scheme page when member more than one scheme" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2018), true)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/add-another-scheme/2018/0")
  }

  "must redirect to other db/dc page when does have dc scheme and not member more than one scheme in standard period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2018), false)
      .get
      .set(DefinedContributionPensionSchemePage, true)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/contributed-to-any-other-dc-or-db-scheme")
  }

  "must redirect to threshold income page when does not have dc scheme and not member more than one scheme in standard period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2018), false)
      .get
      .set(DefinedContributionPensionSchemePage, false)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/threshold-income")
  }

  "must redirect to other db/dc page when does have dc scheme and not member more than one scheme in 2016-pre period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2016PreAlignment, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false)
      .get
      .set(DefinedContributionPensionSchemePage, true)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016-pre/contributed-to-any-other-dc-or-db-scheme")
  }

  "must redirect to check your answers page when does not have dc scheme and not member more than one scheme in 2016-pre period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2016PreAlignment, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false)
      .get
      .set(DefinedContributionPensionSchemePage, false)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016-pre/check-answers")
  }

  "must redirect to other db/dc page when does have dc scheme and not member more than one scheme in 2016-post period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2016PostAlignment, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2016PostAlignment), false)
      .get
      .set(DefinedContributionPensionSchemePage, true)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016-post/contributed-to-any-other-dc-or-db-scheme")
  }

  "must redirect to total income page when does not have dc scheme and not member more than one scheme in 2016-post period" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2016PostAlignment, SchemeIndex(0))

    val userAnswers         = emptyUserAnswers
      .set(MemberMoreThanOnePensionPage(Period._2016PostAlignment), false)
      .get
      .set(DefinedContributionPensionSchemePage, false)
      .success
      .value
    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2016-post/total-income")
  }

  "must redirect to check your answers controller when user submits in check mode" in {

    val page = HowMuchAAChargeSchemePaidPage(Period._2018, SchemeIndex(0))

    val nextPageUrl: String = page.navigate(CheckMode, emptyUserAnswers).url

    checkNavigation(nextPageUrl, "/annual-allowance/2018/check-answers")
  }
}
