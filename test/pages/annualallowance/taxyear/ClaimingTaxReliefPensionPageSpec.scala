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

class ClaimingTaxReliefPensionPageSpec extends PageBehaviours {

  val period = Period._2019

  "ClaimingTaxReliefPensionPage" - {

    beRetrievable[Boolean](ClaimingTaxReliefPensionPage(period))

    beSettable[Boolean](ClaimingTaxReliefPensionPage(period))

    beRemovable[Boolean](ClaimingTaxReliefPensionPage(period))
  }

  "Normal mode" - {

    "to TaxReliefPage when answered true" in {
      val ua     = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(period),
          true
        )
        .success
        .value
      val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/total-income/tax-relief")
    }

    "to InterestFromSavingsPage when answered false" in {
      val ua     = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(period),
          false
        )
        .success
        .value
      val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/interest-from-savings/2019")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ClaimingTaxReliefPensionPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "to TaxReliefPage in check mode when answered true" in {
      val ua     = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(period),
          true
        )
        .success
        .value
      val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/total-income/change-tax-relief")
    }

    "must Navigate correctly to CYA in check mode when answered no" in {
      val ua     = emptyUserAnswers
        .set(
          ClaimingTaxReliefPensionPage(period),
          false
        )
        .success
        .value
      val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2019/check-answers")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ClaimingTaxReliefPensionPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

}
