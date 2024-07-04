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

class DoYouKnowPersonalAllowancePageSpec extends PageBehaviours {

  "DoYouKnowPersonalAllowancePage" - {

    beRetrievable[Boolean](DoYouKnowPersonalAllowancePage(Period._2018))

    beSettable[Boolean](DoYouKnowPersonalAllowancePage(Period._2018))

    beRemovable[Boolean](DoYouKnowPersonalAllowancePage(Period._2018))

    "Normal Mode" - {

      "must redirect to PersonalAllowanceController page when true" in {

        val userAnswers = emptyUserAnswers
          .set(DoYouKnowPersonalAllowancePage(Period._2018), true)
          .success
          .value

        val result = DoYouKnowPersonalAllowancePage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/total-income/personal-allowance")
      }

      "must redirect to do-you-have-code-adjustment page when false" in {

        val userAnswers = emptyUserAnswers
          .set(DoYouKnowPersonalAllowancePage(Period._2018), false)
          .success
          .value

        val result = DoYouKnowPersonalAllowancePage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/do-you-have-code-adjustment")
      }
    }

    "Check mode" - {

      "must redirect to PersonalAllowanceController page when true" in {

        val userAnswers = emptyUserAnswers
          .set(DoYouKnowPersonalAllowancePage(Period._2018), true)
          .success
          .value

        val result = DoYouKnowPersonalAllowancePage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/total-income/change-personal-allowance")
      }

      "must redirect to CYA page when false" in {

        val userAnswers = emptyUserAnswers
          .set(DoYouKnowPersonalAllowancePage(Period._2018), false)
          .success
          .value

        val result = DoYouKnowPersonalAllowancePage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/check-answers")
      }
    }

    "Clean up" - {
      "must clean up correctly when user doesn't know personal allowance" in {

        val ua = emptyUserAnswers.set(PersonalAllowancePage(Period._2018), BigInt("100")).success.value

        val cleanedUserAnswers = DoYouKnowPersonalAllowancePage(Period._2018)
          .cleanup(Some(false), ua)
          .success
          .value

        cleanedUserAnswers.get(PersonalAllowancePage(Period._2018)) mustBe None
      }
    }
  }
}
