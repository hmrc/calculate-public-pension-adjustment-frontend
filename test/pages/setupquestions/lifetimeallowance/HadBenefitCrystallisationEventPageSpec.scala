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

import models.{CheckMode, EnhancementType, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, NewEnhancementType, NewExcessLifetimeAllowancePaid, NormalMode, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, SchemeNameAndTaxRef, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import pages.behaviours.PageBehaviours
import pages.lifetimeallowance._

import java.time.LocalDate

class HadBenefitCrystallisationEventPageSpec extends PageBehaviours {

  "HadBenefitCrystallisationEventPage" - {

    beRetrievable[Boolean](HadBenefitCrystallisationEventPage)

    beSettable[Boolean](HadBenefitCrystallisationEventPage)

    beRemovable[Boolean](HadBenefitCrystallisationEventPage)
  }

  "normal mode" - {

    "to previous LTA charge page when user answers yes" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/LTA-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, false)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }

  "Check mode" - {

    "to LTA charge page when user answers true" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, true)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/LTA-charge")
    }

    "must navigate to kickout page when user answers false" in {

      val ua = emptyUserAnswers
        .set(HadBenefitCrystallisationEventPage, false)
        .success
        .value

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/cannot-use-lta-service")
    }

    "must navigate to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val nextPageUrl: String = HadBenefitCrystallisationEventPage.navigate(CheckMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")

    }
  }
}