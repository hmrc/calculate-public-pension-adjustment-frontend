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

package pages.annualallowance.preaaquestions

import models.{CheckMode, NormalMode, Period, UserAnswers}
import pages.annualallowance.preaaquestions
import pages.behaviours.PageBehaviours

class PIAPreRemedyPageSpec extends PageBehaviours {

  "PIAPreRemedyPage" - {

    beRetrievable[BigInt](PIAPreRemedyPage(Period._2013))

    beSettable[BigInt](preaaquestions.PIAPreRemedyPage(Period._2013))

    beRemovable[BigInt](preaaquestions.PIAPreRemedyPage(Period._2013))

    "normal mode navigation" - {

      "next page should be member of registered scheme capture for 2013-2014 when this page is for 2012-2013" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period._2013), BigInt(1)).get

        val nextPageUrl = preaaquestions.PIAPreRemedyPage(Period._2013).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/registered/2014")
      }

      "next page should be member of registered scheme capture for 2014-2015 when this page is for 2013-2014" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period._2014), BigInt(1)).get

        val nextPageUrl = preaaquestions.PIAPreRemedyPage(Period._2014).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/registered/2015")
      }

      "next page should be CheckYourAnswers when this page is for 2014-2015" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period._2015), BigInt(1)).get

        val nextPageUrl = preaaquestions.PIAPreRemedyPage(Period._2015).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
      }

      "next page should be journey recovery if tax year is before pre remedy window" in {
        val userAnswers =
          UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period.Year(2012)), BigInt(1)).get

        val nextPageUrl =
          preaaquestions.PIAPreRemedyPage(Period.Year(2010)).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }

      "next page should be JourneyRecovery if tax year is after pre remedy window" in {
        val userAnswers =
          UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period._2016PreAlignment), BigInt(1)).get

        val nextPageUrl =
          preaaquestions.PIAPreRemedyPage(Period._2016PreAlignment).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "check mode navigation" - {

      "next page should be CheckYourAnswers if the user answers contains data for the relevant year" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(Period._2013), BigInt(1)).get

        val nextPageUrl = preaaquestions.PIAPreRemedyPage(Period._2013).navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/setup-check-answers")
      }

      "next page should be JourneyRecovery if the user answers does not contain data for the relevant year" in {
        val userAnswers = UserAnswers("1")

        val nextPageUrl = preaaquestions.PIAPreRemedyPage(Period._2013).navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
  }
}
