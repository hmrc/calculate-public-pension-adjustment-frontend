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

import models.{CheckMode, EnhancementType, NormalMode}
import pages.behaviours.PageBehaviours

class EnhancementTypeSpec extends PageBehaviours {

  "EnhancementTypePage" - {

    beRetrievable[EnhancementType](EnhancementTypePage)

    beSettable[EnhancementType](EnhancementTypePage)

    beRemovable[EnhancementType](EnhancementTypePage)
  }

  "normal mode navigation" - {

    "when user has selected Internationalenhancement " in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.InternationalEnhancement).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/international-enhancement-reference")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.Both).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/international-enhancement-reference")
    }

    "when user has selected Pensioncredit " in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.PensionCredit).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/pension-credit-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Internationalenhancement " in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.InternationalEnhancement).get

      val nextPageUrl: String = EnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-international-enhancement-reference")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.Both).get

      val nextPageUrl: String = EnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-international-enhancement-reference")
    }

    "when user has selected Pensioncredit " in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.PensionCredit).get

      val nextPageUrl: String = EnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-pension-credit-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = EnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when user answers both" in {

      val userAnswers = emptyUserAnswers
        .set(EnhancementTypePage, EnhancementType.Both)
        .get
        .set(InternationalEnhancementReferencePage, "123")
        .get
        .set(PensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers = EnhancementTypePage.cleanup(Some(EnhancementType.Both), userAnswers).success.value

      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe Some("123")

    }

    "must cleanup correctly when user answers international enhancement only" in {

      val userAnswers = emptyUserAnswers
        .set(EnhancementTypePage, EnhancementType.Both)
        .get
        .set(InternationalEnhancementReferencePage, "123")
        .get
        .set(PensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        EnhancementTypePage.cleanup(Some(EnhancementType.InternationalEnhancement), userAnswers).success.value

      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe None

    }

    "must cleanup correctly when user answers pension credit" in {

      val userAnswers = emptyUserAnswers
        .set(EnhancementTypePage, EnhancementType.Both)
        .get
        .set(InternationalEnhancementReferencePage, "123")
        .get
        .set(PensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        EnhancementTypePage.cleanup(Some(EnhancementType.PensionCredit), userAnswers).success.value

      cleanedUserAnswers.get(InternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(PensionCreditReferencePage) mustBe Some("123")
    }
  }
}
