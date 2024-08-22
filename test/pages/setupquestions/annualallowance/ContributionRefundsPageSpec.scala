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

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

import scala.util.Random

class ContributionRefundsPageSpec extends PageBehaviours {

  "ContributionRefundsPage" - {

    beRetrievable[Boolean](ContributionRefundsPage)

    beSettable[Boolean](ContributionRefundsPage)

    beRemovable[Boolean](ContributionRefundsPage)
  }

  "normal mode" - {

    "to aa kickout when no and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/not-impacted-no-RPSS")
    }

    "to Net income above 100k 16/17 - 19/20 when yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, true)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/income-over-100")
    }

    "to have any PIAs increase 15/16 - 21/22 when no and RPSS yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-amount-increased")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = ContributionRefundsPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "to aa kickout when no and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/not-impacted-no-RPSS")
    }

    "to Net income above 100k 16/17 - 19/20 when yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(ContributionRefundsPage, true)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/income-over-100")
    }

    "to have any PIAs increase 15/16 - 21/22 when no and RPSS yes" in {
      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(ContributionRefundsPage, false)
        .success
        .value

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-amount-increased")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = ContributionRefundsPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = ContributionRefundsPage
        .cleanup(Some(Random.nextBoolean()), userAnswersAATriage)
        .success
        .value

      cleanedUserAnswers.get(NetIncomeAbove100KPage) mustBe None
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
