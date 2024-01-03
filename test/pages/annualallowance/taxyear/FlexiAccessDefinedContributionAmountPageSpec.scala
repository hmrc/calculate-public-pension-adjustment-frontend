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

package pages.annualallowance.taxyear

import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, Period}
import pages.behaviours.PageBehaviours

class FlexiAccessDefinedContributionAmountPageSpec extends PageBehaviours {

  "FlexiAccessDefinedContributionAmountPage" - {

    beRetrievable[BigInt](FlexiAccessDefinedContributionAmountPage(Period._2013))

    beSettable[BigInt](FlexiAccessDefinedContributionAmountPage(Period._2013))

    beRemovable[BigInt](FlexiAccessDefinedContributionAmountPage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "for pre 15-16" - {

        val period = Period._2016PreAlignment

        "to DefinedBenefitPage when DB is selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/pension-input-amount-defined-benefit")
        }

        "to TotalIncomePage when no DB selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/total-income")
        }
      }

      "for post 15-16" - {

        val period = Period._2016PostAlignment

        "to DefinedBenefitPage when DB is selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/pension-input-amount-defined-benefit")
        }

        "to CheckYourAnswersPage when no DB selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/check-answers")
        }
      }

      "for 16-17 onwards" - {

        val period = genPeriodNot2016.sample.value

        "to DefinedBenefitPage when DB is selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/pension-input-amount-defined-benefit")
        }

        "to ThresholdIncomePage when no DB selected" in {
          val ua     = emptyUserAnswers
            .set(
              FlexiAccessDefinedContributionAmountPage(period),
              BigInt("100")
            )
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(period),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
          val result = FlexiAccessDefinedContributionAmountPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/threshold-income")
        }
      }

      "to JourneyRecovery when no answer" in {
        val ua     = emptyUserAnswers
        val result = FlexiAccessDefinedContributionAmountPage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, s"/there-is-a-problem")
      }
    }
  }

  "must Navigate correctly in check mode" - {

    "to DefinedBenefitAmountPage when DB is required and DB amount does not exist" in {
      val ua = emptyUserAnswers
        .set(
          FlexiAccessDefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value

      val result = FlexiAccessDefinedContributionAmountPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-pension-input-amount-defined-benefit")
    }

    "to CYA when DB is not required" in {
      val ua = emptyUserAnswers
        .set(
          FlexiAccessDefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value

      val result = FlexiAccessDefinedContributionAmountPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "to CYA when DB is required and DB amount does exist" in {
      val ua = emptyUserAnswers
        .set(
          FlexiAccessDefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedBenefitAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value

      val result = FlexiAccessDefinedContributionAmountPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }
  }
}
