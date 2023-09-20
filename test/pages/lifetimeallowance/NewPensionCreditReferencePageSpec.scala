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

class NewPensionCreditReferencePageSpec extends PageBehaviours {

  "NewPensionCreditReferencePage" - {

    beRetrievable[String](NewPensionCreditReferencePage)

    beSettable[String](NewPensionCreditReferencePage)

    beRemovable[String](NewPensionCreditReferencePage)
  }

  "normal mode navigation" - {

    "when user submits a reference" in {

      val userAnswers =
        emptyUserAnswers.set(NewPensionCreditReferencePage, "validRef").get

      val nextPageUrl: String = NewPensionCreditReferencePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lta-charge-2015-2023")
    }

  }

  "check mode navigation" - {

    "when user submits a reference" in {

      val userAnswers =
        emptyUserAnswers.set(NewPensionCreditReferencePage, "validRef").get

      val nextPageUrl: String = NewPensionCreditReferencePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/check-your-lta-answers")
    }

  }
}
