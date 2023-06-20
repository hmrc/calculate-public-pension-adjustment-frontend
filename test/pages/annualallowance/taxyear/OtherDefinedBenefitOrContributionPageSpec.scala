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

import pages.behaviours.PageBehaviours
import models.{CheckMode, NormalMode, Period, SchemeIndex}
import models.Period._2013

class OtherDefinedBenefitOrContributionPageSpec extends PageBehaviours {

  "OtherDefinedBenefitOrContributionPage" - {

    beRetrievable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)))

    beSettable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)))

    beRemovable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)))

    "must Navigate correctly in normal mode" - {

      "to page x when answered true" in {
        val ua = emptyUserAnswers.set(
          OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)), true
        ).success.value
        val result = OtherDefinedBenefitOrContributionPage(_2013, SchemeIndex(1)).navigate(NormalMode, ua).url

        checkNavigation(result, "/check-your-answers-period/2013")
      }

      "to page x when answered false" in {
        val ua = emptyUserAnswers.set(
          OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)), false
        ).success.value
        val result = OtherDefinedBenefitOrContributionPage(_2013, SchemeIndex(1)).navigate(NormalMode, ua).url

        checkNavigation(result, "/check-your-answers-period/2013")
      }
    }

    "must Navigate correctly in check mode" in {
      val ua = emptyUserAnswers.set(
        OtherDefinedBenefitOrContributionPage(Period._2013, SchemeIndex(0)), false
      ).success.value
      val result = OtherDefinedBenefitOrContributionPage(_2013, SchemeIndex(1)).navigate(CheckMode, ua).url

      checkNavigation(result, "/check-your-answers-period/2013")
    }
  }
}