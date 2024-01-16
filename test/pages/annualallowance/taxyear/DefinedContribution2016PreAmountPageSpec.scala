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

class DefinedContribution2016PreAmountPageSpec extends PageBehaviours {

  "DefinedContribution2016PreAmountPage" - {

    beRetrievable[BigInt](DefinedContribution2016PreAmountPage)

    beSettable[BigInt](DefinedContribution2016PreAmountPage)

    beRemovable[BigInt](DefinedContribution2016PreAmountPage)
  }

  "Normal Mode" - {

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "must navigate to DCPost2016AmountPage when no flexi access" in {

      val ua = emptyUserAnswers
        .set(DefinedContribution2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-contribution")

    }

    "must navigate to DCPost2016FlexiAmountPage when flexi access" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016flexi-pre-pension-input-amount-defined-contribution")
    }

    "must navigate to DCPost2016AmountPage when flexi access last date of sub period" in {

      val ua = emptyUserAnswers
        .set(FlexibleAccessStartDatePage, Period.pre2016End)
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-contribution")
    }
  }

  "Check Mode" - {

    "no flexi access" - {

      "must navigate to journey recoery when no answer" in {

        val ua = emptyUserAnswers

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "must navigate to 2016Post DC Amount page when no answer for 2016Post DC Amount" in {

        val ua = emptyUserAnswers
          .set(DefinedContribution2016PreAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")
      }

      "must navigate to CYA when 2016Post DC page amount answered and DB indicated and 2016Pre DB answered" in {

        val ua = emptyUserAnswers
          .set(DefinedContribution2016PreAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")

      }

      "must navigate to 2016PreDB when 2016Post amount page answered and DB indicated and 2016Pre DB not answered" in {

        val ua = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
      }

      "must navigate to CYA when 2016Post amount page answered and DB not indicated" in {

        val ua = emptyUserAnswers
          .set(
            ContributedToDuringRemedyPeriodPage(Period._2016),
            Set(ContributedToDuringRemedyPeriod.values.head)
          )
          .success
          .value
          .set(DefinedContribution2016PreAmountPage, BigInt(1))
          .success
          .value
          .set(DefinedContribution2016PostAmountPage, BigInt(1))
          .success
          .value

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2016/check-answers")

      }
    }

    "flexi access" - {

      "regular flexi date" - {

        "must navigate to 2016Pre flexi amount page when no answer to 2016Pre flexi amount" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016flexi-pre-pension-input-amount-defined-contribution")

        }

        "must navigate to CYA when 2016Pre flexi answered and no DB indicated" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")

        }

        "must navigate to CYA when 2016Pre flexi answered, DB indicated and 2016Pre DB amount answered" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedBenefit2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")
        }

        "must navigate to 2016Pre DB amount page when 2016Pre flexi answered, DB indicated but no 2016Pre DB answered" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PreFlexiAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
        }
      }

      "flexi access date equal end of sub period date" - {

        "must navigate to 2016Post DC Amount page when no answer for 2016Post DC Amount" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, Period.pre2016End)
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")
        }

        "must navigate to CYA when 2016Post DC page amount answered and DB indicated and 2016Pre DB answered" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, Period.pre2016End)
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PostAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedBenefit2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")
        }

        "must navigate to 2016PreDB when 2016Post amount page answered and DB indicated and 2016Pre DB not answered" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, Period.pre2016End)
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PostAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
        }

        "must navigate to CYA when 2016Post amount page answered and DB not indicated" in {

          val ua = emptyUserAnswers
            .set(FlexibleAccessStartDatePage, Period.pre2016End)
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PostAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")

        }
      }
    }
  }
}
