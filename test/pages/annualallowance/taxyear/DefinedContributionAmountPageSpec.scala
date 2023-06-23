/*
 * Copyright 2023 HM Revenue & Customs
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

import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DefinedContributionAmountPageSpec extends PageBehaviours {

  "DefinedContributionAmountPage" - {

    beRetrievable[Int](DefinedContributionAmountPage(Period._2013, SchemeIndex(0)))

    beSettable[Int](DefinedContributionAmountPage(Period._2013, SchemeIndex(0)))

    beRemovable[Int](DefinedContributionAmountPage(Period._2013, SchemeIndex(0)))
  }

  "must Navigate correctly in normal mode" - {

    "for pre 15-16" - {

      val period = Period._2016PreAlignment

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            FlexibleAccessStartDatePage,
            LocalDate.now()
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/flexiAccessDefinedContributionAmount/$period/0")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            ContributedToDuringRemedyPeriodPage(period, SchemeIndex(0)),
            Set(ContributedToDuringRemedyPeriod.values.tail.head)
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/definedBenefitAmount/$period/0")
      }

      "to CheckYourAnswersPage when answered and no flexi access selected and no DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/check-your-answers-period/$period")
      }
    }

    "for post 15-16" - {

      val period = Period._2016PostAlignment

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            FlexibleAccessStartDatePage,
            LocalDate.now()
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/flexiAccessDefinedContributionAmount/$period/0")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            ContributedToDuringRemedyPeriodPage(period, SchemeIndex(0)),
            Set(ContributedToDuringRemedyPeriod.values.tail.head)
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/definedBenefitAmount/$period/0")
      }

      "to TotalIncomePage when answered and no flexi access selected and no DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/totalIncome/$period/0")
      }
    }

    "for 16-17 onwards" - {

      val period = genPeriodNot2016.sample.value

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            FlexibleAccessStartDatePage,
            LocalDate.now()
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/flexiAccessDefinedContributionAmount/$period/0")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          ).success.value
          .set(
            ContributedToDuringRemedyPeriodPage(period, SchemeIndex(0)),
            Set(ContributedToDuringRemedyPeriod.values.tail.head)
          ).success.value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/definedBenefitAmount/$period/0")
      }

      "to ThresholdIncome when answered and no flexi access selected and no DB selected" in {
        val ua = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period, SchemeIndex(0)),
            100
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/thresholdIncome/$period/0")
      }
    }

    "to JourneyRecovery when no answer given" in {
      val ua = emptyUserAnswers
      val result = DefinedContributionAmountPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

      checkNavigation(result, s"/there-is-a-problem")
    }
  }

  "must Navigate correctly to CYA in check mode" in {
    val ua = emptyUserAnswers
      .set(
        DefinedContributionAmountPage(Period._2013, SchemeIndex(0)),
        100
      )
      .success
      .value
    val result = DefinedContributionAmountPage(Period._2013, SchemeIndex(0)).navigate(CheckMode, ua).url

    checkNavigation(result, "/check-your-answers-period/2013")
  }
}
