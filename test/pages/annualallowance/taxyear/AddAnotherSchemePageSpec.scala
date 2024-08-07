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

import models.{NormalMode, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.behaviours.PageBehaviours

class AddAnotherSchemePageSpec extends PageBehaviours {

  "AddAnotherSchemePage" - {

    beRetrievable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    beSettable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    beRemovable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    "when answer yes and current period is first period then capture scheme details for current period with incremented index" in {
      val page = AddAnotherSchemePage(Period._2016, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/scheme-name-reference/1")
    }

    "when answer yes and current period is not first period then select which scheme with incremented index" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(1))

      val answersWithAPreviousPeriodCompleted =
        emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016), false).get

      val userAnswers = answersWithAPreviousPeriodCompleted.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/select-scheme-2")
    }

    "must redirect to other db/dc page when answer no and have dc scheme in standard period" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/contributed-to-any-other-dc-or-db-scheme")
    }

    "must redirect to threshold income page when when answer no and do not have dc scheme in standard period" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/threshold-income")
    }

    "must redirect to other db/dc page when when answer no and have dc scheme in 2016 period" in {
      val page = AddAnotherSchemePage(Period._2016, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/contributed-to-any-other-dc-or-db-scheme")
    }

    "must redirect to total income when answer no and do not have dc scheme in 2016 period" in {
      val page = AddAnotherSchemePage(Period._2016, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(page, false)
        .get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/taxable-income")
    }

    "must redirect to journey recovery when no answer" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val nextPagUrl = page.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPagUrl, "/there-is-a-problem")

    }
  }
}
