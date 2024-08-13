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

class MultipleBenefitCrystallisationEventPageSpec extends PageBehaviours {

  "MultipleBenefitCrystallisationEventPage" - {

    beRetrievable[Boolean](MultipleBenefitCrystallisationEventPage)

    beSettable[Boolean](MultipleBenefitCrystallisationEventPage)

    beRemovable[Boolean](MultipleBenefitCrystallisationEventPage)
  }

  "normal mode navigation" - {

    "when user has multiple public sector BCE " in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers.set(page, true).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/other-scheme-LTA-notification")
    }

    "when user doesn't have multiple public sector BCE " in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers.set(page, false).get

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/cannot-use-triage-lta-service")
    }

    "when user hasn't answered multiple public sector BCE " in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has multiple public sector BCE in CheckMode" in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers.set(page, true).get

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-other-scheme-LTA-notification")
    }

    "when user has multiple public sector BCE in check mode" in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers.set(page, false).get

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/cannot-use-triage-lta-service")
    }

    "when user hasn't answered multiple public sector BCE in check mode " in {
      val page = MultipleBenefitCrystallisationEventPage

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
