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

import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class RegisteredYearPageSpec extends PageBehaviours {

  "RegisteredYearPage" - {

    beRetrievable[Boolean](RegisteredYearPage(Period._2011))

    beSettable[Boolean](RegisteredYearPage(Period._2011))

    beRemovable[Boolean](RegisteredYearPage(Period._2011))
  }

  "Normal Mode" - {

    "must return PIA page for associated period when user a registered scheme member" in {

      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2011), true)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2011).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/pension-input-amount/2011")
    }

    "must return registered year page for next period when user not a registered scheme member" in {

      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2011), false)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2011).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/registered/2012")
    }

    "for 14/15 must return AA setup CYA when user not a registered scheme member" in {
      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2015), false)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
    }

    "for 14/15 must return PIA page for associated year when user a registered scheme member" in {
      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2015), true)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/pension-input-amount/2015")
    }

    "must redirect to journey recovery when no answer" in {

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "CheckMode" - {

    "must return PIA page for associated period when user a registered scheme member" in {

      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2011), true)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2011).navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/change-pension-input-amount/2011")
    }

    "must return CYA page when user not a registered scheme member" in {

      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2011), false)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2011).navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
    }

    "for 14/15 must return AA setup CYA when user not a registered scheme member" in {
      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2015), false)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
    }

    "for 14/15 must return PIA page for associated year when user a registered scheme member" in {
      val userAnswers = emptyUserAnswers
        .set(RegisteredYearPage(Period._2015), true)
        .success
        .value

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/change-pension-input-amount/2015")
    }

    "must redirect to journey recovery when no answer" in {

      val nextPageUrl = RegisteredYearPage(Period._2015).navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup specific period answers only when user answers false" in {

      val userAnswers = emptyUserAnswers
        .set(PIAPreRemedyPage(Period._2011), BigInt(100))
        .success
        .value
        .set(PIAPreRemedyPage(Period._2012), BigInt(100))
        .success
        .value

      val cleanedUserAnswers = RegisteredYearPage(Period._2011).cleanup(Some(false), userAnswers).success.value

      cleanedUserAnswers.get(PIAPreRemedyPage(Period._2011)) mustBe None
      cleanedUserAnswers.get(PIAPreRemedyPage(Period._2012)) mustBe Some(BigInt(100))

    }

    "must cleanup specific period answers only when user answers true" in {

      val userAnswers = emptyUserAnswers
        .set(PIAPreRemedyPage(Period._2011), BigInt(100))
        .success
        .value
        .set(PIAPreRemedyPage(Period._2012), BigInt(100))
        .success
        .value

      val cleanedUserAnswers = RegisteredYearPage(Period._2011).cleanup(Some(true), userAnswers).success.value

      cleanedUserAnswers.get(PIAPreRemedyPage(Period._2011)) mustBe Some(BigInt(100))
      cleanedUserAnswers.get(PIAPreRemedyPage(Period._2012)) mustBe Some(BigInt(100))

    }
  }
}
