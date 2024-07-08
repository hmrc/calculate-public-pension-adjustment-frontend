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

import models.{CheckMode, NormalMode, PayeCodeAdjustment, Period}
import pages.behaviours.PageBehaviours

class DoYouHaveCodeAdjustmentPageSpec extends PageBehaviours {

  val period = Period._2022

  "DoYouHaveCodeAdjustmentPage" - {

    beRetrievable[Boolean](DoYouHaveCodeAdjustmentPage(period))

    beSettable[Boolean](DoYouHaveCodeAdjustmentPage(period))

    beRemovable[Boolean](DoYouHaveCodeAdjustmentPage(period))
  }

  "Normal mode" - {

    "when user answers true go to PayeCodeAdjustment page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveCodeAdjustmentPage(period),
          true
        )
        .success
        .value

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/paye-code-adjustment")

    }

    "when user answers false go to BlindAllowance page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveCodeAdjustmentPage(period),
          false
        )
        .success
        .value

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/blind-person-allowance")

    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "when user answers true and PayeCodeAdjustment has not already been answered go to PayeCodeAdjustment page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveCodeAdjustmentPage(period),
          true
        )
        .success
        .value

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/change-paye-code-adjustment")

    }

    "when user answers true and PayeCodeAdjustment HAS been answered go to CheckYourAAPeriodAnswers" in {

      val ua = emptyUserAnswers
        .set(
          PayeCodeAdjustmentPage(period),
          PayeCodeAdjustment.Increase
        )
        .success
        .value
        .set(
          DoYouHaveCodeAdjustmentPage(period),
          true
        )
        .success
        .value

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/check-answers")

    }

    "when user answers false go to CheckYourAAPeriodAnswers" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveCodeAdjustmentPage(period),
          false
        )
        .success
        .value

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/check-answers")

    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DoYouHaveCodeAdjustmentPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly" in {

      val ua = emptyUserAnswers
        .set(DoYouHaveCodeAdjustmentPage(period), true)
        .success
        .value
        .set(PayeCodeAdjustmentPage(period), PayeCodeAdjustment.Increase)
        .success
        .value
        .set(CodeAdjustmentAmountPage(period), BigInt(2))
        .success
        .value

      val cleanedUserAnswers = DoYouHaveCodeAdjustmentPage(period).cleanup(Some(false), ua).success.value

      cleanedUserAnswers.get(PayeCodeAdjustmentPage(period)) mustBe None
      cleanedUserAnswers.get(CodeAdjustmentAmountPage(period)) mustBe None

    }
  }
}
