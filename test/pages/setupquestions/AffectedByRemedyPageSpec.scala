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

class AffectedByRemedyPageSpec extends PageBehaviours {

  "AffectedByRemedyPage" - {

    beRetrievable[Boolean](AffectedByRemedyPage)

    beSettable[Boolean](AffectedByRemedyPage)

    beRemovable[Boolean](AffectedByRemedyPage)

    "Normal mode" - {

      "must redirect to aa/lta page when true" in {

        val ua = emptyUserAnswers
          .set(AffectedByRemedyPage, true)
          .success
          .value

        val nextPageUrl: String = AffectedByRemedyPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/triage-journey/AA-or-LTA-charges")
      }

      "must redirect to kick out page when false" in {

        val ua = emptyUserAnswers
          .set(AffectedByRemedyPage, false)
          .success
          .value

        val nextPageUrl: String = AffectedByRemedyPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/triage-journey-not-impacted")
      }

      "must redirect to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val nextPageUrl: String = AffectedByRemedyPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "must redirect to CYA when true" in {

        val ua = emptyUserAnswers
          .set(AffectedByRemedyPage, true)
          .success
          .value

        val nextPageUrl: String = AffectedByRemedyPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "must redirect to kick out page  when false" in {

        val ua = emptyUserAnswers
          .set(AffectedByRemedyPage, false)
          .success
          .value

        val nextPageUrl: String = AffectedByRemedyPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/triage-journey-not-impacted")
      }

      "must redirect to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val nextPageUrl: String = AffectedByRemedyPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
  }
}
