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

package pages.annualallowance.preaaquestions

import models.{CheckMode, NormalMode, WhichYearsScottishTaxpayer}
import pages.behaviours.PageBehaviours

class WhichYearsScottishTaxpayerPageSpec extends PageBehaviours {

  "WhichYearsScottishTaxpayerPage" - {

    beRetrievable[Set[WhichYearsScottishTaxpayer]](WhichYearsScottishTaxpayerPage)

    beSettable[Set[WhichYearsScottishTaxpayer]](WhichYearsScottishTaxpayerPage)

    beRemovable[Set[WhichYearsScottishTaxpayer]](WhichYearsScottishTaxpayerPage)
  }

  "Normal mode" - {

    "must redirect to Paying Public Pension Scheme page when set contains answer" in {

      val userAnswers = emptyUserAnswers
        .set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet)
        .success
        .value

      val result = WhichYearsScottishTaxpayerPage.navigate(NormalMode, userAnswers).url

      checkNavigation(result, "/annual-allowance/paying-into-public-service-pension")
    }

    "must redirect to journey recovery when set contains no answers" in {

      val result = WhichYearsScottishTaxpayerPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must redirect to CYA page when set contains answer" in {

      val userAnswers = emptyUserAnswers
        .set(WhichYearsScottishTaxpayerPage, WhichYearsScottishTaxpayer.values.toSet)
        .success
        .value

      val result = WhichYearsScottishTaxpayerPage.navigate(CheckMode, userAnswers).url

      checkNavigation(result, "/annual-allowance/setup-check-answers")
    }

    "must redirect to journey recovery when set contains no answers" in {

      val result = WhichYearsScottishTaxpayerPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
