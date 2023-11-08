/*
 * Copyright 2023 HM Revenue & Customs
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

import models.{CheckMode, NormalMode}

import java.time.LocalDate
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class StopPayingPublicPensionPageSpec extends PageBehaviours {

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
