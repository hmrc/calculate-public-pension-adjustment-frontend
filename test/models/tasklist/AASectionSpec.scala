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
import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.sections.AASection
import models.{Period, UserAnswers}
import pages.behaviours.PageBehaviours

class AASectionSpec extends SpecBase with PageBehaviours {

  "AA section navigation" - {

    "Must link to first page url in correct period when no section navigation has been saved" in {
      val navUrl = AASection(Period._2011).navigateTo(emptyUserAnswers)

      checkNavigation(navUrl, "/annual-allowance/2011/information")
    }

    "Must link to check answers url in correct period when check answers url has been saved in " in {
      val answers: UserAnswers =
        AASection(Period._2011)
          .saveNavigation(emptyUserAnswers, AASection(Period._2011).checkYourAAPeriodAnswersPage.url)

      val navUrl = AASection(Period._2011).navigateTo(answers)

      checkNavigation(navUrl, "/annual-allowance/2011/check-answers")
    }

    "Must link to saved url in correct period when any other url has been saved" in {
      val answers: UserAnswers = AASection(Period._2011).saveNavigation(emptyUserAnswers, "/some-page-url")

      val navUrl = AASection(Period._2011).navigateTo(answers)

      checkNavigation(navUrl, "/some-page-url")
    }

    "Must link to saved url in correct period when urls have been saved in multiple periods" in {
      val answers: UserAnswers            = AASection(Period._2011).saveNavigation(emptyUserAnswers, "/some-page-url")
      val answersWithNavInMultiplePeriods = AASection(Period._2012).saveNavigation(answers, "/some-other-page-url")

      val navUrl = AASection(Period._2011).navigateTo(answersWithNavInMultiplePeriods)

      checkNavigation(navUrl, "/some-page-url")
    }
  }

  "AA section status" - {

    "Must be NotStarted when no section navigation has been saved" in {
      val answers: UserAnswers = emptyUserAnswers

      AASection(Period._2011).status(answers) `mustBe` NotStarted
    }

    "Must be InProgress when any other url has been saved" in {
      val answers: UserAnswers = AASection(Period._2011).saveNavigation(emptyUserAnswers, "/some-page-url")

      AASection(Period._2011).status(answers) `mustBe` InProgress
    }

    "Must be Completed when check answers url for period has been saved" in {
      val answers: UserAnswers =
        AASection(Period._2011)
          .saveNavigation(emptyUserAnswers, AASection(Period._2011).checkYourAAPeriodAnswersPage.url)

      AASection(Period._2011).status(answers) `mustBe` Completed
    }
  }
}
