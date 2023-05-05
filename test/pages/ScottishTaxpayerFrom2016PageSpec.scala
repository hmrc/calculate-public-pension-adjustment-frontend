/*
 * Copyright 2023 HM Revenue & Customs
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

package pages

import pages.behaviours.PageBehaviours
import models.WhichYearsScottishTaxpayer

class ScottishTaxpayerFrom2016PageSpec extends PageBehaviours {

  "ScottishTaxpayerFrom2016Page" - {

    beRetrievable[Boolean](ScottishTaxpayerFrom2016Page)

    beSettable[Boolean](ScottishTaxpayerFrom2016Page)

    beRemovable[Boolean](ScottishTaxpayerFrom2016Page)

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
