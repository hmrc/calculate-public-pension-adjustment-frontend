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
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DefinedBenefit2016PreAmountPageSpec extends PageBehaviours {

  "DefinedBenefit2016PreAmountPage" - {

    beRetrievable[BigInt](DefinedBenefit2016PreAmountPage)

    beSettable[BigInt](DefinedBenefit2016PreAmountPage)

    beRemovable[BigInt](DefinedBenefit2016PreAmountPage)
  }

  "Normal Mode" - {

    "must navigate to 2016Post DB amount page when NOT stopped paying in 2016pre sub period" in {

      val ua = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-benefit")
    }

    "must navigate to total income page when stopped paying in 2016pre sub period" in {

      val ua = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/total-income")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedBenefit2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check Mode" - {

    "must navigate to 2016 DB amount page when no answer for 2016post DB amount and not stopped paying in 2016 pre sub period" in {
      val ua = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PreAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-benefit")
    }

    "must navigate to CYA when stopped paying in 2016pre sub period" in {

      val ua = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedBenefit2016PreAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")
    }

    "must navigate to CYA when answer exists for 2016post DB amount and not stopped paying in 2016pre sub period" in {

      val ua = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedBenefit2016PostAmountPage, BigInt(2))
        .success
        .value

      val result = DefinedBenefit2016PreAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")
    }
  }
}
