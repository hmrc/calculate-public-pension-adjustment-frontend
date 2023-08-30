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

import models.Period._2013
import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class ThresholdIncomePageSpec extends PageBehaviours {

  "ThresholdIncomePage" - {

    beRetrievable[Boolean](ThresholdIncomePage(Period._2013))

    beSettable[Boolean](ThresholdIncomePage(Period._2013))

    beRemovable[Boolean](ThresholdIncomePage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "to AdjustedIncomePage when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2013),
            true
          )
          .success
          .value
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/adjusted-income")
      }

      "to TotalIncomePage when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            ThresholdIncomePage(Period._2013),
            false
          )
          .success
          .value
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/total-income")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ThresholdIncomePage(_2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must Navigate correctly to CYA in check mode when answered no" in {
      val ua     = emptyUserAnswers
        .set(
          ThresholdIncomePage(Period._2013),
          false
        )
        .success
        .value
      val result = ThresholdIncomePage(_2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "must Navigate correctly to CYA in check mode when answered yes" in {
      val ua     = emptyUserAnswers
        .set(
          ThresholdIncomePage(Period._2013),
          true
        )
        .success
        .value
      val result = ThresholdIncomePage(_2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-adjusted-income")
    }

    "must cleanup correctly when answered no" in {
      val ua = emptyUserAnswers
        .set(
          AdjustedIncomePage(Period._2013),
          BigInt("100")
        )
        .success
        .value

      val cleanedUserAnswers = ThresholdIncomePage(_2013).cleanup(Some(false), ua).success.value
      cleanedUserAnswers.get(AdjustedIncomePage(Period._2013)) mustBe None
    }
  }
}
