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

import models.{CheckMode, NewEnhancementType, NormalMode}
import pages.behaviours.PageBehaviours

class NewEnhancementTypeSpec extends PageBehaviours {

  "NewEnhancementTypePage" - {

    beRetrievable[NewEnhancementType](NewEnhancementTypePage)

    beSettable[NewEnhancementType](NewEnhancementTypePage)

    beRemovable[NewEnhancementType](NewEnhancementTypePage)
  }

  "normal mode navigation" - {

    "when user has selected Internationalenhancement " in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.InternationalEnhancement).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-international-enhancement-reference")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.Both).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-international-enhancement-reference")
    }

    "when user has selected Pensioncredit " in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.PensionCredit).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/new-pension-credit-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewEnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode navigation" - {

    "when user has selected Internationalenhancement " in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.InternationalEnhancement).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-international-enhancement-reference")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.Both).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-international-enhancement-reference")
    }

    "when user has selected Pensioncredit " in {

      val userAnswers =
        emptyUserAnswers.set(NewEnhancementTypePage, models.NewEnhancementType.PensionCredit).get

      val nextPageUrl: String = NewEnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/change-new-pension-credit-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = NewEnhancementTypePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly when user answers both" in {

      val userAnswers = emptyUserAnswers
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers = NewEnhancementTypePage.cleanup(Some(NewEnhancementType.Both), userAnswers).success.value

      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")

    }

    "must cleanup correctly when user answers international enhancement only" in {

      val userAnswers = emptyUserAnswers
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        NewEnhancementTypePage.cleanup(Some(NewEnhancementType.InternationalEnhancement), userAnswers).success.value

      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe Some("123")
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe None

    }

    "must cleanup correctly when user answers pension credit" in {

      val userAnswers = emptyUserAnswers
        .set(NewEnhancementTypePage, NewEnhancementType.Both)
        .get
        .set(NewInternationalEnhancementReferencePage, "123")
        .get
        .set(NewPensionCreditReferencePage, "123")
        .get

      val cleanedUserAnswers =
        NewEnhancementTypePage.cleanup(Some(NewEnhancementType.PensionCredit), userAnswers).success.value

      cleanedUserAnswers.get(NewInternationalEnhancementReferencePage) mustBe None
      cleanedUserAnswers.get(NewPensionCreditReferencePage) mustBe Some("123")
    }
  }
}
