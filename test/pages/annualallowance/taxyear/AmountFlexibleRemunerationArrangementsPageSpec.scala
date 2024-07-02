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

import models.{CheckMode, NormalMode, Period, ThresholdIncome}
import pages.behaviours.PageBehaviours

class AmountFlexibleRemunerationArrangementsPageSpec extends PageBehaviours {

  "AmountFlexibleRemunerationArrangementsPage" - {

    beRetrievable[BigInt](AmountFlexibleRemunerationArrangementsPage(Period._2018))

    beSettable[BigInt](AmountFlexibleRemunerationArrangementsPage(Period._2018))

    beRemovable[BigInt](AmountFlexibleRemunerationArrangementsPage(Period._2018))
  }

  "must Navigate correctly in normal mode" - {

    "to HowMuchContributionPensionSchemeController page when answered" in {
      val ua     = emptyUserAnswers
        .set(
          AmountFlexibleRemunerationArrangementsPage(Period._2018),
          BigInt(100)
        )
        .success
        .value
      val result = AmountFlexibleRemunerationArrangementsPage(Period._2018).navigate(NormalMode, ua).url

      checkNavigation(result, "/annual-allowance/2018/contribute-to-relief-at-source-scheme")
    }
  }

  "when in check mode must Navigate correctly to HowMuchContributionPensionSchemeController in normal mode " in {
    val ua     = emptyUserAnswers
      .set(
        AmountFlexibleRemunerationArrangementsPage(Period._2018),
        BigInt(100)
      )
      .success
      .value
    val result = AmountFlexibleRemunerationArrangementsPage(Period._2018).navigate(CheckMode, ua).url

    checkNavigation(result, "/annual-allowance/2018/contribute-to-relief-at-source-scheme")
  }

  "cleanup" - {

    "must cleanup correctly" in {

      val period = Period._2022

      val cleanedUserAnswers = AmountFlexibleRemunerationArrangementsPage(period)
        .cleanup(Some(BigInt(1)), incomeSubJourneyData)
        .success
        .value

      cleanedUserAnswers.get(ThresholdIncomePage(period)) mustBe Some(ThresholdIncome.IDoNotKnow)
      cleanedUserAnswers.get(TotalIncomePage(period)) mustBe Some(BigInt(2000))
      cleanedUserAnswers.get(AnySalarySacrificeArrangementsPage(period)) mustBe Some(true)
      cleanedUserAnswers.get(AmountSalarySacrificeArrangementsPage(period)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(FlexibleRemunerationArrangementsPage(period)) mustBe Some(true)
      cleanedUserAnswers.get(AmountFlexibleRemunerationArrangementsPage(period)) mustBe Some(BigInt(1))
      cleanedUserAnswers.get(HowMuchContributionPensionSchemePage(period)) mustBe None
      cleanedUserAnswers.get(AnyLumpSumDeathBenefitsPage(period)) mustBe None
      cleanedUserAnswers.get(LumpSumDeathBenefitsValuePage(period)) mustBe None
      cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(period)) mustBe None
      cleanedUserAnswers.get(TaxReliefPage(period)) mustBe None
      cleanedUserAnswers.get(KnowAdjustedAmountPage(period)) mustBe None
      cleanedUserAnswers.get(AdjustedIncomePage(period)) mustBe None
      cleanedUserAnswers.get(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)) mustBe None
      cleanedUserAnswers.get(HowMuchTaxReliefPensionPage(period)) mustBe None
      cleanedUserAnswers.get(HasReliefClaimedOnOverseasPensionPage(period)) mustBe None
      cleanedUserAnswers.get(AmountClaimedOnOverseasPensionPage(period)) mustBe None
      cleanedUserAnswers.get(DoYouHaveGiftAidPage(period)) mustBe None
      cleanedUserAnswers.get(AmountOfGiftAidPage(period)) mustBe None
      cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(period)) mustBe None
      cleanedUserAnswers.get(DoYouHaveCodeAdjustmentPage(period)) mustBe None
      cleanedUserAnswers.get(PayeCodeAdjustmentPage(period)) mustBe None
      cleanedUserAnswers.get(CodeAdjustmentAmountPage(period)) mustBe None
      cleanedUserAnswers.get(PersonalAllowancePage(period)) mustBe None
      cleanedUserAnswers.get(BlindAllowancePage(period)) mustBe None
    }
  }
}
