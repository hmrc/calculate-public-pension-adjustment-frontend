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
import org.scalacheck.Gen
import org.scalacheck.rng.Seed.random
import pages.behaviours.PageBehaviours

import scala.util.Random

class PensionProtectedMemberPageSpec extends PageBehaviours {

  "PensionProtectedMemberPage" - {

    beRetrievable[Boolean](PensionProtectedMemberPage)

    beSettable[Boolean](PensionProtectedMemberPage)

    beRemovable[Boolean](PensionProtectedMemberPage)
  }

  "normal mode" - {

    "must go to AA Kickout when yes and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(PensionProtectedMemberPage, true)
        .success
        .value
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/not-impacted-no-RPSS")
    }

    "must go to 22/23 PIA > 40K page when yes and RPSS yes" in {

      val userAnswers = emptyUserAnswers
        .set(PensionProtectedMemberPage, true)
        .success
        .value
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-above-annual-allowance-limit-22-23")
    }

    "must go to AA charge when no" in {

      val randomBoolean = Random.nextBoolean()
      val userAnswer    = emptyUserAnswers
        .set(PensionProtectedMemberPage, false)
        .success
        .value
        .set(SavingsStatementPage, randomBoolean)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, userAnswer).url

      checkNavigation(nextPageUrl, "/annual-allowance-charge")
    }

    "must go to journey recovery when no answer" in {

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "when answered no must go to AA charge page" in {

      val userAnswer = emptyUserAnswers
        .set(PensionProtectedMemberPage, false)
        .success
        .value
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswer).url

      checkNavigation(nextPageUrl, "/annual-allowance-charge")

    }

    "must go to 22/23 PIA > 40K page when yes and RPSS yes" in {

      val userAnswers = emptyUserAnswers
        .set(PensionProtectedMemberPage, true)
        .success
        .value
        .set(SavingsStatementPage, true)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/PIA-above-annual-allowance-limit-22-23")

    }

    "must go to AA Kickout when yes and RPSS no" in {

      val userAnswers = emptyUserAnswers
        .set(PensionProtectedMemberPage, true)
        .success
        .value
        .set(SavingsStatementPage, false)
        .success
        .value

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/not-impacted-no-RPSS")
    }

    "when no answer must go to journey recovery" in {

      val nextPageUrl: String = PensionProtectedMemberPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = PensionProtectedMemberPage
        .cleanup(Some(Random.nextBoolean()), userAnswersAATriage)
        .success
        .value

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
