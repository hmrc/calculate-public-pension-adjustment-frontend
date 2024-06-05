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

class InterestFromSavingsPageSpec extends PageBehaviours {

  val period = Period._2019

  "InterestFromSavingsPage" - {

    beRetrievable[BigInt](InterestFromSavingsPage(period))

    beSettable[BigInt](InterestFromSavingsPage(period))

    beRemovable[BigInt](InterestFromSavingsPage(period))
  }

  "Normal mode" - {

//    "to FUTUREPAGETOBEADDED when user enters data" in {
//      val ua     = emptyUserAnswers
//        .set(
//          InterestFromSavingsPage(period),
//          BigInt(100)
//        )
//        .success
//        .value
//      val result = InterestFromSavingsPage(period).navigate(NormalMode, ua).url
//
//      checkNavigation(result, "/annual-allowance/2019/total-income/tax-relief")
//    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ThresholdIncomePage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

//    "to FUTUREPAGETOBEADDED when user enters data in check mode" in {
//      val ua     = emptyUserAnswers
//        .set(
//          InterestFromSavingsPage(period),
//          BigInt(100)
//        )
//        .success
//        .value
//      val result = InterestFromSavingsPage(period).navigate(CheckMode, ua).url
//
//      checkNavigation(result, "/annual-allowance/2019/total-income/tax-relief")
//    }

    "must Navigate correctly to CYA in check mode when user enters data" in {
      val ua     = emptyUserAnswers
        .set(
          InterestFromSavingsPage(period),
          BigInt(100)
        )
        .success
        .value
      val result = InterestFromSavingsPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/check-answers")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = InterestFromSavingsPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
