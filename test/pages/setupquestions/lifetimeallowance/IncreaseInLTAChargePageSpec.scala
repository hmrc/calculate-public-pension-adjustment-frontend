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

package pages.setupquestions.lifetimeallowance

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class IncreaseInLTAChargePageSpec extends PageBehaviours {

  "IncreaseInLTAChargePage" - {

    beRetrievable[Boolean](IncreaseInLTAChargePage)

    beSettable[Boolean](IncreaseInLTAChargePage)

    beRemovable[Boolean](IncreaseInLTAChargePage)
  }

  "Normal mode" - {

    "to New LTA charge page when user answers yes" in {

      val ua = emptyUserAnswers
        .set(IncreaseInLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance-new-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(IncreaseInLTAChargePage, false)
        .success
        .value

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-triage-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "Check mode" - {

    "to New LTA charge page when user answers true" in {

      val ua = emptyUserAnswers
        .set(IncreaseInLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/change-lifetime-allowance-new-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(IncreaseInLTAChargePage, false)
        .success
        .value

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-triage-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = IncreaseInLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }
}