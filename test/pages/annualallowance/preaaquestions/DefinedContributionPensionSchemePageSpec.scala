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

import models.{CheckMode, NormalMode, UserAnswers}
import pages.behaviours.PageBehaviours
import play.api.mvc.Call

import java.time.LocalDate

class DefinedContributionPensionSchemePageSpec extends PageBehaviours {

  "DefinedContributionPensionSchemePage" - {

    beRetrievable[Boolean](DefinedContributionPensionSchemePage)

    beSettable[Boolean](DefinedContributionPensionSchemePage)

    beRemovable[Boolean](DefinedContributionPensionSchemePage)

    "normal mode navigation" - {

      "next page should be FlexiblyAccessedPensionPage when user has a DC pension" in {
        val userAnswers = UserAnswers("1").set(DefinedContributionPensionSchemePage, true).get

        val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/annual-allowance/flexibly-accessed")
      }

      "next page should be PayTaxCharge1415Page when user does not have a DC pension" in {
        val userAnswers = UserAnswers("1").set(DefinedContributionPensionSchemePage, false).get

        val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/annual-allowance/tax-charge-2014-2015")
      }
    }

    "check mode navigation" - {

      "next page should be FlexiblyAccessedPensionPage when user has a DC pension" in {
        val userAnswers = UserAnswers("1").set(DefinedContributionPensionSchemePage, true).get

        val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(CheckMode, userAnswers)

        check(nextPageUrl, "/annual-allowance/change-flexibly-accessed")
      }

      "next page should be PayTaxCharge1415Page when user does not have a DC pension" in {
        val userAnswers = UserAnswers("1").set(DefinedContributionPensionSchemePage, false).get

        val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(CheckMode, userAnswers)

        check(nextPageUrl, "/annual-allowance/setup-check-answers")
      }
    }

    def check(nextPageUrl: Call, expectedPath: String) =
      nextPageUrl.url.endsWith(expectedPath) must be(true)

    "must not remove dependent state when the answer is yes" in {

      val answers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .flatMap(a => a.set(FlexiblyAccessedPensionPage, true))
        .flatMap(a => a.set(FlexibleAccessStartDatePage, LocalDate.now))
        .success
        .value

      val result = answers.set(DefinedContributionPensionSchemePage, true).success.value

      result.get(DefinedContributionPensionSchemePage) must be(defined)
      result.get(FlexiblyAccessedPensionPage)          must be(defined)
      result.get(FlexibleAccessStartDatePage)          must be(defined)
    }

    "must remove dependent state when the answer is no" in {

      val answers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .flatMap(a => a.set(FlexiblyAccessedPensionPage, true))
        .flatMap(a => a.set(FlexibleAccessStartDatePage, LocalDate.now))
        .success
        .value

      val result = answers.set(DefinedContributionPensionSchemePage, false).success.value

      result.get(DefinedContributionPensionSchemePage) must be(defined)
      result.get(FlexiblyAccessedPensionPage)          must not be defined
      result.get(FlexibleAccessStartDatePage)          must not be defined
    }

    "must redirect to journey recovery page when no answer in normal mode" in {

      val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(NormalMode, emptyUserAnswers)

      check(nextPageUrl, "/there-is-a-problem")
    }

    "must redirect to journey recovery page when no answer in check mode" in {

      val nextPageUrl: Call = DefinedContributionPensionSchemePage.navigate(CheckMode, emptyUserAnswers)

      check(nextPageUrl, "/there-is-a-problem")
    }
  }
}
