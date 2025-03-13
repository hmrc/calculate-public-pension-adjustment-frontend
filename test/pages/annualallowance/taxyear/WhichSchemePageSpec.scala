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

import models.{CheckMode, NormalMode, PSTR, PensionSchemeDetails, Period, SchemeIndex}
import pages.behaviours.PageBehaviours

class WhichSchemePageSpec extends PageBehaviours {

  "WhichSchemePage" - {

    beRetrievable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))

    beSettable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))

    beRemovable[String](WhichSchemePage(Period._2018, SchemeIndex(0)))
  }

  "normal mode navigation" - {

    "when a scheme ref is specified capture input amounts for 2017+ periods" in {
      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2018, SchemeIndex(0)), "schemeRef").get

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/pension-scheme-0/pension-input-amount")
    }

    "when a scheme ref is specified capture input amounts for 2016 period" in {
      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2016, SchemeIndex(0)), "schemeRef").get

      val nextPageUrl: String =
        WhichSchemePage(Period._2016, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2016/pension-scheme-0/pension-input-amount-15-16-period-1")
    }

    "when a new scheme is indicated capture scheme details" in {
      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2018, SchemeIndex(0)), PSTR.New).get

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/scheme-name-reference/0")
    }

    "to journey recovery when no answer" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to CYA when submitted" in {

      val userAnswers = emptyUserAnswers.set(WhichSchemePage(Period._2018, SchemeIndex(0)), PSTR.New).get

      val nextPageUrl: String =
        WhichSchemePage(Period._2018, SchemeIndex(0)).navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/annual-allowance/2018/check-answers")
    }
  }

  "Cleanup" - {

    "must clean up correctly when a user selects new in English" in {

      val userAnswers = emptyUserAnswers
        .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
        .get

      val cleanedUserAnswers = WhichSchemePage(Period._2018, SchemeIndex(0))
        .cleanup(Some(PSTR.New), userAnswers)
        .success
        .value

      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0))) `mustBe` None
    }

    "must clean up correctly when a user selects new in welsh" in {

      val userAnswers = emptyUserAnswers
        .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
        .get

      val cleanedUserAnswers = WhichSchemePage(Period._2018, SchemeIndex(0))
        .cleanup(Some(PSTR.NewInWelsh), userAnswers)
        .success
        .value

      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0))) `mustBe` None
    }

    "must not clean up when a user selects another scheme from the scheme list" in {

      val userAnswers = emptyUserAnswers
        .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
        .get

      val cleanedUserAnswers = WhichSchemePage(Period._2018, SchemeIndex(0))
        .cleanup(Some("schemeRef"), userAnswers)
        .success
        .value

      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0))) `mustBe` Some(
        PensionSchemeDetails("schemeName", "schemeRef")
      )
    }
  }
}
