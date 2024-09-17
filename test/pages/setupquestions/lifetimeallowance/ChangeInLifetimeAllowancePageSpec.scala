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
import pages.lifetimeallowance.DateOfBenefitCrystallisationEventPage

import scala.util.Random

class ChangeInLifetimeAllowancePageSpec extends PageBehaviours {

  "ChangeInLifetimeAllowancePage" - {

    beRetrievable[Boolean](ChangeInLifetimeAllowancePage)

    beSettable[Boolean](ChangeInLifetimeAllowancePage)

    beRemovable[Boolean](ChangeInLifetimeAllowancePage)
  }

  "Normal mode" - {

    "must navigate to CYA page when user answers true for PreviousLTACharge and ChangeInLTA and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to PensionSavingsStatement when user answers true for PreviousLTACharge,true for ChangeInLTA and the value for AAKickOutStatus is 1" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 1)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey/received-letter")
    }

    "must navigate to CYA page when user answers true for PreviousLTACharge and ChangeInLTA and the value for KickOutStatus is 2" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 2)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to IncreaseInLTACharge page when user answers false for PreviousLTACharge and true for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-percentage-increase")
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

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
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

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to CYA page when user answers true for PreviousLTACharge and false for ChangeInLTA but nothing for AAKickOutStatus " in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to change in CYA page when user answers true for PreviousLTACharge and ChangeInLTA and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to change in PensionSavingsStatement when user answers true for PreviousLTACharge, ChangeInLTA and the value for AAKickOutStatus is 1" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 1)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey/received-letter")
    }

    "must navigate to change in CYA page when user answers true for PreviousLTACharge and ChangeInLTA and the value for AAKickOutStatus is 2" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 2)

      val ua = uaWithAAKickOutStatus
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to change in IncreaseInLTACharge page when user answers false for PreviousLTACharge and true for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-percentage-increase")
    }

    "must navigate to kickout when user answers false for PreviousLTACharge and ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, false)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to kickout when user answers true for PreviousLTACharge and false for ChangeInLTA" in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, false)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to CYA page when user answers true for PreviousLTACharge and true for ChangeInLTA but nothing for AAKickOutStatus " in {

      val ua = emptyUserAnswers
        .set(PreviousLTAChargePage, true)
        .success
        .value
        .set(ChangeInLifetimeAllowancePage, true)
        .success
        .value

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = ChangeInLifetimeAllowancePage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = ChangeInLifetimeAllowancePage
        .cleanup(Some(Random.nextBoolean()), userAnswersLTATriage)
        .success
        .value

      cleanedUserAnswers.get(IncreaseInLTAChargePage) mustBe None
      cleanedUserAnswers.get(NewLTAChargePage) mustBe None
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe None
    }

    "when user answers no" in {

      val cleanedUserAnswers = ChangeInLifetimeAllowancePage
        .cleanup(Some(false), testCalulationServiceData)
        .success
        .value

      cleanedUserAnswers.get(NewLTAChargePage) mustBe None
      cleanedUserAnswers.get(DateOfBenefitCrystallisationEventPage) mustBe None
    }
  }
}
