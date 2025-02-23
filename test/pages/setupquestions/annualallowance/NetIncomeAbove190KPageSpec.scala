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

import models.Period._2021
import models.{CheckMode, LTAKickOutStatus, NormalMode}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import pages.behaviours.PageBehaviours

class NetIncomeAbove190KPageSpec extends PageBehaviours {

  "NetIncomeAbove190KPage" - {

    beRetrievable[Boolean](NetIncomeAbove190KPage)

    beSettable[Boolean](NetIncomeAbove190KPage)

    beRemovable[Boolean](NetIncomeAbove190KPage)
  }

  "normal mode" - {

    "when yes and RPSS yes" - {

      "when lta kickout status 0 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "to PIA increase 15/16 - 21/22 when false and RPSS true" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(NetIncomeAbove190KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-amount-increased")
    }

    "to aa kickout when anything else" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(NetIncomeAbove190KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(NormalMode, emptyUserAnswers).url

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
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 0)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when lta kickout status 1 to had BCE page" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 1)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
      }

      "when lta kickout status 2 to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 2)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when no LTA kickout status to cya" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "when LTA kickout status anything else to journey recovery" in {

        val userAnswers = emptyUserAnswers
          .set(SavingsStatementPage, true)
          .success
          .value
          .set(NetIncomeAbove190KPage, true)
          .success
          .value
          .set(LTAKickOutStatus(), 3)
          .success
          .value

        val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "to PIA increase 15/16 - 21/22 when false and RPSS true" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, true)
        .success
        .value
        .set(NetIncomeAbove190KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey/PIA-amount-increased")
    }

    "to aa kickout when anything else" in {

      val userAnswers = emptyUserAnswers
        .set(SavingsStatementPage, false)
        .success
        .value
        .set(NetIncomeAbove190KPage, false)
        .success
        .value

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/triage-journey-not-eligible-no-RPSS")
    }

    "to journey recovery when not answered" in {

      val nextPageUrl: String = NetIncomeAbove190KPage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when false" - {

      "when RPSS true, only cleanup triage pages" in {

        val cleanedUserAnswers = NetIncomeAbove190KPage
          .cleanup(Some(false), testCalulationServiceData)
          .success
          .value

        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
        cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)

      }

      "when RPSS false, cleanup triage and task pages" in {

        val userAnswers = testCalulationServiceData
          .set(SavingsStatementPage, false)
          .success
          .value

        val cleanedUserAnswers = NetIncomeAbove190KPage
          .cleanup(Some(false), userAnswers)
          .success
          .value

        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
        cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
      }
    }

    "when true" - {

      "when RPSS true, cleanup triage pages" in {

        val cleanedUserAnswers = NetIncomeAbove190KPage
          .cleanup(Some(true), testCalulationServiceData)
          .success
          .value

        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
        cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)

      }

      "when RPSS false, cleaup triage and task pages" in {

        val cleanedUserAnswers = NetIncomeAbove190KPage
          .cleanup(Some(true), testCalulationServiceData)
          .success
          .value

        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe None
        cleanedUserAnswers.get(MaybePIAUnchangedOrDecreasedPage) mustBe None
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe None
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe None
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe None
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe None

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)

      }
    }
  }
}
