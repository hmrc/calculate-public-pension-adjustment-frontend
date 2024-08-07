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

import models.{CheckMode, NormalMode, Period}
import pages.behaviours.PageBehaviours

class AmountOfGiftAidPageSpec extends PageBehaviours {

  "AmountOfGiftAidPage" - {

    beRetrievable[BigInt](AmountOfGiftAidPage(Period._2022))

    beSettable[BigInt](AmountOfGiftAidPage(Period._2022))

    beRemovable[BigInt](AmountOfGiftAidPage(Period._2022))
  }

  "Normal mode" - {

    "to do you know your personal allowance page" in {
      val ua     = emptyUserAnswers
        .set(
          AmountOfGiftAidPage(Period._2018),
          BigInt(100)
        )
        .success
        .value
      val result = AmountOfGiftAidPage(Period._2018).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2018/do-you-know-personal-allowance")
    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = AmountOfGiftAidPage(Period._2018).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "to CYA" in {
      val ua     = emptyUserAnswers
        .set(
          AmountOfGiftAidPage(Period._2018),
          BigInt(100)
        )
        .success
        .value
      val result = AmountOfGiftAidPage(Period._2018).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2018/check-answers")
    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = AmountOfGiftAidPage(Period._2018).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }

  }
}
