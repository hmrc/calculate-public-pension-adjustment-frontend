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

import models.{CheckMode, LTAKickOutStatus, MaybePIAIncrease, NormalMode}
import pages.behaviours.PageBehaviours

class MaybePIAIncreasePageSpec extends PageBehaviours {

  "MaybePIAIncreasePage" - {

    beRetrievable[MaybePIAIncrease](MaybePIAIncreasePage)

    beSettable[MaybePIAIncrease](MaybePIAIncreasePage)

    beRemovable[MaybePIAIncrease](MaybePIAIncreasePage)
  }

  "normal mode" - {

    "when yes" - {

      "when lta kickout status 0 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
          .success
          .value
          .set(HadAAChargePage, true)
          .success
          .value

        val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "to PIAs unchanged/decreased when I do not know" in {

      val userAnswers = emptyUserAnswers
        .set(MaybePIAIncreasePage, MaybePIAIncrease.IDoNotKnow)
        .success
        .value

      val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-amount-decrease-or-no-change")
    }

    "to 22/23 PIA >40k when  no" in {

      val userAnswers = emptyUserAnswers
        .set(MaybePIAIncreasePage, MaybePIAIncrease.No)
        .success
        .value

      val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-above-annual-allowance-limit-22-23")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = MaybePIAIncreasePage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "to check your ansewrs when answered" in {

      val userAnswers = emptyUserAnswers
        .set(MaybePIAIncreasePage, MaybePIAIncrease.Yes)
        .success
        .value

      val nextPageUrl: String = MaybePIAIncreasePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = MaybePIAIncreasePage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}