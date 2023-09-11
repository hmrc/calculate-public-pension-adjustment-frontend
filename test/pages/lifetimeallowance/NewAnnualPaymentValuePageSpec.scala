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

package pages.lifetimeallowance

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class NewAnnualPaymentValuePageSpec extends PageBehaviours {

  "NewAnnualPaymentValuePage" - {

    beRetrievable[BigInt](NewAnnualPaymentValuePage)

    beSettable[BigInt](NewAnnualPaymentValuePage)

    beRemovable[BigInt](NewAnnualPaymentValuePage)
  }

    "normal mode navigation" - {

    "when user has entered for Annual Payment Value page" in {

      val userAnswers = emptyUserAnswers.set(NewAnnualPaymentValuePage, BigInt("100")).get

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/who-paying-extra-lta-charge")
    }

    "when user hasn't entered Annual payment value page" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

    "check mode navigation" - {

      "when user has entered for Annual payment value page in Check mode " in {

        val userAnswers = emptyUserAnswers.set(NewAnnualPaymentValuePage, BigInt("200")).get

        val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/check-your-lta-answers")
      }

      "when user hasn't entered Annual payment value page in Check mode " in {

        val userAnswers = emptyUserAnswers

        val nextPageUrl: String = NewAnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
}
