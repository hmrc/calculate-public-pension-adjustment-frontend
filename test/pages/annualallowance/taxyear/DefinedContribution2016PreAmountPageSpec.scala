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
import pages.annualallowance.preaaquestions.{FlexibleAccessStartDatePage, StopPayingPublicPensionPage}
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class DefinedContribution2016PreAmountPageSpec extends PageBehaviours {

  "DefinedContribution2016PreAmountPage" - {

    beRetrievable[BigInt](DefinedContribution2016PreAmountPage)

    beSettable[BigInt](DefinedContribution2016PreAmountPage)

    beRemovable[BigInt](DefinedContribution2016PreAmountPage)
  }

  "Normal Mode" - {

    "no flexi access" - {

      "must navigate to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "stopped paying in 2016 pre sub period" - {

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
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

        }

        "must navigate to total income page when no DB indicated " in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/taxable-income")

        }
      }

      "didn't stop paying in 2016 sub period" - {

        "must navigate to 2016Post DC amount page" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-contribution")

        }
      }
    }

    "flexi access" - {

      "stopped paying in 2016 pre sub period" - {

        "must navigate to DC flexi pre 2016 when any other flexi access date" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 6, 1))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016flexi-pre-pension-input-amount-defined-contribution")

        }

        "must navigate to total income page when flexi access date == sub period end date & DB not indicated" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/taxable-income")
        }

        "must navigate to DB pre 2016 page when flexi access date == sub period end date & DB indicated" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
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

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")
        }
      }

      "didn't stop paying in 2016 pre sub period" - {

        "must navigate to DC post 2016 when flexi acess date == sub period end date" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
            .success
            .value
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016post-pension-input-amount-defined-contribution")

        }

        "must navigate to DC flexi pre 2016 when any other flexi access date" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
            .success
            .value
            .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016flexi-pre-pension-input-amount-defined-contribution")

        }
      }
    }
  }

  "Check mode" - {

    "no flexi access" - {

      "must navigate to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "stopped paying in 2016 pre sub period" - {

        "must navigate to CYA when no DB indicated" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")

        }

        "must navigate to CYA when DB indicated and DB pre 2016 answered" in {

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
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedBenefit2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")

        }

        "must navigate to DB pre 2016 page when DB indicated and DB pre 2016 page not answered" in {

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
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")

        }
      }

      "didn't stop paying in 2016 pre sub period" - {

        "must navigate to 2016 Post DC page when DC 2016 post not answered" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")

        }

        "must navigate to CYA when 2016 post DC page answered and DB not indicated" in {

          val ua = emptyUserAnswers
            .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
            .success
            .value
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
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

        "must navigate to CYA when 2016 post DC and DB pre 2016 page answered when DB indicated" in {

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

        "must navigate to DB pre 2016 page when 2016 post DC answered but DB pre 2016 not when DB indicated" in {

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
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedContribution2016PostAmountPage, BigInt(1))
            .success
            .value

          val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")

        }
      }
    }

    "flexi access" - {

      "stopped paying in 2016 pre sub period" - {

        "flexi access == sub period end date" - {

          "must navigate to CYA when DB not indicated" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
              .success
              .value
              .set(DefinedContribution2016PreAmountPage, BigInt(1))
              .success
              .value

            val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

            checkNavigation(result, "/annual-allowance/2016/check-answers")

          }

          "must navigate to CYA when DB indicated and 2016 pre DB answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
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
              .set(DefinedBenefit2016PreAmountPage, BigInt(1))
              .success
              .value

            val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

            checkNavigation(result, "/annual-allowance/2016/check-answers")

          }

          "must navigate to DB pre2016 when DB indicated but not answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
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

            checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")

          }
        }

        "any other flexi access date" - {

          "must navigate to 2016pre flexi amount page when 2016pre flexi amount not answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(DefinedContribution2016PreAmountPage, BigInt(1))
              .success
              .value

            val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

            checkNavigation(result, "/annual-allowance/change-2016flexi-pre-pension-input-amount-defined-contribution")

          }

          "must navigate to 2016pre DB amount page when 2016 pre flexi answered, DB pre2016 not answered and DB indicated" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
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

          "must navigate to CYA when 2016 pre flexi and DB pre 2016 answered when DB indicated" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
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
        }
      }

      "didn't stop paying in 2016 pre sub period" - {

        "flexi access == sub period end date" - {

          "must navigate to DC post 2016 when not previously answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
              .success
              .value
              .set(DefinedContribution2016PreAmountPage, BigInt(1))
              .success
              .value

            val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

            checkNavigation(result, "/annual-allowance/change-2016post-pension-input-amount-defined-contribution")

          }

          "must navigate to DB pre 2016 when DB indicated but not answered and DC post 2016 answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
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

          "must navigate to CYA when DB indicated and DB pre 2016/DC post 2016 answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 8))
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
        }

        "any other flexi acess date " - {

          "must navigate to DC flexi pre 2016 when not previously answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
              .success
              .value
              .set(DefinedContribution2016PreAmountPage, BigInt(1))
              .success
              .value

            val result = DefinedContribution2016PreAmountPage.navigate(CheckMode, ua).url

            checkNavigation(result, "/annual-allowance/change-2016flexi-pre-pension-input-amount-defined-contribution")

          }

          "must navigate to DB pre 2016 when DC flexi previously answered and DB indicated but not answered" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
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

          "must navigate to CYA when DC flexi and DB pre 2016 answered when DB indicated" in {

            val ua = emptyUserAnswers
              .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 30))
              .success
              .value
              .set(FlexibleAccessStartDatePage, LocalDate.of(2015, 7, 1))
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
        }
      }
    }
  }
}
