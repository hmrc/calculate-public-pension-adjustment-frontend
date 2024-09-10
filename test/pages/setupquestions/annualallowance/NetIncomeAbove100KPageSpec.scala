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

import models.{CheckMode, LTAKickOutStatus, NormalMode}
import pages.behaviours.PageBehaviours

import scala.util.Random

class NetIncomeAbove100KPageSpec extends PageBehaviours {

  "NetIncomeAbove100KPage" - {

    beRetrievable[Boolean](NetIncomeAbove100KPage)

    beSettable[Boolean](NetIncomeAbove100KPage)

    beRemovable[Boolean](NetIncomeAbove100KPage)
  }

  "normal mode" - {

    "when yes and RPSS yes" - {

      "when lta kickout status 0 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "to net income above 190k when all else" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(NetIncomeAbove100KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-190-22")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = NetIncomeAbove100KPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "when yes and RPSS yes" - {

      "when lta kickout status 0 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove100KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "to net income above 190k when all else" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(NetIncomeAbove100KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/income-over-190-22")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = NetIncomeAbove100KPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = NetIncomeAbove100KPage
        .cleanup(Some(Random.nextBoolean()), userAnswersAATriage)
        .success
        .value

      cleanedUserAnswers.get(NetIncomeAbove190KPage) mustBe None
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
      cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None
    }
  }
}
