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
import models.tasklist.sections.SetupSection
import pages.behaviours.PageBehaviours

class SetupSectionSpec extends SpecBase with PageBehaviours {

  "Setup section navigation" - {

    "Must link to first page url when no section navigation has been saved" in {
      val navUrl = SetupSection.navigateTo(emptyUserAnswers)

      checkNavigation(navUrl, "/change-previous-adjustment")
    }

    "Must link to check answers url when a kick out url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, SetupSection.ineligiblePage.url)

      val navUrl = SetupSection.navigateTo(answers)

      checkNavigation(navUrl, "/check-your-answers-setup")
    }

    "Must link to check answers url when a kickoutLTATriage url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, SetupSection.kickoutLTAService.url)

      val navUrl = SetupSection.navigateTo(answers)

      checkNavigation(navUrl, "/check-your-answers-setup")
    }

    "Must link to check answers url when a kickoutLTAService url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, SetupSection.kickoutLTATriage.url)

      val navUrl = SetupSection.navigateTo(answers)

      checkNavigation(navUrl, "/check-your-answers-setup")
    }

    "Must link to check answers url when check answers url has been saved" in {
      val answers: UserAnswers =
        SetupSection.saveNavigation(emptyUserAnswers, SetupSection.checkYourSetupAnswersPage.url)

      val navUrl = SetupSection.navigateTo(answers)

      checkNavigation(navUrl, "/check-your-answers-setup")
    }

    "Must link to saved url when any other url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, "/some-page-url")

      val navUrl = SetupSection.navigateTo(answers)

      checkNavigation(navUrl, "/some-page-url")
    }
  }

  "Setup section status" - {

    "Must be NotStarted when no section navigation has been saved" in {
      SetupSection.status(emptyUserAnswers) `mustBe` NotStarted
    }

    "Must be Completed when a kick out url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, SetupSection.ineligiblePage.url)

      SetupSection.status(answers) `mustBe` Completed
    }

    "Must be Completed when check answers url has been saved" in {
      val answers: UserAnswers =
        SetupSection.saveNavigation(emptyUserAnswers, SetupSection.checkYourSetupAnswersPage.url)

      SetupSection.status(answers) `mustBe` Completed
    }

    "Must be InProgress when any other url has been saved" in {
      val answers: UserAnswers = SetupSection.saveNavigation(emptyUserAnswers, "/some-page-url")

      SetupSection.status(answers) `mustBe` InProgress
    }

    "Must be Completed when NotAbleToUseThisServiceLtaController reached" in {
      val answers: UserAnswers =
        SetupSection.saveNavigation(emptyUserAnswers, SetupSection.kickoutLTAService.url)

      SetupSection.status(answers) `mustBe` Completed
    }

    "Must be Completed when NotAbleToUseThisTriageLtaController reached" in {
      val answers: UserAnswers =
        SetupSection.saveNavigation(emptyUserAnswers, SetupSection.kickoutLTATriage.url)

      SetupSection.status(answers) `mustBe` Completed
    }
  }
}
