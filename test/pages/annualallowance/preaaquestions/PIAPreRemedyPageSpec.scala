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

import models.PIAPreRemedyTaxYear.{TaxYear2012, TaxYear2013, TaxYear2014}
import models.{CheckMode, NormalMode, PIAPreRemedyTaxYear, UserAnswers}
import pages.annualallowance.preaaquestions
import pages.behaviours.PageBehaviours
import play.api.mvc.Call

class PIAPreRemedyPageSpec extends PageBehaviours {

  "PIAPreRemedyPage" - {

    beRetrievable[BigInt](PIAPreRemedyPage(PIAPreRemedyTaxYear(1)))

    beSettable[BigInt](preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(1)))

    beRemovable[BigInt](preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(1)))

    "normal mode navigation" - {

      "next page should be PIAPreRemedy capture for 2013 when this page is for 2012" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(TaxYear2012), BigInt(1)).get

        val nextPageUrl: Call = preaaquestions.PIAPreRemedyPage(TaxYear2012).navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/pia-pre-remedy/2013-2014")
      }

      "next page should be PIAPreRemedy capture for 2014 when this page is for 2013" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(TaxYear2013), BigInt(1)).get

        val nextPageUrl: Call = preaaquestions.PIAPreRemedyPage(TaxYear2013).navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/pia-pre-remedy/2014-2015")
      }

      "next page should be CheckYourAnswers when this page is for 2014" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(TaxYear2014), BigInt(1)).get

        val nextPageUrl: Call = preaaquestions.PIAPreRemedyPage(TaxYear2014).navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/check-your-answers-annual-allowance-setup")
      }

      "next page should be journey recovery if tax year is before pre remedy window" in {
        val userAnswers =
          UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(2011)), BigInt(1)).get

        val nextPageUrl: Call =
          preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(2011)).navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/there-is-a-problem")
      }

      "next page should be JourneyRecovery if tax year is after pre remedy window" in {
        val userAnswers =
          UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(2015)), BigInt(1)).get

        val nextPageUrl: Call =
          preaaquestions.PIAPreRemedyPage(PIAPreRemedyTaxYear(2015)).navigate(NormalMode, userAnswers)

        check(nextPageUrl, "/there-is-a-problem")
      }
    }

    "check mode navigation" - {

      "next page should be CheckYourAnswers if the user answers contains data for the relevant year" in {
        val userAnswers = UserAnswers("1").set(preaaquestions.PIAPreRemedyPage(TaxYear2012), BigInt(1)).get

        val nextPageUrl: Call = preaaquestions.PIAPreRemedyPage(TaxYear2012).navigate(CheckMode, userAnswers)

        check(nextPageUrl, "/check-your-answers-annual-allowance-setup")
      }

      "next page should be JourneyRecovery if the user answers does not contain data for the relevant year" in {
        val userAnswers = UserAnswers("1")

        val nextPageUrl: Call = preaaquestions.PIAPreRemedyPage(TaxYear2012).navigate(CheckMode, userAnswers)

        check(nextPageUrl, "/there-is-a-problem")
      }
    }
  }

  private def check(nextPageUrl: Call, expectedPath: String) =
    nextPageUrl.url.endsWith(expectedPath) must be(true)
}
