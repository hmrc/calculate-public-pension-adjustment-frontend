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
import pages.behaviours.PageBehaviours

class ContributedToDuringRemedyPeriodPageSpec extends PageBehaviours {

  "ContributedToDuringRemedyPeriodPage" - {

    beRetrievable[Set[ContributedToDuringRemedyPeriod]](
      ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0))
    )

    beSettable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)))

    beRemovable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)))

    "must Navigate correctly in normal mode" - {

      "to page DefinedContributionAmountPage when DC selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, "/definedContributionAmount/2013/0")
      }

      "to page DefinedBenefitAmountPage when DB selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)),
            Set(ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, "/definedBenefitAmount/2013/0")
      }

      "to page DefinedContributionAmountPage when DB and DC selected" in {
        val ua     = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)),
            Set(
              ContributedToDuringRemedyPeriod.values.head,
              ContributedToDuringRemedyPeriod.values.tail.head
            )
          )
          .success
          .value
        val result = ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, "/definedContributionAmount/2013/0")
      }

      "to JourneyRecoveryPage when not answered" in {
        val ua     = emptyUserAnswers
        val result = ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must Navigate correctly to CYA in check mode" in {
      val ua     = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
      val result = ContributedToDuringRemedyPeriodPage(Period._2013, SchemeIndex(0)).navigate(CheckMode, ua).url

      checkNavigation(result, "/check-your-answers-period/2013")
    }
  }
}
