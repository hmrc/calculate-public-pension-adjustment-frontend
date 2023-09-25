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

class NewLumpSumValuePageSpec extends PageBehaviours {

  "NewLumpSumValuePage" - {

    beRetrievable[BigInt](NewLumpSumValuePage)

    beSettable[BigInt](NewLumpSumValuePage)

    beRemovable[BigInt](NewLumpSumValuePage)
  }

  "normal mode navigation" - {

    "when user has selected Lump Sum for how excess was paid and there is no old lump sum value" in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Both for how excess was paid " in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("100"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-value-of-annual-payment")
    }

    "when user has entered correct value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewLumpSumValuePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Lump Sum for how excess was paid in check mode " in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("200"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Both for how excess was paid in check mode " in {

      val userAnswers = emptyUserAnswers
        .set(NewLumpSumValuePage, BigInt("200"))
        .get
        .set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both)
        .get

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-value-of-annual-payment")
    }

    "when user has entered correct value in Check Mode" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewLumpSumValuePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
