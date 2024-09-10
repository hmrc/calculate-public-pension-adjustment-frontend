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

import models.{CheckMode, LTAKickOutStatus, NormalMode}
import pages.behaviours.PageBehaviours
import pages.setupquestions.annualallowance.Contribution4000ToDirectContributionSchemePage

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

          checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
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

          checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
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
  }
}
