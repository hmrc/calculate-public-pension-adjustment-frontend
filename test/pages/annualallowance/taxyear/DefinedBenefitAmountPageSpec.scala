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

import models.{CheckMode, NormalMode, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class DefinedBenefitAmountPageSpec extends PageBehaviours {

  "DefinedBenefitAmountPage" - {

    beRetrievable[Int](DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)))

    beSettable[Int](DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)))

    beRemovable[Int](DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)))

    "must Navigate correctly in normal mode" - {

      "for pre 15-16" - {
        val period: Period = Period._2016PreAlignment

        "to CheckYourAnswersPage when answered" in {
          val ua = emptyUserAnswers
            .set(
              DefinedBenefitAmountPage(period, SchemeIndex(0)),
              100
            )
            .success
            .value
          val result = DefinedBenefitAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

          checkNavigation(result, s"/check-your-answers-period/$period")
        }
      }

      "for post 15-16" - {
        val period: Period = Period._2016PostAlignment

        "to TotalIncomePage when answered" in {
          val ua = emptyUserAnswers
            .set(
              DefinedBenefitAmountPage(period, SchemeIndex(0)),
              100
            )
            .success
            .value
          val result = DefinedBenefitAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

          checkNavigation(result, s"/totalIncome/$period/0")
        }
      }

      "for 16-17 onwards" - {
        val period: Period = genPeriodNot2016.sample.value

        "to ThresholdIncome when answered" in {
          val ua = emptyUserAnswers
            .set(
              DefinedBenefitAmountPage(period, SchemeIndex(0)),
              100
            )
            .success
            .value
          val result = DefinedBenefitAmountPage(period, SchemeIndex(0)).navigate(NormalMode, ua).url

          checkNavigation(result, s"/thresholdIncome/$period/0")
        }
      }

      "to JourneyRecoveryPage when not answerd" in {
        val ua = emptyUserAnswers
        val result = DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)).navigate(NormalMode, ua).url

        checkNavigation(result, s"/there-is-a-problem")
      }
    }

    "must Navigate correctly to CYA in check mode" in {
      val ua = emptyUserAnswers
        .set(
          DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)),
          100
        )
        .success
        .value
      val result = DefinedBenefitAmountPage(Period._2013, SchemeIndex(0)).navigate(CheckMode, ua).url

      checkNavigation(result, "/check-your-answers-period/2013")
    }
  }
}
