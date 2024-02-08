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
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DefinedContribution2016PreFlexiAmountPageSpec extends PageBehaviours {

  "DefinedContribution2016PreFlexiAmountPage" - {

    beRetrievable[BigInt](DefinedContribution2016PreFlexiAmountPage)

    beSettable[BigInt](DefinedContribution2016PreFlexiAmountPage)

    beRemovable[BigInt](DefinedContribution2016PreFlexiAmountPage)
  }

  "Normal mode" - {

    "stopped paying in sub period" - {

      "must navigate to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")

      }

      "must navigate to total income page when no DB indicated" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/total-income")

      }

      "must navigate to DB pre 2016 page when DB indicated" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

      }
    }

    "didn't stop paying in sub period" - {

      "must navigate to DC post 2016 page when answered" in {

        val ua = emptyUserAnswers
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-contribution")

      }
    }
  }

  "Check mode" - {

    "stopped paying in sub period" - {

      "must navigate to DB pre 2016 page if DB indicated and not answered" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
      }

      "must navigate to CYA if DB indicated and DB pre 2016 answered" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedBenefit2016PreAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")

      }

      "must navigate to CYA if DB not indicated" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")
      }
    }

    "didn't stop paying in sub period" - {

      "must navigate to DC post 2016 if not answered" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")
      }

      "must navigate to DB pre 2016 if DB indicated and not answered and DC post 2016 answered" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
      }

      "must navigate to CYA if DB indicated and DB pre 2016 already answered" in {

        val ua = emptyUserAnswers
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedBenefit2016PreAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreFlexiAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")

      }
    }
  }
}
