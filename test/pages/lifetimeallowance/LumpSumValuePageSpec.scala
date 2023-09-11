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

class LumpSumValuePageSpec extends PageBehaviours {

  "LumpSumValuePage" - {

    beRetrievable[BigInt](LumpSumValuePage)

    beSettable[BigInt](LumpSumValuePage)

    beRemovable[BigInt](LumpSumValuePage)
  }

  "normal mode navigation" - {

    "when user has selected Lump Sum for how excess was paid " in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Lumpsum)
        .get

      val nextPageUrl: String = LumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/who-paid-lta-charge")
    }

    "when user has selected Both for how excess was paid " in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("100"))
        .get
        .set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = LumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/value-of-annual-payment")
    }

    "when user has entered correct value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = LumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Lump Sum for how excess was paid in check mode " in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("200"))
        .get
        .set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Lumpsum)
        .get

      val nextPageUrl: String = LumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-lta-answers")
    }

    "when user has selected Both for how excess was paid in check mode " in {

      val userAnswers = emptyUserAnswers
        .set(LumpSumValuePage, BigInt("200"))
        .get
        .set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = LumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-value-of-annual-payment")
    }

    "when user has entered correct value in Check Mode" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = LumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
