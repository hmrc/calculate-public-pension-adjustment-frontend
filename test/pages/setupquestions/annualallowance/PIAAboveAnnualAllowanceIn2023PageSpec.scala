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

package pages.setupquestions.annualallowance

import models.{CheckMode, LTAKickOutStatus, NormalMode, Period}
import pages.annualallowance.taxyear.{BlindAllowancePage, BlindPersonsAllowanceAmountPage, PersonalAllowancePage}
import pages.behaviours.PageBehaviours

import scala.util.Random

class PIAAboveAnnualAllowanceIn2023PageSpec extends PageBehaviours {

  "PIAAboveAnnualAllowanceIn2023Page" - {

    beRetrievable[Boolean](PIAAboveAnnualAllowanceIn2023Page)

    beSettable[Boolean](PIAAboveAnnualAllowanceIn2023Page)

    beRemovable[Boolean](PIAAboveAnnualAllowanceIn2023Page)
  }

  "normal mode" - {

    "when yes is selected for page" - {

      "when lta kickout status 0 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to bce" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status any other number to recovery" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }

      "when lta kickout status None to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }
    }

    "when no is selected for page redirect to NetIncomeAbove190 " in {
      val userAnswers = emptyUserAnswers
        .set(PIAAboveAnnualAllowanceIn2023Page, false)
        .success
        .value

      val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-190-23")

    }

    "when nothing is selected for page redirect to recovery " in {
      val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "check mode" - {
    "when yes is selected for page" - {

      "when lta kickout status 0 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to bce" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status any other number to recovery" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }

      "when lta kickout status None to cya" in {
        val userAnswers = emptyUserAnswers
          .set(PIAAboveAnnualAllowanceIn2023Page, true)
          .success
          .value

        val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }
    }

    "when no is selected for page redirect to NetIncomeAbove190 " in {
      val userAnswers = emptyUserAnswers
        .set(PIAAboveAnnualAllowanceIn2023Page, false)
        .success
        .value

      val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-190-23")

    }

    "when nothing is selected for page redirect to recovery " in {
      val nextPageUrl: String = PIAAboveAnnualAllowanceIn2023Page.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = PIAAboveAnnualAllowanceIn2023Page
        .cleanup(Some(Random.nextBoolean()), userAnswersAATriage)
        .success
        .value

      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None
    }
  }
}
