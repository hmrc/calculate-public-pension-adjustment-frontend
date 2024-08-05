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

package pages.lifetimeallowance

import models.{CheckMode, NormalMode}
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DateOfBenefitCrystallisationEventPageSpec extends PageBehaviours {

  "DateOfBenefitCrystallisationEventPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](DateOfBenefitCrystallisationEventPage)

    beSettable[LocalDate](DateOfBenefitCrystallisationEventPage)

    beRemovable[LocalDate](DateOfBenefitCrystallisationEventPage)
  }

  "Normal mode" - {

    "must navigate to LTA protections or enhancements page" in {

      val userAnswers =
        emptyUserAnswers
          .set(DateOfBenefitCrystallisationEventPage, LocalDate.of(2020, 1, 1))
          .get

      val nextPageUrl: String = DateOfBenefitCrystallisationEventPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/protection-enhancements")
    }
  }

  "Check mode" - {

    "must navigate to LTA CYA page " in {

      val userAnswers =
        emptyUserAnswers
          .set(DateOfBenefitCrystallisationEventPage, LocalDate.of(2020, 1, 1))
          .get

      val nextPageUrl: String = DateOfBenefitCrystallisationEventPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")

    }
  }
}
