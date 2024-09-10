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

import models.{AAKickOutStatus, CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

import scala.util.Random

class NewLTAChargePageSpec extends PageBehaviours {

  "NewLTAChargePage" - {

    beRetrievable[Boolean](NewLTAChargePage)

    beSettable[Boolean](NewLTAChargePage)

    beRemovable[Boolean](NewLTAChargePage)
  }

  "Normal mode" - {

    "must navigate to CYA page when user answers true for NewLTAChargePage and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(NewLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to PensionSavingsStatement page when user answers true for NewLTAChargePage and the value for AAKickOutStatus is 1" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 1)

      val ua = uaWithAAKickOutStatus
        .set(NewLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey/received-letter")
    }

    "must navigate to CYA page when user answers true for NewLTAChargePage and the value for KickOutStatus is 2" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 2)

      val ua = uaWithAAKickOutStatus
        .set(NewLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to CYA page when user answers true for NewLTAChargePage but nothing for AAKickOutStatus " in {

      val ua = emptyUserAnswers
        .set(NewLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to MultipleBenefitCrystallisationEvent page when user answers false for NewLTAChargePage" in {

      val ua = emptyUserAnswers
        .set(NewLTAChargePage, false)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = NewLTAChargePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to change in CYA page when user answers true for NewLTAChargePage and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(NewLTAChargePage, true)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to change in MultipleBenefitCrystallisationEvent page when user answers false for NewLTAChargePage" in {

      val ua = emptyUserAnswers
        .set(NewLTAChargePage, false)
        .success
        .value

      val nextPageUrl: String = NewLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = NewLTAChargePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = NewLTAChargePage
        .cleanup(Some(Random.nextBoolean()), userAnswersLTATriage)
        .success
        .value

      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe None
    }
  }
}
