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

import base.SpecBase
import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

import scala.util.Random

class SavingsStatementPageSpec extends PageBehaviours with SpecBase {

  "SavingsStatementPage" - {

    beRetrievable[Boolean](SavingsStatementPage)

    beSettable[Boolean](SavingsStatementPage)

    beRemovable[Boolean](SavingsStatementPage)
  }

  "Normal mode" - {

    "must redirect to pension protected member page when answered when yes" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/protected-member")
    }

    "must redirect to pension protected member page when answered when no" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/protected-member")
    }

    "must redirect to ResubmittingAdjustment page when user is authenticated and selected Yes on the page" in {

      val nextPageUrl: String = SavingsStatementPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must redirect to pension protected member page when true" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/protected-member")
    }

    "must redirect to CYA page when false" in {

      val ua = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/protected-member")
    }

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = SavingsStatementPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = SavingsStatementPage
        .cleanup(Some(Random.nextBoolean()), userAnswersAATriage)
        .success
        .value

      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe None
      cleanedUserAnswers.get(HadAAChargePage) mustBe None
      cleanedUserAnswers.get(ContributionRefundsPage) mustBe None
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
