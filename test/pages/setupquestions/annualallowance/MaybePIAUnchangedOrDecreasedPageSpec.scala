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

import models.{CheckMode, LTAKickOutStatus, MaybePIAUnchangedOrDecreased, NormalMode}
import pages.behaviours.PageBehaviours

class MaybePIAUnchangedOrDecreasedPageSpec extends PageBehaviours {

  "MaybePIAUnchangedOrDecreasedPage" - {

    beRetrievable[MaybePIAUnchangedOrDecreased](MaybePIAUnchangedOrDecreasedPage)

    beSettable[MaybePIAUnchangedOrDecreased](MaybePIAUnchangedOrDecreasedPage)

    beRemovable[MaybePIAUnchangedOrDecreased](MaybePIAUnchangedOrDecreasedPage)
  }

  "normal mode" - {

    "when no or I do not know" - {

      "when lta kickout status 0 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.No)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.IDoNotKnow)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.IDoNotKnow)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.IDoNotKnow)
          .success
          .value

        val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.IDoNotKnow)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "when yes to 22/23 PIA >40k page" in {
      val userAnswers = emptyUserAnswers
        .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.Yes)
        .success
        .value

      val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-above-annual-allowance-limit-22-23")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "to check your ansewrs when answered" in {

      val userAnswers = emptyUserAnswers
        .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.IDoNotKnow)
        .success
        .value

      val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = MaybePIAUnchangedOrDecreasedPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
