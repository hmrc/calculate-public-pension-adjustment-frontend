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

class ClaimingTaxReliefPensionNotAdjustedIncomePageSpec extends PageBehaviours {

  "ClaimingTaxReliefPensionNotAdjustedIncomePage" - {

    beRetrievable[Boolean](ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018))

    beSettable[Boolean](ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018))

    beRemovable[Boolean](ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018))

    "Normal mode" - {

      "to HowMuchTaxReliefPension when period not 2016, when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018),
            true
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/how-much-tax-relief-pension")
      }

      "to HowMuchContribution when period not 2016, when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018),
            false
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/how-much-contribution")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "to HowMuchTaxReliefPension when period not 2016, when answered true" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018),
            true
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/change-how-much-tax-relief-pension")
      }

      "to CheckAnswers when period not 2016, when answered false" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018),
            false
          )
          .success
          .value
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/annual-allowance/2018/check-answers")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2018).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "must cleanup correctly" in {

        val period = Period._2022

        val cleanedUserAnswers = ClaimingTaxReliefPensionNotAdjustedIncomePage(Period._2022)
          .cleanup(Some(true), incomeSubJourneyData)
          .success
          .value

        cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None

      }
    }

  }
}
