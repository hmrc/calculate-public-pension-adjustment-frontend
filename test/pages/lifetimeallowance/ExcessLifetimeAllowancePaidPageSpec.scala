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

import models.{CheckMode, ExcessLifetimeAllowancePaid, NormalMode}
import pages.behaviours.PageBehaviours

class ExcessLifetimeAllowancePaidSpec extends PageBehaviours {

  "ExcessLifetimeAllowancePaidPage" - {

    beRetrievable[ExcessLifetimeAllowancePaid](ExcessLifetimeAllowancePaidPage)

    beSettable[ExcessLifetimeAllowancePaid](ExcessLifetimeAllowancePaidPage)

    beRemovable[ExcessLifetimeAllowancePaid](ExcessLifetimeAllowancePaidPage)
  }

  "normal mode navigation" - {

    "when user has selected Lump Sum for how excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Lumpsum).get

      val nextPageUrl: String = ExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/value-of-lump-sum")
    }

    "when user has selected Both for how excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Both).get

      val nextPageUrl: String = ExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/value-of-lump-sum")
    }

    "when user has selected Annual Payment for how excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(ExcessLifetimeAllowancePaidPage, models.ExcessLifetimeAllowancePaid.Annualpayment).get

      val nextPageUrl: String = ExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/value-of-annual-payment")
    }

    "when user has entered correct value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "must navigate to journey recovery when incorrect value" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ExcessLifetimeAllowancePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
