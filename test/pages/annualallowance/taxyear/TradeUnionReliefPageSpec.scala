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

import models.{CheckMode, NormalMode, Period, UserAnswers}
import pages.behaviours.PageBehaviours

class TradeUnionReliefPageSpec extends PageBehaviours {

  val period = Period._2018

  "TradeUnionReliefPage" - {

    beRetrievable[Boolean](TradeUnionReliefPage(period))

    beSettable[Boolean](TradeUnionReliefPage(period))

    beRemovable[Boolean](TradeUnionReliefPage(period))

    "must Navigate correctly in normal mode" - {

      "must redirect to union police relief amount when true" in {

        val userAnswers = emptyUserAnswers
          .set(TradeUnionReliefPage(Period._2018), true)
          .success
          .value

        val result = TradeUnionReliefPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/union-police-relief-amount")
      }

      "must redirect to blind persons allowance page when false" in {

        val userAnswers = emptyUserAnswers
          .set(TradeUnionReliefPage(Period._2018), false)
          .success
          .value

        val result = TradeUnionReliefPage(Period._2018).navigate(NormalMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/blind-person-allowance")
      }

    }
    "must Navigate correctly in Check mode" - {

      "must redirect to union police relief amount in checkmode when true" in {

        val userAnswers = emptyUserAnswers
          .set(TradeUnionReliefPage(Period._2018), true)
          .success
          .value

        val result = TradeUnionReliefPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/change-union-police-relief-amount")
      }

      "must redirect to CYA when false" in {

        val userAnswers = emptyUserAnswers
          .set(TradeUnionReliefPage(Period._2018), false)
          .success
          .value

        val result = TradeUnionReliefPage(Period._2018).navigate(CheckMode, userAnswers).url

        checkNavigation(result, "/annual-allowance/2018/check-answers")
      }
    }
    "clean up" - {

      "must clean up correctly when user answers true" in {

        val userAnswers = emptyUserAnswers
          .set(UnionPoliceReliefAmountPage(Period._2018), BigInt(1))
          .success
          .value

        val cleanedAnswers: UserAnswers = TradeUnionReliefPage(period).cleanup(Some(true), userAnswers).get

        cleanedAnswers.get(UnionPoliceReliefAmountPage(period)) mustBe Some(1)
      }

      "must clean up correctly when user answers false" in {

        val userAnswers = emptyUserAnswers
          .set(UnionPoliceReliefAmountPage(Period._2018), BigInt(1))
          .success
          .value

        val cleanedAnswers: UserAnswers = TradeUnionReliefPage(period).cleanup(Some(false), userAnswers).get

        cleanedAnswers.get(UnionPoliceReliefAmountPage(period)) mustBe None
      }
    }

  }
}
