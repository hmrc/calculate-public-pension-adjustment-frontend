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

import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.behaviours.PageBehaviours

import scala.util.Random

class MemberMoreThanOnePensionPageSpec extends PageBehaviours {

  "MemberMoreThanOnePensionPage" - {

    beRetrievable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    beSettable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    beRemovable[Boolean](MemberMoreThanOnePensionPage(Period._2018))

    "normal mode navigation" - {

      "when current period is first relevant period then capture scheme details for current period, scheme index 0" in {
        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016), false).get

        val nextPageUrl: String =
          MemberMoreThanOnePensionPage(Period._2016).navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/annual-allowance/2016/scheme-name-reference/0")
      }

      "when current period is not first relevant period then select which scheme for current period, scheme index 0" in {
        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016), false).get

        val nextUrl: String =
          MemberMoreThanOnePensionPage(Period._2017).navigate(NormalMode, userAnswers).url

        checkNavigation(nextUrl, "/annual-allowance/2017/select-scheme-0")
      }
    }

    "check mode navigation" - {

      "must navigate to CYA page when submitted" in {
        val userAnswers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2016), false).get

        val nextUrl: String =
          MemberMoreThanOnePensionPage(Period._2017).navigate(CheckMode, userAnswers).url

        checkNavigation(nextUrl, "/annual-allowance/2017/select-scheme-0")
      }

      "must navigate to   ask for scheme detail page when change to 'yes'  with pension scheme" in {
        val userAnswers = emptyUserAnswers
          .set(MemberMoreThanOnePensionPage(Period._2016), true)
          .get
          .set(PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
          .get
          .set(DefinedContributionPensionSchemePage, true)
          .get

        val nextUrl: String =
          MemberMoreThanOnePensionPage(Period._2017).navigate(CheckMode, userAnswers).url

        checkNavigation(nextUrl, "/annual-allowance/2017/scheme-name-reference/0")
      }

      "must navigate to   ask for scheme detail page when change to 'no'  with pension scheme" in {
        val userAnswers = emptyUserAnswers
          .set(MemberMoreThanOnePensionPage(Period._2016), false)
          .get
          .set(PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
          .get
          .set(DefinedContributionPensionSchemePage, true)
          .get

        val nextUrl: String =
          MemberMoreThanOnePensionPage(Period._2017).navigate(CheckMode, userAnswers).url

        checkNavigation(nextUrl, "/annual-allowance/2017/scheme-name-reference/0")
      }
    }

    "Cleanup if more pension scheme change happen" - {

      "clean multiple scheme data if user changes between" in {

        val userAnswers = emptyUserAnswers
          .set(PensionSchemeDetailsPage(Period._2020, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
          .success
          .value
          .set(
            PensionSchemeDetailsPage(Period._2020, SchemeIndex(1)),
            PensionSchemeDetails("schemeName2", "schemeRef2")
          )
          .success
          .value

        val cleanedUserAnswers = MemberMoreThanOnePensionPage(Period._2020)
          .cleanup(Some(Random.nextBoolean()), userAnswers)
          .success
          .value

        cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020, SchemeIndex(1))) mustBe None

      }
    }
  }
}
