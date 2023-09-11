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

import models.LtaProtectionOrEnhancements.{Both, Enhancements, Protection}
import models.{CheckMode, NormalMode, ProtectionType}
import pages.behaviours.PageBehaviours

class ProtectionTypeSpec extends PageBehaviours {

  "ProtectionTypePage" - {

    beRetrievable[ProtectionType](ProtectionTypePage)

    beSettable[ProtectionType](ProtectionTypePage)

    beRemovable[ProtectionType](ProtectionTypePage)
  }

  "normal mode navigation" - {

    "when user submits any protection type" in {

      val userAnswers =
        emptyUserAnswers
          .set(ProtectionTypePage, models.ProtectionType.FixedProtection)
          .get

      val nextPageUrl: String = ProtectionTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/protection-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user submits any protection type" in {

      val userAnswers =
        emptyUserAnswers
          .set(ProtectionTypePage, models.ProtectionType.FixedProtection)
          .get

      val nextPageUrl: String = ProtectionTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/change-protection-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
