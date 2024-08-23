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

package pages.setupquestions.annualallowance

import models.{CheckMode, LTAKickOutStatus, NormalMode}
import pages.behaviours.PageBehaviours

class NetIncomeAbove190KIn2023PageSpec extends PageBehaviours {

  "NetIncomeAbove190KIn2023Page" - {

    beRetrievable[Boolean](NetIncomeAbove190KIn2023Page)

    beSettable[Boolean](NetIncomeAbove190KIn2023Page)

    beRemovable[Boolean](NetIncomeAbove190KIn2023Page)
  }

  "normal mode" - {

    "when yes is selected for page" - {

      "when lta kickout status 0 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to bce" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status any other number to recovery" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }

      "when lta kickout status None to cya" in {
        val userAnswers = emptyUserAnswers
          .set(NetIncomeAbove190KIn2023Page, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }
    }

    "when no is selected for page redirect to FlexibleAccessDcScheme " in {
      val userAnswers = emptyUserAnswers
        .set(NetIncomeAbove190KIn2023Page, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/flexible-access-dc-scheme")

    }

    "when nothing is selected for page redirect to recovery " in {
      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "check mode" - {
    "Redirect to cya" in {
      val nextPageUrl: String = NetIncomeAbove190KIn2023Page.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }
  }
}
