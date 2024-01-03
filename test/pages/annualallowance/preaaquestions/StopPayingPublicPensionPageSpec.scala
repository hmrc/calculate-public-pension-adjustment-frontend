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

package pages.annualallowance.preaaquestions

import models.tasklist.sections.AASection
import models.{CheckMode, NormalMode, PensionSchemeInputAmounts, Period, SchemeIndex, SectionNavigation}
import org.scalacheck.Arbitrary
import org.scalatest.matchers.must.Matchers
import pages.annualallowance.taxyear.PensionSchemeInputAmountsPage
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class StopPayingPublicPensionPageSpec extends PageBehaviours with Matchers {

  "StopPayingPublicPensionPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(2015, 4, 6), LocalDate.of(2022, 4, 5))
    }

    beRetrievable[LocalDate](StopPayingPublicPensionPage)

    beSettable[LocalDate](StopPayingPublicPensionPage)

    beRemovable[LocalDate](StopPayingPublicPensionPage)
  }

  "Normal mode" - {

    "must navigate to defined contribution scheme page on submit" in {

      val userAnswers = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2020, 1, 1))
        .success
        .value

      val result = StopPayingPublicPensionPage.navigate(NormalMode, userAnswers).url

      checkNavigation(result, "/annual-allowance/defined-contributions-scheme")
    }
  }

  "Clean up" - {

    "must remove period answers and navigation state for periods that are not relevant" in {
      val inputs2019 = PensionSchemeInputAmounts(1, 2)
      val inputs2020 = PensionSchemeInputAmounts(3, 4)

      val userAnswersWithPeriods = emptyUserAnswers
        .set(PensionSchemeInputAmountsPage(Period._2019, SchemeIndex(0)), inputs2019)
        .get
        .set(PensionSchemeInputAmountsPage(Period._2020, SchemeIndex(0)), inputs2020)
        .get

      val answers2019 = AASection(Period._2019).saveNavigation(userAnswersWithPeriods, "/some-url-a")
      val answers2020 = AASection(Period._2020).saveNavigation(answers2019, "/some-url-b")

      val cleanedAnswers = answers2020.set(StopPayingPublicPensionPage, LocalDate.of(2018, 7, 1)).get

      cleanedAnswers.get(PensionSchemeInputAmountsPage(Period._2019, SchemeIndex(0))) mustBe Some(inputs2019)
      cleanedAnswers.get(SectionNavigation(s"aaSection${Period._2019}")) mustBe Some("/some-url-a")

      cleanedAnswers.get(PensionSchemeInputAmountsPage(Period._2020, SchemeIndex(0))) mustBe None
      cleanedAnswers.get(SectionNavigation(s"aaSection${Period._2020}")) mustBe None
    }
  }

  "Check mode" - {

    "must navigate to CYA page on submit" in {

      val userAnswers = emptyUserAnswers
        .set(StopPayingPublicPensionPage, LocalDate.of(2020, 1, 1))
        .success
        .value

      val result = StopPayingPublicPensionPage.navigate(CheckMode, userAnswers).url

      checkNavigation(result, "/annual-allowance/setup-check-answers")
    }
  }
}
