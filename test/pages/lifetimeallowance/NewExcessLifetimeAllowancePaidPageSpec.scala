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

import models.{CheckMode, NewExcessLifetimeAllowancePaid, NormalMode}
import pages.behaviours.PageBehaviours

class NewExcessLifetimeAllowancePaidSpec extends PageBehaviours {

  "NewExcessLifetimeAllowancePaidPage" - {

    beRetrievable[NewExcessLifetimeAllowancePaid](NewExcessLifetimeAllowancePaidPage)

    beSettable[NewExcessLifetimeAllowancePaid](NewExcessLifetimeAllowancePaidPage)

    beRemovable[NewExcessLifetimeAllowancePaid](NewExcessLifetimeAllowancePaidPage)
  }

  "normal mode navigation" - {

    "when user has selected Lump Sum for how new excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/new-value-of-lump-sum")
    }

    "when user has selected Both for how excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/new-value-of-lump-sum")
    }

    "when user has selected Annual Payment for how excess was paid " in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Annualpayment).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/new-value-of-annual-payment")
    }

    "when user hasn't entered correct value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Lump Sum for how new excess was paid in Check Mode" in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Lumpsum).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-new-value-of-lump-sum")
    }

    "when user has selected Both for how excess was paid in Check Mode" in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Both).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-new-value-of-lump-sum")
    }

    "when user has selected Annual Payment for how excess was paid in Check Mode" in {

      val userAnswers =
        emptyUserAnswers.set(NewExcessLifetimeAllowancePaidPage, models.NewExcessLifetimeAllowancePaid.Annualpayment).get

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-new-value-of-annual-payment")
    }

    "when user hasn't entered correct value in Check Mode" in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewExcessLifetimeAllowancePaidPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
