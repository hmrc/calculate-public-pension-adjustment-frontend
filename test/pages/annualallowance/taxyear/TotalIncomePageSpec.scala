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

import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class TotalIncomePageSpec extends PageBehaviours {

  "TotalIncomePage" - {

    beRetrievable[BigInt](TotalIncomePage(Period._2013))

    beSettable[BigInt](TotalIncomePage(Period._2013))

    beRemovable[BigInt](TotalIncomePage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "to CYA page when answered" in {
        val ua     = emptyUserAnswers
          .set(
            TotalIncomePage(Period._2013),
            BigInt(100)
          )
          .success
          .value
        val result = TotalIncomePage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/total-income/personal-allowance")
      }
    }

    "must Navigate correctly to CYA in check mode" in {
      val ua     = emptyUserAnswers
        .set(
          TotalIncomePage(Period._2013),
          BigInt(100)
        )
        .success
        .value
      val result = TotalIncomePage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }
  }
}
