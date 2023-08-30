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

import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class AdjustedIncomePageSpec extends PageBehaviours {

  "AdjustedIncomePage" - {

    beRetrievable[BigInt](AdjustedIncomePage(Period._2018))

    beSettable[BigInt](AdjustedIncomePage(Period._2018))

    beRemovable[BigInt](AdjustedIncomePage(Period._2018))

    "must Navigate correctly in normal mode" - {

      "to TotalIncome page when answered" in {
        val ua     = emptyUserAnswers
          .set(
            AdjustedIncomePage(Period._2018),
            BigInt(100)
          )
          .success
          .value
        val result = AdjustedIncomePage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/total-income")
      }
    }

    "must Navigate correctly to CYA in check mode" in {
      val ua     = emptyUserAnswers
        .set(
          AdjustedIncomePage(Period._2018),
          BigInt(100)
        )
        .success
        .value
      val result = AdjustedIncomePage(Period._2018).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2018/check-answers")
    }
  }
}
