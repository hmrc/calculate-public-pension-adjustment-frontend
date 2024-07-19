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
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DefinedContribution2016PostAmountPageSpec extends PageBehaviours {

  "DefinedContribution2016PostAmountPage" - {

    beRetrievable[BigInt](DefinedContribution2016PostAmountPage)

    beSettable[BigInt](DefinedContribution2016PostAmountPage)

    beRemovable[BigInt](DefinedContribution2016PostAmountPage)
  }

  "Normal Mode" - {

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")

    }

    "non-flexi scenarios" - {

      "must navigate to total income controller when answered and no defined benefit indicated" in {

        val ua = emptyUserAnswers
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/taxable-income")

      }

      "must navigate to defined benefit pre 2016 page when defined benefit indicated" in {

        val ua = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

      }

    }

    "flexi scenarios" - {

      "must navigate to 2016Post flexi amount page when flexi access date not end of sub-period date" in {

        val ua = emptyUserAnswers
          .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 1, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016flexi-post-pension-input-amount-defined-contribution")

      }

      "must navigate to total income controller when flexi date = sub-period end date and DB not indicated" in {

        val ua = emptyUserAnswers
          .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 4, 5))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/taxable-income")

      }

      "must navigate to DB2016pre amount page when flexi date = sub-period end date and DB indicated" in {

        val ua = emptyUserAnswers
          .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 4, 5))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

      }
    }
  }

  "Check Mode" - {

    "must navigate to journey recovery when no answer" in {
      val ua = emptyUserAnswers

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")

    }

    "non-flexi scenarios" - {

      "must navigate to CYA when no DB indicated" in {

        val ua = emptyUserAnswers
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")

      }

      "must navigate to CYA when DB indicated and DBpre2016 amount answered" in {

        val ua = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedBenefit2016PreAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")

      }

      "must navigate to DB pre2016 when DB indicated and DBpre2016 amount not answered" in {

        val ua = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
      }
    }

    "flexi scenarios" - {

      "must navigate to post2016 DC flexi amount page when not previously answered and when flexi access date not end of sub-period date " in {

        val ua = emptyUserAnswers
          .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 1, 1))
          .success
          .value
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016flexi-post-pension-input-amount-defined-contribution")
      }

    }

    "must navigate to CYA when post2016 DC flexi previously answered when flexi access date not end of sub-period date and DB not indicated" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 1, 1))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")

    }

    "must navigate to CYA when post2016 DC flexi previously answered when flexi access date not end of sub-period date and DB indicated and answered" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 1, 1))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(0))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")

    }

    "must navigate to CYA when post2016 DC flexi previously answered when flexi access date not end of sub-period date and DB indicated and not answered" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 1, 1))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(0))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
    }

    "must navigate to CYA when DB not indicated and when flexi date = sub-period end date" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 4, 5))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")

    }

    "must navigate to CYA when DB indicated, pre2016 DB amount answered and when flexi date = sub-period end date" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 4, 5))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(0))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")

    }

    "must navigate to pre2016DB amount when DB indicated, pre2016DB not answered and when flexi date = sub-period end date" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2016, 4, 5))
        .success
        .value
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")

    }
  }
}
