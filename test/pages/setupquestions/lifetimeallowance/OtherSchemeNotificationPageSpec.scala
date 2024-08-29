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

class OtherSchemeNotificationPageSpec extends PageBehaviours {

  "OtherSchemeNotificationPage" - {

    beRetrievable[Boolean](OtherSchemeNotificationPage)

    beSettable[Boolean](OtherSchemeNotificationPage)

    beRemovable[Boolean](OtherSchemeNotificationPage)
  }

  "Normal mode" - {

    "must navigate to CYA page when user answers true for OtherSchemeNotificationPage and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(OtherSchemeNotificationPage, true)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to PensionSavingsStatement page when user answers true for OtherSchemeNotificationPage and the value for AAKickOutStatus is 1" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 1)

      val ua = uaWithAAKickOutStatus
        .set(OtherSchemeNotificationPage, true)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/pension-saving-statement")
    }

    "must navigate to CYA page when user answers true for OtherSchemeNotificationPage and the value for KickOutStatus is 2" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 2)

      val ua = uaWithAAKickOutStatus
        .set(OtherSchemeNotificationPage, true)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to CYA page when user answers true for OtherSchemeNotificationPage but nothing for AAKickOutStatus " in {

      val ua = emptyUserAnswers
        .set(OtherSchemeNotificationPage, true)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to Kickout page when user answers false for OtherSchemeNotificationPage" in {

      val ua = emptyUserAnswers
        .set(OtherSchemeNotificationPage, false)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to CYA page when user answers true for OtherSchemeNotificationPage and the value for AAKickOutStatus is 0" in {

      val uaWithAAKickOutStatus = AAKickOutStatus().saveAAKickOutStatus(emptyUserAnswers, 0)

      val ua = uaWithAAKickOutStatus
        .set(OtherSchemeNotificationPage, true)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must navigate to Kickout page when user answers false for OtherSchemeNotificationPage" in {

      val ua = emptyUserAnswers
        .set(OtherSchemeNotificationPage, false)
        .success
        .value

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/triage-journey-not-impacted-no-change")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = OtherSchemeNotificationPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
