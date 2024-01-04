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

import models.{CheckMode, NormalMode, PensionSchemeInput2016postAmounts, PensionSchemeInput2016preAmounts, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class PensionSchemeInput2016preAmountsPageSpec extends PageBehaviours {

  "PensionSchemeInput2016preAmountsPage" - {

    "Normal mode" - {

      "must navigate to PensionSchemeInput2016postAmounts page" in {

        val page = PensionSchemeInput2016preAmountsPage(Period._2016, SchemeIndex(0))

        val userAnswers = emptyUserAnswers
          .set(
            PensionSchemeInput2016preAmountsPage(Period._2016, SchemeIndex(0)),
            PensionSchemeInput2016preAmounts(BigInt(1), BigInt(1))
          )
          .success
          .value

        val nextPageUrl = page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2016/pension-scheme-0/pension-input-amount-15-16-period-2")
      }
    }

    "Check mode" - {

      "must navigate to CYA page" in {

        val page = PensionSchemeInput2016preAmountsPage(Period._2016, SchemeIndex(0))

        val userAnswers = emptyUserAnswers
          .set(
            PensionSchemeInput2016preAmountsPage(Period._2016, SchemeIndex(0)),
            PensionSchemeInput2016preAmounts(BigInt(1), BigInt(1))
          )
          .success
          .value

        val nextPageUrl = page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2016/check-answers")
      }
    }
  }
}
