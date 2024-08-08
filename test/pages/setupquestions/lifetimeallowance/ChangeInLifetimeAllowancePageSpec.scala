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

    "must navigate to change in tax charge page when user answers true" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-charge-change")
    }

    "must navigate to kickout when user answers false" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to change in tax charge page when user answers true" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-charge-change")
    }

    "must navigate to kickout when user answers false" in {

      val ua = emptyUserAnswers
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
