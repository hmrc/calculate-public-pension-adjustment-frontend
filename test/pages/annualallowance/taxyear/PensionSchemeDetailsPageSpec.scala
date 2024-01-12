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

import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class PensionSchemeDetailsPageSpec extends PageBehaviours {

  "PensionSchemeDetailsPage" - {

    "Normal mode" - {

      "must redirect to pension scheme input amount page" in {

        val page = PensionSchemeDetailsPage(Period._2018, SchemeIndex(0))

        val userAnswers = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeDetails("schemeName", "12345678RL")
          )
          .success
          .value

        val nextPageUrl = page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/pension-input-amount")
      }

      "must redirect to pension scheme input amount 2016 pre page when in 2016 period" in {

        val page = PensionSchemeDetailsPage(Period._2016, SchemeIndex(0))

        val userAnswers = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)),
            PensionSchemeDetails("schemeName", "12345678RL")
          )
          .success
          .value

        val nextPageUrl = page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2016/pension-scheme-0/pension-input-amount-15-16-period-1")
      }
    }
  }

  "Check mode" - {

    "must redirect to CYA page" in {

      val page = PensionSchemeDetailsPage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(
          PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)),
          PensionSchemeDetails("schemeName", "12345678RL")
        )
        .success
        .value

      val nextPageUrl = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/check-answers")

    }
  }
}
