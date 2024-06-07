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

class KnowAdjustedAmountPageSpec extends PageBehaviours {

  "KnowAdjustedAmountPage" - {

    beRetrievable[Boolean](KnowAdjustedAmountPage(Period._2018))

    beSettable[Boolean](KnowAdjustedAmountPage(Period._2018))

    beRemovable[Boolean](KnowAdjustedAmountPage(Period._2018))

    "Normal mode" - {

      "to AdjustedIncome when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            true
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/adjusted-income")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/claiming-tax-relief-pension/2018")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = KnowAdjustedAmountPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "to AdjustedIncome when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            true
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/change-adjusted-income")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            KnowAdjustedAmountPage(Period._2018),
            false
          )
          .success
          .value
        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-claiming-tax-relief-pension/2018")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = KnowAdjustedAmountPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

  }
}
