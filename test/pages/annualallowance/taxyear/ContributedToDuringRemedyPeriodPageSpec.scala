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

class ContributedToDuringRemedyPeriodPageSpec extends PageBehaviours {

  "ContributedToDuringRemedyPeriodPage" - {

    beRetrievable[Set[ContributedToDuringRemedyPeriod]](
      ContributedToDuringRemedyPeriodPage(Period._2013)
    )

    beSettable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013))

    beRemovable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "to page DefinedContributionAmountPage when DC selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/pension-input-amount-defined-contribution")
      }

      "to page DefinedBenefitAmountPage when DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013),
            Set(ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/pension-input-amount-defined-benefit")
      }

      "to page DefinedContributionAmountPage when DB and DC selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013),
            Set(
              ContributedToDuringRemedyPeriod.values.head,
              ContributedToDuringRemedyPeriod.values.tail.head
            )
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2013/pension-input-amount-defined-contribution")
      }

      "to JourneyRecoveryPage when not answered" in {
        val ua     = emptyUserAnswers
        val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must Navigate to DefinedContributionAmountPage in check mode when DC selected" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-pension-input-amount-defined-contribution")
    }

    "must Navigate to DefinedBenefitAmountPage in check mode when DB selected" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-pension-input-amount-defined-benefit")
    }

    "must Navigate to DefinedBenefitAmountPage in check mode when DB and DC selected and contribution amount answered" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-pension-input-amount-defined-benefit")
    }

    "must Navigate to DefinedBenefitAmountPage in check mode when DB and DC selected and benefit amount answered" in {
      val ua     = emptyUserAnswers
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
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-pension-input-amount-defined-contribution")
    }

    "must Navigate to CYA in check mode when DB and DC selected and benefit and contribution amount answered" in {
      val ua     = emptyUserAnswers
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
        .set(
          DefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "must Navigate to CYA in check mode when DC selected and contribution amount answered" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
        .set(
          DefinedContributionAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "must Navigate to CYA in check mode when DB selected and benefit amount answered" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013),
          Set(ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(
          DefinedBenefitAmountPage(Period._2013),
          BigInt("100")
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "to JourneyRecoveryPage when not answered in Check Mode" in {
      val ua     = emptyUserAnswers
      val result = ContributedToDuringRemedyPeriodPage(Period._2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
