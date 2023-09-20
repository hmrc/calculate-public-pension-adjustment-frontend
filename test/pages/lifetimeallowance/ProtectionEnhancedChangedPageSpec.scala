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

import models.{CheckMode, NormalMode, ProtectionEnhancedChanged}
import pages.behaviours.PageBehaviours

class ProtectionEnhancedChangedSpec extends PageBehaviours {

  "ProtectionEnhancedChangedPage" - {

    beRetrievable[ProtectionEnhancedChanged](ProtectionEnhancedChangedPage)

    beSettable[ProtectionEnhancedChanged](ProtectionEnhancedChangedPage)

    beRemovable[ProtectionEnhancedChanged](ProtectionEnhancedChangedPage)
  }

  "normal mode navigation" - {

    "when user has selected Protection " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Protection).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/protection-changed-new-type")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Both).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/protection-changed-new-type")
    }

    "when user has selected Enhancement " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Enhancement).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-enhancement-type")
    }

    "when user has selected no " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.No).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lta-charge-2015-2023")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Protection " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Protection).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-protection-changed-new-type")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Both).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-protection-changed-new-type")
    }

    "when user has selected Enhancement " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Enhancement).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-enhancement-type")
    }

    "when user has selected No " in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.No).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-lta-answers")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
