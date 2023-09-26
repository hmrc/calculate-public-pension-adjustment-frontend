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

import models.{CheckMode, NewEnhancementType, NormalMode, ProtectionEnhancedChanged, WhatNewProtectionTypeEnhancement}
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

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-protection")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Both).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-protection")
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

      checkNavigation(nextPageUrl, "/lifetime-allowance/charge-2015-2023")
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

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-protection")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(ProtectionEnhancedChangedPage, models.ProtectionEnhancedChanged.Both).get

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-protection")
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

      checkNavigation(nextPageUrl, "/lifetime-allowance/check-answers")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = ProtectionEnhancedChangedPage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when user answers both" in {

      val userAnswers = emptyUserAnswers
        .set(ProtectionEnhancedChangedPage, ProtectionEnhancedChanged.Both)
        .get
        .set(WhatNewProtectionTypeEnhancementPage, WhatNewProtectionTypeEnhancement.EnhancedProtection)
        .get
        .set(ReferenceNewProtectionTypeEnhancementPage, "123")
        .get
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        ProtectionEnhancedChangedPage.cleanup(Some(ProtectionEnhancedChanged.Both), userAnswers).success.value

      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe Some(
        WhatNewProtectionTypeEnhancement.EnhancedProtection
      )
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe Some("123")
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe Some(NewEnhancementType.Both)
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")

    }

    "must cleanup correctly when user answers protection" in {

      val userAnswers = emptyUserAnswers
        .set(ProtectionEnhancedChangedPage, ProtectionEnhancedChanged.Both)
        .get
        .set(WhatNewProtectionTypeEnhancementPage, WhatNewProtectionTypeEnhancement.EnhancedProtection)
        .get
        .set(ReferenceNewProtectionTypeEnhancementPage, "123")
        .get
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        ProtectionEnhancedChangedPage.cleanup(Some(ProtectionEnhancedChanged.Protection), userAnswers).success.value

      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe Some(
        WhatNewProtectionTypeEnhancement.EnhancedProtection
      )
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe Some("123")
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe None
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe None

    }

    "must cleanup correctly when user answers enhancement" in {

      val userAnswers = emptyUserAnswers
        .set(ProtectionEnhancedChangedPage, ProtectionEnhancedChanged.Both)
        .get
        .set(WhatNewProtectionTypeEnhancementPage, WhatNewProtectionTypeEnhancement.EnhancedProtection)
        .get
        .set(ReferenceNewProtectionTypeEnhancementPage, "123")
        .get
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        ProtectionEnhancedChangedPage.cleanup(Some(ProtectionEnhancedChanged.Enhancement), userAnswers).success.value

      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe Some(NewEnhancementType.Both)
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")
    }

    "must cleanup correctly when user answers none" in {

      val userAnswers = emptyUserAnswers
        .set(ProtectionEnhancedChangedPage, ProtectionEnhancedChanged.Both)
        .get
        .set(WhatNewProtectionTypeEnhancementPage, WhatNewProtectionTypeEnhancement.EnhancedProtection)
        .get
        .set(ReferenceNewProtectionTypeEnhancementPage, "123")
        .get
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        ProtectionEnhancedChangedPage.cleanup(Some(ProtectionEnhancedChanged.No), userAnswers).success.value

      cleanedUserAnswers.get(WhatNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(ReferenceNewProtectionTypeEnhancementPage) mustBe None
      cleanedUserAnswers.get(NewEnhancementTypePage) mustBe None
      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe None
    }
  }
}
