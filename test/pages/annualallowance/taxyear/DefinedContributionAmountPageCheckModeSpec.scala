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

import models.{CheckMode, ContributedToDuringRemedyPeriod, Period}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.behaviours.PageBehaviours

class DefinedContributionAmountPageCheckModeSpec extends PageBehaviours {

  "DefinedContributionAmountPage" - {

    beRetrievable[BigInt](DefinedContributionAmountPage(Period._2013))

    beSettable[BigInt](DefinedContributionAmountPage(Period._2013))

    beRemovable[BigInt](DefinedContributionAmountPage(Period._2013))
  }

  "must Navigate correctly in check mode when not in flexi access period" - {

    "to DefinedBenefitAmountPage when DB required and DB amount does not exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end.plusMonths(1)

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(period),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/change-pension-input-amount-defined-benefit")
    }

    "to CYA when DB required and DB amount does exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end.plusMonths(1)

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(period),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value
        .set(
          DefinedBenefitAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/check-answers")
    }

    "to CYA when DB not required" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end.plusMonths(1)

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/check-answers")
    }

    "to JourneyRecovery when no answer given" in {
      val ua     = emptyUserAnswers
      val result = DefinedContributionAmountPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, s"/there-is-a-problem")
    }
  }

  "must Navigate correctly in check mode in flexi access period" - {

    "to FlexiAccessDefinedContributionAmountPage when flexi DC amount does not exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.start

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/change-flexible-pension-input-amount-defined-contribution")
    }

    "to CYA when flexi DC amount does exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.start

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value
        .set(
          FlexiAccessDefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/check-answers")
    }
  }

  "must Navigate correctly in check mode in flexi access period when flexi date is period end date" - {

    "to CYA when flexi DC amount does not exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/check-answers")
    }

    "to DefinedBenefitAmountPage when DB db is required and DB amount does not exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(period),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/change-pension-input-amount-defined-benefit")
    }

    "to CYA when DB is required and DB amount does exist" in {
      val period    = arbitraryPeriod.arbitrary.sample.value
      val flexiDate = period.end

      val ua = emptyUserAnswers
        .set(
          FlexibleAccessStartDatePage,
          flexiDate
        )
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(period),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(period),
          BigInt("100")
        )
        .success
        .value
        .set(
          DefinedBenefitAmountPage(period),
          BigInt("100")
        )
        .success
        .value

      val result = DefinedContributionAmountPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, s"/annual-allowance/$period/check-answers")
    }
  }
}
