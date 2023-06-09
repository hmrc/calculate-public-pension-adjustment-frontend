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

class PayTaxCharge1516PageSpec extends PageBehaviours {

  "PayTaxCharge1516Page" - {

    beRetrievable[Boolean](PayTaxCharge1516Page)

    beSettable[Boolean](PayTaxCharge1516Page)

    beRemovable[Boolean](PayTaxCharge1516Page)

    "normal navigation" - {

      "next page should be CheckYourAnswers when user paid a tax charge in 2015/2016" in {
        val userAnswers = UserAnswers("1").set(PayTaxCharge1516Page, true).get

        val nextPageUrl = PayTaxCharge1516Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-annual-allowance-setup")
      }

      "next page should be PIAPreRemedy for 2012 when user did not pay a tax charge in 2015/2016" in {
        val userAnswers = UserAnswers("1").set(PayTaxCharge1516Page, false).get

        val nextPageUrl = PayTaxCharge1516Page.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/pia-pre-remedy/2013")
      }
    }

    "check mode navigation" - {

      "next page should be CheckYourAnswers when user paid a tax charge in 2015/2016" in {
        val userAnswers = UserAnswers("1").set(PayTaxCharge1516Page, true).get

        val nextPageUrl = PayTaxCharge1516Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-answers-annual-allowance-setup")
      }

      "next page should be PIAPreRemedy for 2012 when user did not pay a tax charge in 2015/2016" in {
        val userAnswers = UserAnswers("1").set(PayTaxCharge1516Page, false).get

        val nextPageUrl = PayTaxCharge1516Page.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/pia-pre-remedy/2013")
      }
    }

    "clean up" - {

      "should remove answers related to PIAs when user paid a tax charge in 2015/2016" in {

        val userAnswers =
          UserAnswers("1")
            .set(PIAPreRemedyPage(Period._2013), BigInt(1))
            .flatMap(a => a.set(preaaquestions.PIAPreRemedyPage(Period._2014), BigInt(1)))
            .flatMap(a => a.set(preaaquestions.PIAPreRemedyPage(Period._2015), BigInt(1)))
            .get

        val cleanedAnswers: UserAnswers = PayTaxCharge1516Page.cleanup(Some(true), userAnswers).get

        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2013)) must be(None)
        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2014)) must be(None)
        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2015)) must be(None)
      }

      "should not remove answers related to PIAs when user did not pay a tax charge in 2015/2016" in {

        val userAnswers =
          UserAnswers("1")
            .set(preaaquestions.PIAPreRemedyPage(Period._2013), BigInt(1))
            .flatMap(a => a.set(preaaquestions.PIAPreRemedyPage(Period._2014), BigInt(1)))
            .flatMap(a => a.set(preaaquestions.PIAPreRemedyPage(Period._2015), BigInt(1)))
            .get

        val cleanedAnswers: UserAnswers = PayTaxCharge1516Page.cleanup(Some(false), userAnswers).get

        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2013)) must be(Some(1))
        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2014)) must be(Some(1))
        cleanedAnswers.get(preaaquestions.PIAPreRemedyPage(Period._2015)) must be(Some(1))
      }
    }
  }
}
