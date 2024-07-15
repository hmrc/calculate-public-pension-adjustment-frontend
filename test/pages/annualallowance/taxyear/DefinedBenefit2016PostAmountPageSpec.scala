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

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class DefinedBenefit2016PostAmountPageSpec extends PageBehaviours {

  "DefinedBenefit2016PostAmountPage" - {

    beRetrievable[BigInt](DefinedBenefit2016PostAmountPage)

    beSettable[BigInt](DefinedBenefit2016PostAmountPage)

    beRemovable[BigInt](DefinedBenefit2016PostAmountPage)
  }

  "Normal Mode" - {

    "must navigate to total income page when answered" in {

      val ua = emptyUserAnswers
        .set(DefinedBenefit2016PostAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PostAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/taxable-income")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedBenefit2016PostAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check Mode" - {

    "must navigate to CYA when answered" in {

      val ua = emptyUserAnswers
        .set(DefinedBenefit2016PostAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")

    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedBenefit2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
