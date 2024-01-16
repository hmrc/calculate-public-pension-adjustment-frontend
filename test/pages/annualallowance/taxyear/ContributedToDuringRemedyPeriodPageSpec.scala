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

    beRetrievable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013))

    beSettable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013))

    beRemovable[Set[ContributedToDuringRemedyPeriod]](ContributedToDuringRemedyPeriodPage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "for 15/16 period" - {

        "must navigate to 2016PreDefinedBenefitAmountPage when only DB selected" in {
          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

        }

        "must navigate to 2016PreDefinedContributionAmountPage when only DC selected" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-contribution")

        }

        "must navigate to 2016PreDefinedContributionAmountPage when both DC and DB selected" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-contribution")
        }
      }

      "for 16/17 onwards" - {

        "to page DefinedContributionAmountPage when DC selected" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/pension-input-amount-defined-contribution")
        }

        "to page DefinedBenefitAmountPage when DB selected" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/pension-input-amount-defined-benefit")
        }

        "to page DefinedContributionAmountPage when DB and DC selected" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(
                ContributedToDuringRemedyPeriod.values.head,
                ContributedToDuringRemedyPeriod.values.tail.head
              )
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/pension-input-amount-defined-contribution")
        }

        "to JourneyRecoveryPage when not answered" in {
          val ua     = emptyUserAnswers
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(NormalMode, ua).url

          checkNavigation(result, "/there-is-a-problem")
        }
      }
    }

    "must navigate correctly in CheckMode" - {

      "for 15/16" - {
        "must navigate to 2016pre DB amount page when only DB selected and no DB amount answered" in {

          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
        }

        "must navigate to CYA when only DB selected and DB amount already answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(
              DefinedBenefit2016PreAmountPage,
              BigInt(1)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")
        }

        "must navigate to 2016preDC Amount page when DC only selected no existing answer for 2016preDC Amount page" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-contribution")
        }

        "must navigate to CYA page when DC only selected an no existing answer for 2016preDC Amount page" in {
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

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")
        }

        "must navigate to 2016preDC Amount page when both DC and DB are selected and no previous answer for either" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(
                ContributedToDuringRemedyPeriod.values.head,
                ContributedToDuringRemedyPeriod.values.tail.head
              )
            )
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-contribution")

        }

        "must navigate to 2016preDC Amount Page when both DC and DB are selected with DB answered and DC not answered" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(
                ContributedToDuringRemedyPeriod.values.head,
                ContributedToDuringRemedyPeriod.values.tail.head
              )
            )
            .success
            .value
            .set(DefinedBenefit2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-contribution")

        }

        "must navigate to 2016preDB Amount Page when both DC and DB are selected with DC answered and DB not answered" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(
                ContributedToDuringRemedyPeriod.values.head,
                ContributedToDuringRemedyPeriod.values.tail.head
              )
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")
        }

        "must navigate to CYA page when both DC and DB are selected with existing answers for both" in {

          val ua = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2016),
              Set(
                ContributedToDuringRemedyPeriod.values.head,
                ContributedToDuringRemedyPeriod.values.tail.head
              )
            )
            .success
            .value
            .set(DefinedContribution2016PreAmountPage, BigInt(1))
            .success
            .value
            .set(DefinedBenefit2016PreAmountPage, BigInt(1))
            .success
            .value

          val result = ContributedToDuringRemedyPeriodPage(Period._2016).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/check-answers")
        }
      }

      "for 16/17 onwards" - {

        "must Navigate to DefinedContributionAmountPage in check mode when DC selected" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/change-pension-input-amount-defined-contribution")
        }

        "must Navigate to DefinedBenefitAmountPage in check mode when DB selected" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/change-pension-input-amount-defined-benefit")
        }

        "must Navigate to DefinedBenefitAmountPage in check mode when DB and DC selected and contribution amount answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(
              DefinedContributionAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/change-pension-input-amount-defined-benefit")
        }

        "must Navigate to DefinedBenefitAmountPage in check mode when DB and DC selected and benefit amount answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(
              DefinedBenefitAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/change-pension-input-amount-defined-contribution")
        }

        "must Navigate to CYA in check mode when DB and DC selected and benefit and contribution amount answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(
              DefinedBenefitAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
            .set(
              DefinedContributionAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/check-answers")
        }

        "must Navigate to CYA in check mode when DC selected and contribution amount answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.head)
            )
            .success
            .value
            .set(
              DefinedContributionAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/check-answers")
        }

        "must Navigate to CYA in check mode when DB selected and benefit amount answered" in {
          val ua     = emptyUserAnswers
            .set(
              ContributedToDuringRemedyPeriodPage(Period._2017),
              Set(ContributedToDuringRemedyPeriod.values.tail.head)
            )
            .success
            .value
            .set(
              DefinedBenefitAmountPage(Period._2017),
              BigInt("100")
            )
            .success
            .value
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/annual-allowance/2017/check-answers")
        }

        "to JourneyRecoveryPage when not answered in Check Mode" in {
          val ua     = emptyUserAnswers
          val result = ContributedToDuringRemedyPeriodPage(Period._2017).navigate(CheckMode, ua).url

          checkNavigation(result, "/there-is-a-problem")
        }
      }
    }
  }

  "cleanup" - {

    "15/16" - {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedBenefit2016PostAmountPage, BigInt(2))
        .success
        .value
        .set(DefinedContribution2016PreAmountPage, BigInt(3))
        .success
        .value
        .set(DefinedContribution2016PreFlexiAmountPage, BigInt(4))
        .success
        .value
        .set(DefinedContribution2016PostAmountPage, BigInt(5))
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(6))
        .success
        .value

      "must clean up correctly when user deselects DB and DC remains" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2016)
          .cleanup(Some(Set(ContributedToDuringRemedyPeriod.values.head)), ua)
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefit2016PreAmountPage) mustBe None
        cleanedUserAnswers.get(DefinedBenefit2016PostAmountPage) mustBe None
        cleanedUserAnswers.get(DefinedContribution2016PreAmountPage) mustBe Some(BigInt(3))
        cleanedUserAnswers.get(DefinedContribution2016PreFlexiAmountPage) mustBe Some(BigInt(4))
        cleanedUserAnswers.get(DefinedContribution2016PostAmountPage) mustBe Some(BigInt(5))
        cleanedUserAnswers.get(DefinedContribution2016PostFlexiAmountPage) mustBe Some(BigInt(6))

      }

      "must clean up correctly when user deselects DC and DB remains" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2016)
          .cleanup(Some(Set(ContributedToDuringRemedyPeriod.values.tail.head)), ua)
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefit2016PreAmountPage) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DefinedBenefit2016PostAmountPage) mustBe Some(BigInt(2))
        cleanedUserAnswers.get(DefinedContribution2016PreAmountPage) mustBe None
        cleanedUserAnswers.get(DefinedContribution2016PreFlexiAmountPage) mustBe None
        cleanedUserAnswers.get(DefinedContribution2016PostAmountPage) mustBe None
        cleanedUserAnswers.get(DefinedContribution2016PostFlexiAmountPage) mustBe None

      }

      "must clean up correctly when both DC and DB selected" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2016)
          .cleanup(
            Some(Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)),
            ua
          )
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefit2016PreAmountPage) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DefinedBenefit2016PostAmountPage) mustBe Some(BigInt(2))
        cleanedUserAnswers.get(DefinedContribution2016PreAmountPage) mustBe Some(BigInt(3))
        cleanedUserAnswers.get(DefinedContribution2016PreFlexiAmountPage) mustBe Some(BigInt(4))
        cleanedUserAnswers.get(DefinedContribution2016PostAmountPage) mustBe Some(BigInt(5))
        cleanedUserAnswers.get(DefinedContribution2016PostFlexiAmountPage) mustBe Some(BigInt(6))

      }
    }

    "16/17 onwards" - {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2017),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedBenefitAmountPage(Period._2017), BigInt(1))
        .success
        .value
        .set(DefinedContributionAmountPage(Period._2017), BigInt(2))
        .success
        .value
        .set(FlexiAccessDefinedContributionAmountPage(Period._2017), BigInt(3))
        .success
        .value

      "must clean up correctly when user deselects DB and DC remains" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2017)
          .cleanup(Some(Set(ContributedToDuringRemedyPeriod.values.head)), ua)
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefitAmountPage(Period._2017)) mustBe None
        cleanedUserAnswers.get(DefinedContributionAmountPage(Period._2017)) mustBe Some(BigInt(2))
        cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(Period._2017)) mustBe Some(BigInt(3))
      }

      "must clean up correctly when user deselects DC and DB remains" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2017)
          .cleanup(Some(Set(ContributedToDuringRemedyPeriod.values.tail.head)), ua)
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefitAmountPage(Period._2017)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DefinedContributionAmountPage(Period._2017)) mustBe None
        cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(Period._2017)) mustBe None
      }

      "must clean up correctly when both DC and DB selected" in {

        val cleanedUserAnswers = ContributedToDuringRemedyPeriodPage(Period._2017)
          .cleanup(
            Some(Set(ContributedToDuringRemedyPeriod.values.tail.head, ContributedToDuringRemedyPeriod.values.head)),
            ua
          )
          .success
          .value

        cleanedUserAnswers.get(DefinedBenefitAmountPage(Period._2017)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DefinedContributionAmountPage(Period._2017)) mustBe Some(BigInt(2))
        cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(Period._2017)) mustBe Some(BigInt(3))
      }
    }
  }
}
