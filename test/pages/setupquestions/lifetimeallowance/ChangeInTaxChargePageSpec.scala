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

import models.{ChangeInTaxCharge, CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class ChangeInTaxChargePageSpec extends PageBehaviours {

  "ChangeInTaxChargePage" - {

    beRetrievable[ChangeInTaxCharge](ChangeInTaxChargePage)

    beSettable[ChangeInTaxCharge](ChangeInTaxChargePage)

    beRemovable[ChangeInTaxCharge](ChangeInTaxChargePage)
  }

  "normal mode" - {

    "must navigate to multiple benefit crystallisation event page when user indicates a charge" in {

      val userAnswers =
        emptyUserAnswers
          .set(ChangeInTaxChargePage, models.ChangeInTaxCharge.IncreasedCharge)
          .get

      val nextPageUrl: String = ChangeInTaxChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/task-list")
    }

    "must navigate to kickout when user answers no charge" in {

      val userAnswers =
        emptyUserAnswers
          .set(ChangeInTaxChargePage, models.ChangeInTaxCharge.None)
          .get

      val nextPageUrl: String = ChangeInTaxChargePage.navigate(NormalMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }
  }

  "Checkmode" - {

    "must navigate to kickout when user answers no charge" in {

      val userAnswers =
        emptyUserAnswers
          .set(ChangeInTaxChargePage, models.ChangeInTaxCharge.None)
          .get

      val nextPageUrl: String = ChangeInTaxChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/lifetime-allowance/more-than-one-benefit-crystallisation-event")
    }

    "must navigate to MultipleBenefitCrystallisationEvent Page when user answers new/increase/decrease charge" in {

      val userAnswers =
        emptyUserAnswers
          .set(ChangeInTaxChargePage, models.ChangeInTaxCharge.IncreasedCharge)
          .get

      val nextPageUrl: String = ChangeInTaxChargePage.navigate(CheckMode, userAnswers).url

      checkNavigation(nextPageUrl, "/task-list")
    }
  }
}
