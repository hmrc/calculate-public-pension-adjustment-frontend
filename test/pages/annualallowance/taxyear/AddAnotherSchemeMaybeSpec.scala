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

import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, SchemeIndex, ThresholdIncome}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.behaviours.PageBehaviours

class AddAnotherSchemeMaybeSpec extends PageBehaviours {

  "Add Another Scheme Maybe" - {

    "must navigate to  check answer page when OtherDefinedBenefitOrContributionPage is defined" in {
      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2016), false).get
        .set(AddAnotherSchemePage(Period._2016,SchemeIndex(0)), false).get
        .set(PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef")).get
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016), true)
        .get

      val nextUrl: String =
        AddAnotherSchemePage(Period._2016,SchemeIndex(0)).navigate(CheckMode, userAnswers).url

      checkNavigation(nextUrl, "/annual-allowance/2016/check-answers")
    }

    "must navigate to  check answer page when ThresholdIncomePage is defined" in {
      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2016), false).get
        .set(AddAnotherSchemePage(Period._2016,SchemeIndex(0)), false).get
        .set(PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef")).get
        .set(ThresholdIncomePage(Period._2016),ThresholdIncome.No)
        .get

      val nextUrl: String =
        AddAnotherSchemePage(Period._2016,SchemeIndex(0)).navigate(CheckMode, userAnswers).url

      checkNavigation(nextUrl, "/annual-allowance/2016/check-answers")
    }

    "must navigate to  check answer page when TotalIncomePage is defined" in {
      val userAnswers = emptyUserAnswers
        .set(MemberMoreThanOnePensionPage(Period._2016), false).get
        .set(AddAnotherSchemePage(Period._2016,SchemeIndex(0)), false).get
        .set(PensionSchemeDetailsPage(Period._2016, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef")).get
        .set(TotalIncomePage(Period._2016),BigInt(100))
        .get

      val nextUrl: String =
        AddAnotherSchemePage(Period._2016,SchemeIndex(0)).navigate(CheckMode, userAnswers).url

      checkNavigation(nextUrl, "/annual-allowance/2016/check-answers")
    }

    "must redirect to journey recovery when member more than one pension scheme page not answered" in {

      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .success
        .value

      val nextPagUrl = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPagUrl, "/there-is-a-problem")
    }

    "must redirect to journey recovery when member more than one scheme false & Defined Contribution not answered" in {
      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .success
        .value
        .set(MemberMoreThanOnePensionPage(Period._2018), false)
        .success
        .value

      val nextPagUrl = page.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPagUrl, "/there-is-a-problem")
    }

    "must navigate to AA period CYA page when answered in check mode" in {

      val page = AddAnotherSchemePage(Period._2018, SchemeIndex(0))

      val userAnswers = emptyUserAnswers
        .set(page, false)
        .success
        .value

      val nextPagUrl = page.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPagUrl, "/annual-allowance/2018/check-answers")
    }
  }
}
