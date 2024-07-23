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

class DefinedContribution2016PostFlexiAmountPageSpec extends PageBehaviours {

  "DefinedContribution2016PostFlexiAmountPage" - {

    beRetrievable[BigInt](DefinedContribution2016PostFlexiAmountPage)

    beSettable[BigInt](DefinedContribution2016PostFlexiAmountPage)

    beRemovable[BigInt](DefinedContribution2016PostFlexiAmountPage)
  }

  "Normal Mode" - {

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "must redirect to total income page when DB not indicated" in {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/taxable-income")
    }

    "must redirect to preDB2016 page when DB indicated" in {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2016pre-pension-input-amount-defined-benefit")

    }
  }

  "Check Mode" - {

    "must redirect to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")

    }

    "must redirect to CYA when DB not indicated" in {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")
    }

    "must redirect to CYA when DB indicated and preDB2016 page answered" in {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value
        .set(DefinedBenefit2016PreAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2016/check-answers")
    }

    "must redirect to preDB2016 page when DB indicated and preDB2016 page not answered" in {

      val ua = emptyUserAnswers
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016),
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .success
        .value
        .set(DefinedContribution2016PostFlexiAmountPage, BigInt(1))
        .success
        .value

      val result = DefinedContribution2016PostFlexiAmountPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/change-2016pre-pension-input-amount-defined-benefit")

    }
  }
}
