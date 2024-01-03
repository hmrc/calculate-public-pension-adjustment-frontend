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

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class ProtectionReferencePageSpec extends PageBehaviours {

  "ProtectionReferencePage" - {

    beRetrievable[String](ProtectionReferencePage)

    beSettable[String](ProtectionReferencePage)

    beRemovable[String](ProtectionReferencePage)
  }

  "normal mode navigation" - {

    "when user has selected Protection on LtaProtectionOrEnhancementsPage and submits a reference" in {

      val userAnswers =
        emptyUserAnswers
          .set(LtaProtectionOrEnhancementsPage, models.LtaProtectionOrEnhancements.Protection)
          .get
          .set(ProtectionReferencePage, "validRef")
          .get

      val nextPageUrl: String = ProtectionReferencePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/protection-enhancement-changed")
    }

    "when user has selected Both on LtaProtectionOrEnhancementsPage and submits a reference" in {

      val userAnswers =
        emptyUserAnswers
          .set(LtaProtectionOrEnhancementsPage, models.LtaProtectionOrEnhancements.Both)
          .get
          .set(InternationalEnhancementReferencePage, "validRef")
          .get

      val nextPageUrl: String = ProtectionReferencePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/enhancement-type")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionReferencePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Protection on LtaProtectionOrEnhancementsPage and submits a reference" in {

      val userAnswers =
        emptyUserAnswers
          .set(LtaProtectionOrEnhancementsPage, models.LtaProtectionOrEnhancements.Protection)
          .get
          .set(ProtectionReferencePage, "validRef")
          .get

      val nextPageUrl: String = ProtectionReferencePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has selected Both on LtaProtectionOrEnhancementsPage and submits a reference" in {

      val userAnswers =
        emptyUserAnswers
          .set(LtaProtectionOrEnhancementsPage, models.LtaProtectionOrEnhancements.Both)
          .get
          .set(InternationalEnhancementReferencePage, "validRef")
          .get

      val nextPageUrl: String = ProtectionReferencePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-enhancement-type")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionReferencePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
