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

package pages

import models.Period._2021
import models.{CheckMode, MaybePIAIncrease, NormalMode}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import pages.behaviours.PageBehaviours
import pages.setupquestions.annualallowance._

class FlexibleAccessDcSchemePageSpec extends PageBehaviours {

  "FlexibleAccessDcSchemePage" - {

    beRetrievable[Boolean](FlexibleAccessDcSchemePage)

    beSettable[Boolean](FlexibleAccessDcSchemePage)

    beRemovable[Boolean](FlexibleAccessDcSchemePage)

    "normal mode" - {

      "to Contribution4000ToDirectContributionSchemeController when true" in {

        val userAnswers = emptyUserAnswers
          .set(FlexibleAccessDcSchemePage, true)
          .success
          .value

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/4000-contribution-to-defined-contribution-scheme")
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(FlexibleAccessDcSchemePage, false)
          .success
          .value

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "check mode" - {

      "to Contribution4000ToDirectContributionSchemeController when true" in {

        val userAnswers = emptyUserAnswers
          .set(FlexibleAccessDcSchemePage, true)
          .success
          .value

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/4000-contribution-to-defined-contribution-scheme")
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(FlexibleAccessDcSchemePage, false)
          .success
          .value

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String = FlexibleAccessDcSchemePage.navigate(CheckMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
  }

  "cleanup" - {

    "when user answers yes cleanup triage pages only" in {

      val cleanedUserAnswers = FlexibleAccessDcSchemePage
        .cleanup(Some(true), testCalulationServiceData)
        .success
        .value

      // AA Triage Answers
      cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
    }

    "when false cleanup triage page and AA tasks" in {

      val cleanedUserAnswers = FlexibleAccessDcSchemePage
        .cleanup(Some(false), testCalulationServiceData)
        .success
        .value

      // AA Triage Answers
      cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
      cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
      cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
      cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
      cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
      cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

      // AA Setup Answers
      cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None

      // AALoop Answers
      cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
    }
  }
}
