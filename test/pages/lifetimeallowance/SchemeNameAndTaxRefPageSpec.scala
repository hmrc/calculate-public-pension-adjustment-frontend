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

package pages.lifetimeallowance

import models.{CheckMode, NormalMode, SchemeNameAndTaxRef}
import pages.behaviours.PageBehaviours

class SchemeNameAndTaxRefPageSpec extends PageBehaviours {

  "SchemeNameAndTaxRefPage" - {

    beRetrievable[SchemeNameAndTaxRef](SchemeNameAndTaxRefPage)

    beSettable[SchemeNameAndTaxRef](SchemeNameAndTaxRefPage)

    beRemovable[SchemeNameAndTaxRef](SchemeNameAndTaxRefPage)
  }

  "Normal mode" - {

    "must navigate to quarter change page" in {

      val userAnswers =
        emptyUserAnswers
          .set(SchemeNameAndTaxRefPage, SchemeNameAndTaxRef("schemeName", "schemeRef"))
          .get

      val nextPageUrl: String = SchemeNameAndTaxRefPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/quarter-charge-was-paid")
    }
  }

  "Check mode" - {

    "must navigate to LTA CYA page when QuarterChargePaidPage exists" in {

      val userAnswers =
        emptyUserAnswers
          .set(SchemeNameAndTaxRefPage, SchemeNameAndTaxRef("schemeName", "schemeRef"))
          .get
          .set(QuarterChargePaidPage, models.QuarterChargePaid.AprToJul)
          .get

      val nextPageUrl: String = SchemeNameAndTaxRefPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")

    }

    "must navigate to QuarterChargePaid page when QuarterChargePaidPage does not exist" in {

      val userAnswers =
        emptyUserAnswers
          .set(SchemeNameAndTaxRefPage, SchemeNameAndTaxRef("schemeName", "schemeRef"))
          .get

      val nextPageUrl: String = SchemeNameAndTaxRefPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-quarter-charge-was-paid")

    }
  }
}
