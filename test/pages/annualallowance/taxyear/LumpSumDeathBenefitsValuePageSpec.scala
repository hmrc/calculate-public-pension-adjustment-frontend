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

import models.{CheckMode, NormalMode, Period, ThresholdIncome}
import pages.behaviours.PageBehaviours

class LumpSumDeathBenefitsValuePageSpec extends PageBehaviours {

  "LumpSumDeathBenefitsValuePage" - {

    beRetrievable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    beSettable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    beRemovable[BigInt](LumpSumDeathBenefitsValuePage(Period._2016))

    "NormalMode" - {

      "to ClaimingTaxReliefPension when period not 2016 and threshold income is IDoNotKnow" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/adjusted-income/claiming-tax-relief-pension/2017")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when period not 2016 and threshold income is Yes" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief-pension")
      }

      "to JourneyRecovery when period not 2016 and threshold income is no" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

    }

    "CheckMode" - {

      "to ClaimingTaxReliefPension when period not 2016 and threshold income is IDoNotKnow" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/adjusted-income/change-claiming-tax-relief-pension/2017")
      }

      "to ClaimingTaxReliefPensionNotAdjustedIncome when period not 2016 and threshold income is Yes" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2017/change-claiming-tax-relief-pension")
      }

      "to JourneyRecovery when period not 2016 and threshold income is no" in {
        val ua     = emptyUserAnswers
          .set(
            LumpSumDeathBenefitsValuePage(Period._2017),
            BigInt(100)
          )
          .success
          .value
          .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
          .success
          .value
        val result = LumpSumDeathBenefitsValuePage(Period._2017).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

    }

  }

}
