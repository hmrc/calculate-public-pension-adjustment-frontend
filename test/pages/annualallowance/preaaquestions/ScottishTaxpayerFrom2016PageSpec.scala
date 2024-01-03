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

package pages.annualallowance.preaaquestions

import models.{CheckMode, NormalMode, WhichYearsScottishTaxpayer}
import pages.behaviours.PageBehaviours

class ScottishTaxpayerFrom2016PageSpec extends PageBehaviours {

  "ScottishTaxpayerFrom2016Page" - {

    beRetrievable[Boolean](ScottishTaxpayerFrom2016Page)

    beSettable[Boolean](ScottishTaxpayerFrom2016Page)

    beRemovable[Boolean](ScottishTaxpayerFrom2016Page)

    "Normal Mode" - {

      "must redirect to which year scottish tax payer page when true" in {

        val userAnswers = emptyUserAnswers
          .set(ScottishTaxpayerFrom2016Page, true)
          .success
          .value

        val result = ScottishTaxpayerFrom2016Page.navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/scottish-taxpayer-years")
      }

      "must redirect to paying public pension scheme page when false" in {

        val userAnswers = emptyUserAnswers
          .set(ScottishTaxpayerFrom2016Page, false)
          .success
          .value

        val result = ScottishTaxpayerFrom2016Page.navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/paying-into-public-service-pension")
      }

      "must redirect to paying scottish tax payer from 2016 page when no answer" in {

        val result = ScottishTaxpayerFrom2016Page.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(result, "/annual-allowance/scottish-taxpayer")
      }
    }

    "Check mode" - {

      "must redirect to which year scottish tax payer page when true" in {

        val userAnswers = emptyUserAnswers
          .set(ScottishTaxpayerFrom2016Page, true)
          .success
          .value

        val result = ScottishTaxpayerFrom2016Page.navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/change-scottish-taxpayer-years")
      }

      "must redirect to CYA page when false" in {

        val userAnswers = emptyUserAnswers
          .set(ScottishTaxpayerFrom2016Page, false)
          .success
          .value

        val result = ScottishTaxpayerFrom2016Page.navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/setup-check-answers")
      }

      "must redirect to journey recovery when no answer" in {

        val result = ScottishTaxpayerFrom2016Page.navigate(CheckMode, emptyUserAnswers).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "clean up" - {

      "must not remove ReasonForResubmissionPage when the answer is yes" in {

        val answers =
          emptyUserAnswers.set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet).success.value

        val result = answers.set(ScottishTaxpayerFrom2016Page, true).success.value

        result.get(ScottishTaxpayerFrom2016Page)   must be(defined)
        result.get(WhichYearsScottishTaxpayerPage) must be(defined)
      }

      "must remove ReasonForResubmissionPage when the answer is no" in {

        val answers =
          emptyUserAnswers.set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet).success.value

        val result = answers.set(ScottishTaxpayerFrom2016Page, false).success.value

        result.get(ScottishTaxpayerFrom2016Page)   must be(defined)
        result.get(WhichYearsScottishTaxpayerPage) must not be defined
      }
    }
  }
}
