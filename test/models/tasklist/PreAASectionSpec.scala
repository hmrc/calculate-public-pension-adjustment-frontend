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

package models.tasklist

import base.SpecBase
import models.UserAnswers
import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.sections.PreAASection
import pages.behaviours.PageBehaviours

class PreAASectionSpec extends SpecBase with PageBehaviours {

  "Pre AA setup section navigation" - {

    "Must link to first page url when no section navigation has been saved" in {
      val navUrl = PreAASection.navigateTo(emptyUserAnswers)

      checkNavigation(navUrl, "/annual-allowance/scottish-taxpayer")
    }

    "Must link to check answers url when check answers url has been saved" in {
      val answers: UserAnswers =
        PreAASection.saveNavigation(emptyUserAnswers, PreAASection.checkYourAASetupAnswersPage.url)

      val navUrl = PreAASection.navigateTo(answers)

      checkNavigation(navUrl, "/annual-allowance/setup-check-answers")
    }

    "Must link to saved url when any other url has been saved" in {
      val answers: UserAnswers = PreAASection.saveNavigation(emptyUserAnswers, "/some-page-url")

      val navUrl = PreAASection.navigateTo(answers)

      checkNavigation(navUrl, "/some-page-url")
    }
  }

  "Pre AA section status" - {

    "Must be NotStarted when no section navigation has been saved" in {
      val answers: UserAnswers = emptyUserAnswers

      PreAASection.status(answers) mustBe NotStarted
    }

    "Must be InProgress when any other url has been saved" in {
      val answers: UserAnswers = PreAASection.saveNavigation(emptyUserAnswers, "/some-page-url")

      PreAASection.status(answers) mustBe InProgress
    }

    "Must be Completed when check answers url has been saved" in {
      val answers: UserAnswers =
        PreAASection.saveNavigation(emptyUserAnswers, PreAASection.checkYourAASetupAnswersPage.url)

      PreAASection.status(answers) mustBe Completed
    }
  }
}
