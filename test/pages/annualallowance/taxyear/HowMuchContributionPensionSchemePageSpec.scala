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

class HowMuchContributionPensionSchemePageSpec extends PageBehaviours {

  "HowMuchContributionPensionSchemePage" - {

    beRetrievable[BigInt](HowMuchContributionPensionSchemePage(Period._2018))

    beSettable[BigInt](HowMuchContributionPensionSchemePage(Period._2018))

    beRemovable[BigInt](HowMuchContributionPensionSchemePage(Period._2018))

    "must Navigate correctly in normal mode" - {
      "to HasReliefClaimedOnOverseasPension page when anything answered" in {
        val ua = emptyUserAnswers
          .set(HowMuchContributionPensionSchemePage(Period._2017), BigInt(5000))
          .success
          .value

        val result = HowMuchContributionPensionSchemePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/any-tax-relief-overseas-pension")
      }
    }

    "must Navigate correctly in check mode" - {
      "to CheckAnswers page when anything answered" in {
        val ua = emptyUserAnswers
          .set(HowMuchContributionPensionSchemePage(Period._2017), BigInt(5000))
          .success
          .value

        val result = HowMuchContributionPensionSchemePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/check-answers")
      }
    }
  }
}
