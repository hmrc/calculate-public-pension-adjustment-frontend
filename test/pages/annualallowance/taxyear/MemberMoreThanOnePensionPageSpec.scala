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

import models.{NormalMode, Period}
import pages.behaviours.PageBehaviours

class MemberMoreThanOnePensionPageSpec extends PageBehaviours {

  "MemberMoreThanOnePensionPage" - {

    beRetrievable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    beSettable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    beRemovable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    "normal mode navigation" - {

      "when current period is first relevant period then capture scheme details for current period, scheme index 0" in {
        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false).get

        val nextPageUrl: String =
          MemberMoreThanOnePensionPage(Period._2016PreAlignment).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2016-pre/scheme-name-reference/0")
      }

      "when current period is not first relevant period then select which scheme for current period, scheme index 0" in {
        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), false).get

        val nextUrl: String =
          MemberMoreThanOnePensionPage(Period._2016PostAlignment).navigate(NormalMode, userAnswers).url

        checkNavigation(nextUrl, "/annual-allowance/2016-post/select-scheme-0")
      }
    }
  }
}
