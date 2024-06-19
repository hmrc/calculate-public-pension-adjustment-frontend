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

class AreYouNonDomPageSpec extends PageBehaviours {

  "AreYouNonDomPage" - {

    beRetrievable[Boolean](AreYouNonDomPage(Period._2018))

    beSettable[Boolean](AreYouNonDomPage(Period._2018))

    beRemovable[Boolean](AreYouNonDomPage(Period._2018))

    "Normal Mode" - {

      "must redirect to HasReliefClaimedOnOverseasPension page when true" in {

        val userAnswers = emptyUserAnswers
          .set(AreYouNonDomPage(Period._2018), true)
          .success
          .value

        val result = AreYouNonDomPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/any-tax-relief-overseas-pension/2018")
      }

      "must redirect to DoYouKnowPersonalAllowance page when false" in {

        val userAnswers = emptyUserAnswers
          .set(AreYouNonDomPage(Period._2018), false)
          .success
          .value

        val result = AreYouNonDomPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/personal-allowance/2018")
      }

    }

    "Check mode" - {

      "must redirect to HasReliefClaimedOnOverseasPension page when true" in {

        val userAnswers = emptyUserAnswers
          .set(AreYouNonDomPage(Period._2018), true)
          .success
          .value

        val result = AreYouNonDomPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/change-any-tax-relief-overseas-pension/2018")
      }

      "must redirect to CYA page when false" in {

        val userAnswers = emptyUserAnswers
          .set(AreYouNonDomPage(Period._2018), false)
          .success
          .value

        val result = AreYouNonDomPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/check-answers")
      }
    }
  }
}
