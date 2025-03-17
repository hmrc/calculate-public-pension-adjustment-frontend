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
import models.{CheckMode, LTAKickOutStatus, MaybePIAIncrease, NormalMode}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import pages.behaviours.PageBehaviours
import pages.setupquestions.annualallowance.*

class Contribution4000ToDirectContributionSchemePageSpec extends PageBehaviours {

  "Contribution4000ToDirectContributionSchemePage" - {

    beRetrievable[Boolean](Contribution4000ToDirectContributionSchemePage)

    beSettable[Boolean](Contribution4000ToDirectContributionSchemePage)

    beRemovable[Boolean](Contribution4000ToDirectContributionSchemePage)

    "normal mode" - {

      "when yes" - {

        "when lta kickout status 0 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 0)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when lta kickout status 1 to had BCE page" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 1)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
        }

        "when lta kickout status 2 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when no LTA kickout status to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when LTA kickout status anything else to journey recovery" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 3)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/there-is-a-problem")
        }
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(Contribution4000ToDirectContributionSchemePage, false)
          .success
          .value

        val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String =
          Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
    "check mode" - {

      "when yes" - {

        "when lta kickout status 0 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 0)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when lta kickout status 1 to had BCE page" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 1)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/triage-journey/lifetime-allowance/benefit-crystallisation-event")
        }

        "when lta kickout status 2 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when no LTA kickout status to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when LTA kickout status anything else to journey recovery" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 3)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/there-is-a-problem")
        }
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(Contribution4000ToDirectContributionSchemePage, false)
          .success
          .value

        val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String =
          Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "when yes do not cleanup" in {

        val cleanedUserAnswers = Contribution4000ToDirectContributionSchemePage
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

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)

      }
      "when no clean up AA task data" in {

        val cleanedUserAnswers = Contribution4000ToDirectContributionSchemePage
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
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe Some(false)

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
      }
    }
  }
}
