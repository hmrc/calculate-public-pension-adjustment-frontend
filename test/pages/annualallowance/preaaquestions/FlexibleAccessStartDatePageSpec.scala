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

import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours
import play.api.mvc.Call

import java.time.LocalDate

class FlexibleAccessStartDatePageSpec extends PageBehaviours {

  "FlexibleAccessStartDatePage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](FlexibleAccessStartDatePage)

    beSettable[LocalDate](FlexibleAccessStartDatePage)

    beRemovable[LocalDate](FlexibleAccessStartDatePage)

    val validDate = LocalDate.of(2020, 1, 1)

    "normal mode navigation" - {

      "next page should be PayTaxCharge1516Page when user has a DC pension" in {
        val userAnswers = UserAnswers("1").set(FlexibleAccessStartDatePage, validDate).get

        val nextPageUrl: Call = FlexibleAccessStartDatePage.navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/pay-tax-charge-from2015-2016")
      }
    }

    "check mode navigation" - {

      "next page should be CheckYourAnswersPage when user has a DC pension" in {
        val userAnswers = UserAnswers("1").set(FlexibleAccessStartDatePage, validDate).get

        val nextPageUrl: Call = FlexibleAccessStartDatePage.navigate(CheckMode, userAnswers)

        check(nextPageUrl, "/check-your-answers-annual-allowance-setup")
      }
    }

    def check(nextPageUrl: Call, expectedPath: String) =
      nextPageUrl.url.endsWith(expectedPath) must be(true)
  }
}
