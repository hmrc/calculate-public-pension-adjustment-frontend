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

import models.Period._2013
import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class OtherDefinedBenefitOrContributionPageSpec extends PageBehaviours {

  "OtherDefinedBenefitOrContributionPage" - {

    beRetrievable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013))

    beSettable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013))

    beRemovable[Boolean](OtherDefinedBenefitOrContributionPage(Period._2013))

    "must Navigate correctly in normal mode" - {

      "for 15-16" - {

        val period = Period._2016

        "to ContributedToDuringRemedyPeriodPage when answered true" in {
          val ua     = emptyUserAnswers
            .set(
              OtherDefinedBenefitOrContributionPage(period),
              true
            )
            .success
            .value
          val result = OtherDefinedBenefitOrContributionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/contributed-to-dc-or-db-scheme")
        }

        "to CheckYourAnswersPage when answered false" in {
          val ua     = emptyUserAnswers
            .set(
              OtherDefinedBenefitOrContributionPage(period),
              false
            )
            .success
            .value
          val result = OtherDefinedBenefitOrContributionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, "/annual-allowance/2016/total-income")
        }
      }

      "for 16-17 onwards" - {
        val period: Period = genPeriodNot2016.sample.value

        "to ContributedToDuringRemedyPeriodPage when answered true" in {
          val ua     = emptyUserAnswers
            .set(
              OtherDefinedBenefitOrContributionPage(period),
              true
            )
            .success
            .value
          val result = OtherDefinedBenefitOrContributionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/contributed-to-dc-or-db-scheme")
        }

        "to ThresholdIncomePage when answered false" in {
          val ua     = emptyUserAnswers
            .set(
              OtherDefinedBenefitOrContributionPage(period),
              false
            )
            .success
            .value
          val result = OtherDefinedBenefitOrContributionPage(period).navigate(NormalMode, ua).url

          checkNavigation(result, s"/annual-allowance/$period/threshold-income")
        }
      }

      "to JourneyRecovery when not answered" in {
        val result = OtherDefinedBenefitOrContributionPage(
          _2013
        ).navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must Navigate correctly to CYA in check mode" in {
      val ua     = emptyUserAnswers
        .set(
          OtherDefinedBenefitOrContributionPage(Period._2013),
          false
        )
        .success
        .value
      val result = OtherDefinedBenefitOrContributionPage(_2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/check-answers")
    }

    "to JourneyRecovery when not answered in Check Mode" in {
      val result = OtherDefinedBenefitOrContributionPage(
        _2013
      ).navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "must Navigate correctly to ContributedToDuringRemedyPeriodPage in normal mode" in {
      val ua     = emptyUserAnswers
        .set(
          OtherDefinedBenefitOrContributionPage(Period._2013),
          true
        )
        .success
        .value
      val result = OtherDefinedBenefitOrContributionPage(_2013).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2013/change-contributed-to-dc-or-db-scheme")
    }
  }
}
