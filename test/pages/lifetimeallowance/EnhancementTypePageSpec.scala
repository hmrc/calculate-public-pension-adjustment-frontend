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

import models.{EnhancementType, NormalMode}
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
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.Internationalenhancement).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/international-enhancement-reference")
    }

    "when user has selected Both" in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.Both).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/international-enhancement-reference")
    }

    "when user has selected Pensioncredit " in {

      val userAnswers =
        emptyUserAnswers.set(EnhancementTypePage, models.EnhancementType.Pensioncredit).get

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/pension-credit-reference")
    }

    "when user has entered incorrect value " in {

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = EnhancementTypePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
