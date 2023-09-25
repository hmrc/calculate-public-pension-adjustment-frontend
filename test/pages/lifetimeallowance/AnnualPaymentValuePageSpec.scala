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

class AnnualPaymentValuePageSpec extends PageBehaviours {

  "AnnualPaymentValuePage" - {

    beRetrievable[BigInt](AnnualPaymentValuePage)

    beSettable[BigInt](AnnualPaymentValuePage)

    beRemovable[BigInt](AnnualPaymentValuePage)
  }

  "normal mode navigation" - {

    "when user has entered for Annual Payment Value page" in {

      val userAnswers = emptyUserAnswers.set(AnnualPaymentValuePage, BigInt("100")).get

      val nextPageUrl: String = AnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/who-paid-charge")
    }

    "when user hasn't entered Annual payment value page" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = AnnualPaymentValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has entered for Annual payment value page in Check mode " in {

      val userAnswers = emptyUserAnswers.set(AnnualPaymentValuePage, BigInt("200")).get

      val nextPageUrl: String = AnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-who-paid-charge")
    }

    "when user hasn't entered Annual payment value page in Check mode " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = AnnualPaymentValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
