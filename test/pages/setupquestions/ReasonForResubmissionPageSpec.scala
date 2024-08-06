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

class ReasonForResubmissionPageSpec extends PageBehaviours {

  "ReasonForResubmissionPage" - {

    beRetrievable[String](ReasonForResubmissionPage)

    beSettable[String](ReasonForResubmissionPage)

    beRemovable[String](ReasonForResubmissionPage)
  }

  "Normal mode" - {

    "must navigate to affected by remedy page" in {

      val userAnswers =
        emptyUserAnswers
          .set(ReasonForResubmissionPage, "Reason")
          .get

      val nextPageUrl: String = ReasonForResubmissionPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/affected-by-remedy")
    }
  }

  "Check mode" - {

    "must navigate new enhancement/protection page" in {

      val userAnswers =
        emptyUserAnswers
          .set(ReasonForResubmissionPage, "Reason")
          .get

      val nextPageUrl: String = ReasonForResubmissionPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-answers-setup")

    }
  }
}
