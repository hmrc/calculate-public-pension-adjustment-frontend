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

class AnyLumpSumDeathBenefitsPageSpec extends PageBehaviours {

  "AnyLumpSumDeathBenefitsPage" - {

    beRetrievable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2017))

    beSettable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2017))

    beRemovable[Boolean](AnyLumpSumDeathBenefitsPage(Period._2017))
  }

  "Normal mode" - {

    "to LumpSumDeathBenefitsValue when period not 2016 and answered true" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          true
        )
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2017/lump-sum-death-benefits-value")
    }

    "to ClaimingTaxReliefPension when period not 2016 and answered false and threshold income is IDoNotKnow" in {
      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
        .success
        .value
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value

      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2017/claiming-tax-relief")
    }

    "to ClaimingTaxReliefPensionNotAdjustedIncome when period not 2016 and answered false and threshold income is Yes" in {
      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
        .success
        .value
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value

      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/claiming-tax-relief-pension/2017")
    }

    "to JourneyRecovery when period not 2016 and answered false and threshold income is no" in {
      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
        .success
        .value
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value

      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ThresholdIncomePage(Period._2013).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "to LumpSumDeathBenefitsValue when period not 2016 and answered true" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          true
        )
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2017/change-lump-sum-death-benefits-value")
    }

    "to ClaimingTaxReliefPension when period not 2016 and answered false and threshold income is IDoNotKnow" in {
      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.IDoNotKnow)
        .success
        .value
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value

      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2017/change-claiming-tax-relief")
    }

    "to ClaimingTaxReliefPensionNotAdjustedIncome when period not 2016 and answered false and threshold income is Yes" in {
      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.Yes)
        .success
        .value
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value

      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/change-claiming-tax-relief-pension/2017")
    }

    "to JourneyRecovery when period not 2016 and answered false and threshold income is no" in {
      val ua     = emptyUserAnswers
        .set(
          AnyLumpSumDeathBenefitsPage(Period._2017),
          false
        )
        .success
        .value
        .set(ThresholdIncomePage(Period._2017), ThresholdIncome.No)
        .success
        .value
      val result = AnyLumpSumDeathBenefitsPage(Period._2017).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "to JourneyRecovery when not answered" in {
      val ua     = emptyUserAnswers
      val result = ThresholdIncomePage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
