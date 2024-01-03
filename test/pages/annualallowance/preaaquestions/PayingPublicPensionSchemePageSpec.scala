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

import java.time.LocalDate

class PayingPublicPensionSchemePageSpec extends PageBehaviours {

  "PayingPublicPensionSchemePage" - {

    beRetrievable[Boolean](PayingPublicPensionSchemePage)

    beSettable[Boolean](PayingPublicPensionSchemePage)

    beRemovable[Boolean](PayingPublicPensionSchemePage)
  }

  "normal navigation" - {

    "next page should be defined contributions pension scheme page when true" in {
      val userAnswers = UserAnswers("1").set(PayingPublicPensionSchemePage, true).get

      val nextPageUrl = PayingPublicPensionSchemePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/defined-contributions-scheme")
    }

    "next page should be when did you stop paying public pension scheme page when true" in {
      val userAnswers = UserAnswers("1").set(PayingPublicPensionSchemePage, false).get

      val nextPageUrl = PayingPublicPensionSchemePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/stopped-paying-into-public-service-pension")
    }

    "must redirect to journey recovery when no answer" in {
      val nextPageUrl = PayingPublicPensionSchemePage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode navigation" - {

    "next page should be defined contributions pension scheme page when true" in {
      val userAnswers = UserAnswers("1").set(PayingPublicPensionSchemePage, true).get

      val nextPageUrl = PayingPublicPensionSchemePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
    }

    "next page should be when did you stop paying public pension scheme page when true" in {
      val userAnswers = UserAnswers("1").set(PayingPublicPensionSchemePage, false).get

      val nextPageUrl = PayingPublicPensionSchemePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/change-stopped-paying-into-public-service-pension")
    }

    "must redirect to journey recovery when no answer" in {
      val nextPageUrl = PayingPublicPensionSchemePage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "clean up" - {

    "must clean up correctly when user answers true" in {

      val userAnswers =
        emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2016, 1, 1))
          .success
          .value

      val cleanedAnswers: UserAnswers = PayingPublicPensionSchemePage.cleanup(Some(true), userAnswers).get

      cleanedAnswers.get(StopPayingPublicPensionPage) mustBe None
    }

    "must clean up correctly when user answers false" in {

      val userAnswers =
        emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2016, 1, 1))
          .success
          .value

      val cleanedAnswers: UserAnswers = PayingPublicPensionSchemePage.cleanup(Some(false), userAnswers).get

      cleanedAnswers.get(StopPayingPublicPensionPage) mustBe Some(LocalDate.of(2016, 1, 1))
    }
  }
}
