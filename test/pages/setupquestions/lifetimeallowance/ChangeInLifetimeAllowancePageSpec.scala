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

class ChangeInLifetimeAllowancePageSpec extends PageBehaviours {

  "ChangeInLifetimeAllowancePage" - {

    beRetrievable[Boolean](ChangeInLifetimeAllowancePage)

    beSettable[Boolean](ChangeInLifetimeAllowancePage)

    beRemovable[Boolean](ChangeInLifetimeAllowancePage)
  }

  "Normal mode" - {

    "must navigate to change in CYA page when user answers true for PreviousLTACharge and ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value
        .set(PreviousLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to change in CYA page when user answers false for PreviousLTACharge and true for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance-percentage-increase")
    }

    "must navigate to kickout when user answers false for PreviousLTACharge and ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to kickout when user answers true for PreviousLTACharge and false for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to change in CYA page when user answers true for PreviousLTACharge and ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value
        .set(PreviousLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to change in CYA page when user answers false for PreviousLTACharge and true for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance-percentage-increase")
    }

    "must navigate to kickout when user answers false for PreviousLTACharge and ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to kickout when user answers true for PreviousLTACharge and false for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
