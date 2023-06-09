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

import models.{CheckMode, ContributedToDuringRemedyPeriod, NormalMode, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.behaviours.PageBehaviours

class DefinedContributionAmountPageSpec extends PageBehaviours {

  "DefinedContributionAmountPage" - {

    beRetrievable[BigInt](DefinedContributionAmountPage(Period._2013))

    beSettable[BigInt](DefinedContributionAmountPage(Period._2013))

    beRemovable[BigInt](DefinedContributionAmountPage(Period._2013))
  }

  "must Navigate correctly in normal mode" - {

    "to FlexiAccessDefinedContributionAmountPage when flexi date is for the same period and flexi access selected" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.start

      val ua     = emptyUserAnswers
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
      val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, s"/pia-for-dc-pension-flexible/$period")
    }

    "to DefinedBenefitAmountPage when flexi date answered but for a different period and DB selected" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end.plusMonths(1)

      val ua     = emptyUserAnswers
        .set(
          DefinedContributionAmountPage(period),
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
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
      val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, s"/pia-for-db-pension/$period")
    }

    "for pre 15-16" - {

      val period = Period._2016PreAlignment

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            period.start
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-dc-pension-flexible/$period")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-db-pension/$period")
      }

      "to CheckYourAnswersPage when answered and no flexi access selected and no DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/check-your-answers-period/$period")
      }

      "to CheckYourAnswersPage when flexi date answered but for a different period and no DB selected" in {
        val flexiDate = period.end.plusMonths(1)

        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            flexiDate
          )
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(period),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/check-your-answers-period/$period")
      }
    }

    "for post 15-16" - {

      val period = Period._2016PostAlignment

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            period.end
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-dc-pension-flexible/$period")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-db-pension/$period")
      }

      "to TotalIncomePage when answered and no flexi access selected and no DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/total-income/$period")
      }

      "to TotalIncomePage when flexi date answered but for a different period and no DB selected" in {
        val flexiDate = period.end.plusMonths(1)

        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            flexiDate
          )
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(period),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/total-income/$period")
      }
    }

    "for 16-17 onwards" - {

      val period = genPeriodNot2016.sample.value

      "to FlexiAccessDefinedContributionAmountPage when answered and flexi access selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            period.start
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-dc-pension-flexible/$period")
      }

      "to DefinedBenefitAmountPage when answered and no flexi access selected and DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/pia-for-db-pension/$period")
      }

      "to ThresholdIncome when answered and no flexi access selected and no DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
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
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/threshold-income/$period")
      }

      "to ThresholdIncome when flexi date answered but for a different period and no DB selected" in {
        val flexiDate = period.end.plusMonths(1)

        val ua     = emptyUserAnswers
          .set(
            DefinedContributionAmountPage(period),
            BigInt("100")
          )
          .success
          .value
          .set(
            FlexibleAccessStartDatePage,
            flexiDate
          )
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(period),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
        val result = DefinedContributionAmountPage(period).navigate(NormalMode, ua).url

        checkNavigation(result, s"/threshold-income/$period")
      }
    }

    "to JourneyRecovery when no answer given" in {
      val ua     = emptyUserAnswers
      val result = DefinedContributionAmountPage(Period._2013).navigate(NormalMode, ua).url

      checkNavigation(result, s"/there-is-a-problem")
    }
  }

  "must Navigate correctly to CYA in check mode" in {
    val ua     = emptyUserAnswers
      .set(
        DefinedContributionAmountPage(Period._2013),
        BigInt("100")
      )
      .success
      .value
    val result = DefinedContributionAmountPage(Period._2013).navigate(CheckMode, ua).url

    checkNavigation(result, "/check-your-answers-period/2013")
  }
}
