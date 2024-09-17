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

package pages.setupquestions.lifetimeallowance

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

import scala.util.Random

class PreviousLTAChargePageSpec extends PageBehaviours {

  "PreviousLTAChargePage" - {

    beRetrievable[Boolean](PreviousLTAChargePage)

    beSettable[Boolean](PreviousLTAChargePage)

    beRemovable[Boolean](PreviousLTAChargePage)
  }

  "Normal mode" - {

    "to change in life time allowance page when answered" in {
      val userAnswers =
        emptyUserAnswers
          .set(PreviousLTAChargePage, true)
          .get

      val nextPageUrl: String = PreviousLTAChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-percentage-change")
    }

    "to journey recovery when not answered" in {
      val nextPageUrl: String = PreviousLTAChargePage.navigate(NormalMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "Check mode" - {

    "to change in life time allowance page when answered" in {

      val userAnswers =
        emptyUserAnswers
          .set(PreviousLTAChargePage, true)
          .get

      val nextPageUrl: String = PreviousLTAChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/lifetime-allowance-percentage-change")

    }

    "to journey recovery when not answered" in {
      val nextPageUrl: String = PreviousLTAChargePage.navigate(CheckMode, emptyUserAnswers).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "cleanup" - {

    "when user answers yes or no" in {

      val cleanedUserAnswers = PreviousLTAChargePage
        .cleanup(Some(Random.nextBoolean()), userAnswersLTATriage)
        .success
        .value

      cleanedUserAnswers.get(ChangeInLifetimeAllowancePage) mustBe None
      cleanedUserAnswers.get(IncreaseInLTAChargePage) mustBe None
      cleanedUserAnswers.get(NewLTAChargePage) mustBe None
      cleanedUserAnswers.get(MultipleBenefitCrystallisationEventPage) mustBe None
      cleanedUserAnswers.get(OtherSchemeNotificationPage) mustBe None
    }
  }

}
