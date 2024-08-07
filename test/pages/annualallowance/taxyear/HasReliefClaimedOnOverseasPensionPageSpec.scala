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

import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class HasReliefClaimedOnOverseasPensionPageSpec extends PageBehaviours {

  "ReliefClaimedOnOverseasPensionPage" - {

    beRetrievable[Boolean](HasReliefClaimedOnOverseasPensionPage(Period._2018))

    beSettable[Boolean](HasReliefClaimedOnOverseasPensionPage(Period._2018))

    beRemovable[Boolean](HasReliefClaimedOnOverseasPensionPage(Period._2018))

    "Normal Mode" - {

      "must redirect to AmountClaimedOnOverseasPension page when true" in {

        val userAnswers = emptyUserAnswers
          .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), true)
          .success
          .value

        val result = HasReliefClaimedOnOverseasPensionPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/tax-relief-overseas-pension-value")
      }

      "must redirect to do you have gift aid page when false" in {

        val userAnswers = emptyUserAnswers
          .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), false)
          .success
          .value

        val result = HasReliefClaimedOnOverseasPensionPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/donated-via-gift-aid")
      }

    }

    "Check mode" - {

      "must redirect to AmountClaimedOnOverseasPension page when true" in {

        val userAnswers = emptyUserAnswers
          .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), true)
          .success
          .value

        val result = HasReliefClaimedOnOverseasPensionPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/change-tax-relief-overseas-pension-value")
      }

      "must redirect to CYA page when false" in {

        val userAnswers = emptyUserAnswers
          .set(HasReliefClaimedOnOverseasPensionPage(Period._2018), false)
          .success
          .value

        val result = HasReliefClaimedOnOverseasPensionPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/check-answers")
      }
    }
  }
}
