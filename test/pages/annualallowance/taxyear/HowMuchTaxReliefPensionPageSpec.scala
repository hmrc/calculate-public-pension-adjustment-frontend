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

class HowMuchTaxReliefPensionPageSpec extends PageBehaviours {

  "HowMuchTaxReliefPensionPage" - {

    beRetrievable[BigInt](HowMuchTaxReliefPensionPage(Period._2018))

    beSettable[BigInt](HowMuchTaxReliefPensionPage(Period._2018))

    beRemovable[BigInt](HowMuchTaxReliefPensionPage(Period._2018))

    "must Navigate correctly in normal mode" - {

      "to AreYouNonDom when period not 2016 when answered" in {
        val ua     = emptyUserAnswers
          .set(
            HowMuchTaxReliefPensionPage(Period._2018),
            BigInt(100)
          )
          .success
          .value
        val result = HowMuchTaxReliefPensionPage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/non-domicile")
      }
    }

    "must Navigate correctly in check mode" - {

      "to AreYouNonDom when period not 2016 when answered" in {
        val ua     = emptyUserAnswers
          .set(
            HowMuchTaxReliefPensionPage(Period._2018),
            BigInt(100)
          )
          .success
          .value
        val result = HowMuchTaxReliefPensionPage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/change-non-domicile")
      }
    }

  }
}
