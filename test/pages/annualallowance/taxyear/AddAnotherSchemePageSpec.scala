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

class AddAnotherSchemePageSpec extends PageBehaviours {

  "AddAnotherSchemePage" - {

    beRetrievable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    beSettable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    beRemovable[Boolean](AddAnotherSchemePage(Period._2018, SchemeIndex(0)))

    "when answer yes and current period is first period then capture scheme details for current period with incremented index" in {
      val page = AddAnotherSchemePage(Period._2016PreAlignment, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/pension-scheme-details/2016-pre/1")
    }

    "when answer yes and current period is not first period then select which scheme with incremented index" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(1))

      val answersWithAPreviousPeriodCompleted =
        emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false).get

      val userAnswers = answersWithAPreviousPeriodCompleted.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/which-scheme/2018/2")
    }

    "when answer no then onward navigation" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers.set(page, false).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-period/2018") // TODO until onward pages are added
    }
  }
}
