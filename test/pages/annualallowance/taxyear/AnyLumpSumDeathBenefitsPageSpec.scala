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

class AnyLumpSumDeathBenefitsPageSpec extends PageBehaviours {

  "AnyLumpSumDeathBenefitsPage" - {

    beRetrievable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2013))

    beSettable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2013))

    beRemovable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2013))
  }

  "Normal mode" - {

    "to LumpSumDeathBenefitsValuePage when answered true" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2013),
          true
        )
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2013).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/threshold-income/lump-sum-death-benefits-value/2013")
    }

//    "to MCSC-877 when answered false" in {
//      val ua     = emptyUserAnswers
//        .set(
//          AnyLumpSumDeathBenefitsPage(Period._2013),
//          false
//        )
//        .success
//        .value
//      val result = AnyLumpSumDeathBenefitsPage(Period._2013).navigate(NormalMode, ua).url
//
//      checkNavigation(result, "/annual-allowance/2013/total-income")
//    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ThresholdIncomePage(Period._2013).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must Navigate correctly to CYA in check mode when answered no" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2013),
          false
        )
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "must Navigate correctly to LumpSumDeathBenefitsValuePage in check mode when answered yes" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2013),
          true
        )
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/threshold-income/change-lump-sum-death-benefits-value/2013")
    }

    "must navigate to journey recovery when no answer" in {
      val ua     = emptyUserAnswers
      val result = AnyLumpSumDeathBenefitsPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

  }
}
