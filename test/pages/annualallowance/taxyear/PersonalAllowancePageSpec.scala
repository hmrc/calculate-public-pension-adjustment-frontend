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

package pages.annualallowance.taxyear

import models.{CheckMode, Period}
import pages.behaviours.PageBehaviours

class PersonalAllowancePageSpec extends PageBehaviours {

  "PersonalAllowancePage" - {

    beRetrievable[BigInt](PersonalAllowancePage(Period._2018))

    beSettable[BigInt](PersonalAllowancePage(Period._2018))

    beRemovable[BigInt](PersonalAllowancePage(Period._2018))

    "must Navigate correctly in normal mode" - {}

    "must Navigate correctly to CYA in check mode" in {
      val ua     = emptyUserAnswers
        .set(
          PersonalAllowancePage(Period._2018),
          BigInt(100)
        )
        .success
        .value
      val result = PersonalAllowancePage(Period._2018).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2018/check-answers")
    }
  }
}
