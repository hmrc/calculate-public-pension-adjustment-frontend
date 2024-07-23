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

package pages.setupquestions

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class SavingsStatementPageSpec extends PageBehaviours {

  "SavingsStatementPage" - {

    beRetrievable[Boolean](SavingsStatementPage)

    beSettable[Boolean](SavingsStatementPage)

    beRemovable[Boolean](SavingsStatementPage)
  }

  "Normal mode" - {

    "must redirect to optional sign in when user is unauthenticated and selected Yes on the page" in {

      val ua = emptyUserAnswers
        .copy(authenticated = false)
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/sign-in-government-gateway")
    }

    "must redirect to ResubmittingAdjustment page when user is authenticated and selected Yes on the page" in {

      val ua = emptyUserAnswers
        .copy(authenticated = true)
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/change-previous-adjustment")
    }

    "must redirect to optional sign in when Yes selected on the page" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/sign-in-government-gateway")
    }

    "must redirect to ineligible page when No selected on the page" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-service")
    }

    "must redirect to journey recovery when no answer on the page" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must redirect to CYA page when true" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")
    }

    "must redirect to ineligible when false" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-service")
    }

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
