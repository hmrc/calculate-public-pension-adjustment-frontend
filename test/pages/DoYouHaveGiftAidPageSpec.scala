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

package pages

import models.{CheckMode, NormalMode, Period}
import pages.annualallowance.taxyear.{AmountOfGiftAidPage, DoYouHaveGiftAidPage}
import pages.behaviours.PageBehaviours

class DoYouHaveGiftAidPageSpec extends PageBehaviours {

  val period = Period._2022

  "DoYouHaveGiftAidPage" - {

    beRetrievable[Boolean](DoYouHaveGiftAidPage(period))

    beSettable[Boolean](DoYouHaveGiftAidPage(period))

    beRemovable[Boolean](DoYouHaveGiftAidPage(period))
  }

  "Normal mode" - {

    "when user answers true to amount of gift aid page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveGiftAidPage(period),
          true
        )
        .success
        .value

      val result = DoYouHaveGiftAidPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/donated-via-gift-aid-amount")

    }

    "when user answers false to amount of do you know personal allowance page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveGiftAidPage(period),
          false
        )
        .success
        .value

      val result = DoYouHaveGiftAidPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/do-you-know-personal-allowance")

    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DoYouHaveGiftAidPage(period).navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "when user answers true to amount of gift aid page" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveGiftAidPage(period),
          true
        )
        .success
        .value

      val result = DoYouHaveGiftAidPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/change-donated-via-gift-aid-amount")

    }

    "when user answers false to CYA" in {

      val ua = emptyUserAnswers
        .set(
          DoYouHaveGiftAidPage(period),
          false
        )
        .success
        .value

      val result = DoYouHaveGiftAidPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/annual-allowance/2022/check-answers")

    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = DoYouHaveGiftAidPage(period).navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "must cleanup correctly" in {

      val ua = emptyUserAnswers
        .set(DoYouHaveGiftAidPage(period), true)
        .success
        .value
        .set(AmountOfGiftAidPage(period), BigInt(1))
        .success
        .value

      val cleanedUserAnswers = DoYouHaveGiftAidPage(period).cleanup(Some(false), ua).success.value

      cleanedUserAnswers.get(AmountOfGiftAidPage(period)) mustBe None
    }
  }
}
