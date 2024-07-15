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

import models.Period.{_2013, _2022}
import models.{CheckMode, NormalMode, PayeCodeAdjustment, Period, ThresholdIncome}
import pages.behaviours.PageBehaviours

class PayeCodeAdjustmentPageSpec extends PageBehaviours {

  val period = Period._2022

  "PayeCodeAdjustmentPage" - {

    beRetrievable[PayeCodeAdjustment](PayeCodeAdjustmentPage(period))

    beSettable[PayeCodeAdjustment](PayeCodeAdjustmentPage(period))

    beRemovable[PayeCodeAdjustment](PayeCodeAdjustmentPage(period))

    "Normal mode" - {

      "to CodeAdjustmentAmount when any answer" in {
        val ua     = emptyUserAnswers
          .set(
            PayeCodeAdjustmentPage(Period._2022),
            PayeCodeAdjustment.Increase
          )
          .success
          .value
        val result = PayeCodeAdjustmentPage(Period._2022).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2022/code-adjustment-amount")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = PayeCodeAdjustmentPage(Period._2022).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "to CodeAdjustmentAmount when any answer and PayeCodeAdjustment HAS NOT been answered" in {
        val ua     = emptyUserAnswers
          .set(
            PayeCodeAdjustmentPage(Period._2022),
            PayeCodeAdjustment.Increase
          )
          .success
          .value
        val result = PayeCodeAdjustmentPage(Period._2022).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2022/change-code-adjustment-amount")
      }

      "to CheckYourAAPeriodAnswers when any answer and PayeCodeAdjustment HAS been answered" in {
        val ua     = emptyUserAnswers
          .set(
            CodeAdjustmentAmountPage(period),
            BigInt(100)
          )
          .success
          .value
          .set(
            PayeCodeAdjustmentPage(Period._2022),
            PayeCodeAdjustment.Increase
          )
          .success
          .value
        val result = PayeCodeAdjustmentPage(Period._2022).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2022/check-answers")
      }

      "must navigate to journey recovery when no answer" in {
        val ua     = emptyUserAnswers
        val result = ThresholdIncomePage(_2013).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

    }

  }
}
