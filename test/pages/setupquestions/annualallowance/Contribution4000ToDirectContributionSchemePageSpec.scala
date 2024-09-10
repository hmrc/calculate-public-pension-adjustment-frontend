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

import models.Period.{_2013, _2014, _2015, _2021, _2022}
import models.{CheckMode, ContributedToDuringRemedyPeriod, LTAKickOutStatus, MaybePIAIncrease, NormalMode, PensionSchemeDetails, PensionSchemeInputAmounts, SchemeIndex, ThresholdIncome, WhichYearsScottishTaxpayer, WhoPaidAACharge}
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, PIAPreRemedyPage, PayTaxCharge1415Page, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page, StopPayingPublicPensionPage, WhichYearsScottishTaxpayerPage}
import pages.annualallowance.taxyear.{AddAnotherSchemePage, AdjustedIncomePage, AmountOfGiftAidPage, BlindAllowancePage, BlindPersonsAllowanceAmountPage, ClaimingTaxReliefPensionPage, ContributedToDuringRemedyPeriodPage, DefinedBenefitAmountPage, DefinedContributionAmountPage, DidYouContributeToRASSchemePage, DoYouHaveGiftAidPage, DoYouKnowPersonalAllowancePage, FlexiAccessDefinedContributionAmountPage, HowMuchAAChargeSchemePaidPage, HowMuchAAChargeYouPaidPage, KnowAdjustedAmountPage, MemberMoreThanOnePensionPage, OtherDefinedBenefitOrContributionPage, PayAChargePage, PensionSchemeDetailsPage, PensionSchemeInputAmountsPage, PersonalAllowancePage, RASContributionAmountPage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage, WhichSchemePage, WhoPaidAAChargePage}
import pages.behaviours.PageBehaviours
import pages.setupquestions.annualallowance.{Contribution4000ToDirectContributionSchemePage, FlexibleAccessDcSchemePage, MaybePIAIncreasePage, NetIncomeAbove190KIn2023Page, PIAAboveAnnualAllowanceIn2023Page, PensionProtectedMemberPage, SavingsStatementPage}

import java.time.LocalDate

class Contribution4000ToDirectContributionSchemePageSpec extends PageBehaviours {

  "Contribution4000ToDirectContributionSchemePage" - {

    beRetrievable[Boolean](Contribution4000ToDirectContributionSchemePage)

    beSettable[Boolean](Contribution4000ToDirectContributionSchemePage)

    beRemovable[Boolean](Contribution4000ToDirectContributionSchemePage)

    "normal mode" - {

      "when yes" - {

        "when lta kickout status 0 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 0)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when lta kickout status 1 to had BCE page" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 1)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
        }

        "when lta kickout status 2 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when no LTA kickout status to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when LTA kickout status anything else to journey recovery" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 3)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

          checkNavigation(nextPageUrl, "/there-is-a-problem")
        }
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(Contribution4000ToDirectContributionSchemePage, false)
          .success
          .value

        val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String =
          Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }
    "check mode" - {

      "when yes" - {

        "when lta kickout status 0 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 0)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when lta kickout status 1 to had BCE page" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 1)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/lifetime-allowance/benefit-crystallisation-event")
        }

        "when lta kickout status 2 to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when no LTA kickout status to cya" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 2)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/check-your-answers-setup")
        }

        "when LTA kickout status anything else to journey recovery" in {

          val userAnswers = emptyUserAnswers
            .set(Contribution4000ToDirectContributionSchemePage, true)
            .success
            .value
            .set(LTAKickOutStatus(), 3)
            .success
            .value

          val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

          checkNavigation(nextPageUrl, "/there-is-a-problem")
        }
      }

      "to TriageJourneyNotImpactedPIADecrease kickout when false" in {

        val userAnswers = emptyUserAnswers
          .set(Contribution4000ToDirectContributionSchemePage, false)
          .success
          .value

        val nextPageUrl: String = Contribution4000ToDirectContributionSchemePage.navigate(CheckMode, userAnswers).url

        checkNavigation(nextPageUrl, "/triage-journey-not-eligible-PIA-decrease")
      }

      "to journey recovery when not answered" in {

        val nextPageUrl: String =
          Contribution4000ToDirectContributionSchemePage.navigate(NormalMode, emptyUserAnswers).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "when yes do not cleanup" in {

        val cleanedUserAnswers = Contribution4000ToDirectContributionSchemePage
          .cleanup(Some(true), testCalulationServiceData)
          .success
          .value

        // AA Triage Answers
        cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
        cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe Some(true)
        cleanedUserAnswers.get(WhichYearsScottishTaxpayerPage) mustBe Some(Set(WhichYearsScottishTaxpayer._2017))
        cleanedUserAnswers.get(PayingPublicPensionSchemePage) mustBe Some(false)
        cleanedUserAnswers.get(StopPayingPublicPensionPage) mustBe Some(LocalDate.of(2021, 1, 1))
        cleanedUserAnswers.get(DefinedContributionPensionSchemePage) mustBe Some(true)
        cleanedUserAnswers.get(PayTaxCharge1415Page) mustBe Some(false)
        cleanedUserAnswers.get(PIAPreRemedyPage(_2013)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(PIAPreRemedyPage(_2014)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(PIAPreRemedyPage(_2015)) mustBe Some(BigInt(123))

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(PensionSchemeDetailsPage(_2021, SchemeIndex(0))) mustBe Some(
          PensionSchemeDetails("schemeName", "schemeRef")
        )
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0))) mustBe Some(
          PensionSchemeInputAmounts(BigInt(123))
        )
        cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(0))) mustBe Some(true)
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(AddAnotherSchemePage(_2021, SchemeIndex(0))) mustBe Some(true)
        cleanedUserAnswers.get(WhichSchemePage(_2021, SchemeIndex(1))) mustBe Some("schemeName")
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1))) mustBe Some(
          PensionSchemeInputAmounts(BigInt(123))
        )
        cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(1))) mustBe Some(true)
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(1))) mustBe Some(WhoPaidAACharge.Both)
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(OtherDefinedBenefitOrContributionPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(ContributedToDuringRemedyPeriodPage(_2021)) mustBe Some(
          Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
        )
        cleanedUserAnswers.get(DefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(_2021)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(DefinedBenefitAmountPage(_2021)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(ThresholdIncomePage(_2021)) mustBe Some(ThresholdIncome.Yes)
        cleanedUserAnswers.get(TotalIncomePage(_2021)) mustBe Some(BigInt(220000))
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(TaxReliefPage(_2021)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(RASContributionAmountPage(_2021)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(KnowAdjustedAmountPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(AdjustedIncomePage(_2021)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(AmountOfGiftAidPage(_2021)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(PersonalAllowancePage(_2021)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(BlindAllowancePage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2021)) mustBe Some(BigInt(1))

        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2022)) mustBe Some(false)
        cleanedUserAnswers.get(PensionSchemeDetailsPage(_2022, SchemeIndex(0))) mustBe Some(
          PensionSchemeDetails("schemeName", "schemeRef")
        )
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0))) mustBe Some(
          PensionSchemeInputAmounts(BigInt(123))
        )
        cleanedUserAnswers.get(PayAChargePage(_2022, SchemeIndex(0))) mustBe Some(true)
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2022, SchemeIndex(0))) mustBe Some(WhoPaidAACharge.Both)
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0))) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(ThresholdIncomePage(_2022)) mustBe Some(ThresholdIncome.No)
        cleanedUserAnswers.get(TotalIncomePage(_2022)) mustBe Some(BigInt(123))
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe Some(true)
        cleanedUserAnswers.get(TaxReliefPage(_2022)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2022)) mustBe Some(true)
        cleanedUserAnswers.get(RASContributionAmountPage(_2022)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2022)) mustBe Some(true)
        cleanedUserAnswers.get(AmountOfGiftAidPage(_2022)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2022)) mustBe Some(true)
        cleanedUserAnswers.get(PersonalAllowancePage(_2022)) mustBe Some(BigInt(1))
        cleanedUserAnswers.get(BlindAllowancePage(_2022)) mustBe Some(true)
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2022)) mustBe Some(BigInt(1))
      }
      "when no clean up AA task data" in {

        val cleanedUserAnswers = Contribution4000ToDirectContributionSchemePage
          .cleanup(Some(false), testCalulationServiceData)
          .success
          .value

        // AA Triage Answers
        cleanedUserAnswers.get(SavingsStatementPage) mustBe Some(true)
        cleanedUserAnswers.get(PensionProtectedMemberPage) mustBe Some(true)
        cleanedUserAnswers.get(MaybePIAIncreasePage) mustBe Some(MaybePIAIncrease.No)
        cleanedUserAnswers.get(PIAAboveAnnualAllowanceIn2023Page) mustBe Some(false)
        cleanedUserAnswers.get(NetIncomeAbove190KIn2023Page) mustBe Some(false)
        cleanedUserAnswers.get(FlexibleAccessDcSchemePage) mustBe Some(true)
        cleanedUserAnswers.get(Contribution4000ToDirectContributionSchemePage) mustBe Some(false)

        // AA Setup Answers
        cleanedUserAnswers.get(ScottishTaxpayerFrom2016Page) mustBe None
        cleanedUserAnswers.get(WhichYearsScottishTaxpayerPage) mustBe None
        cleanedUserAnswers.get(PayingPublicPensionSchemePage) mustBe None
        cleanedUserAnswers.get(StopPayingPublicPensionPage) mustBe None
        cleanedUserAnswers.get(DefinedContributionPensionSchemePage) mustBe None
        cleanedUserAnswers.get(PayTaxCharge1415Page) mustBe None
        cleanedUserAnswers.get(PIAPreRemedyPage(_2013)) mustBe None
        cleanedUserAnswers.get(PIAPreRemedyPage(_2014)) mustBe None
        cleanedUserAnswers.get(PIAPreRemedyPage(_2015)) mustBe None

        // AALoop Answers
        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2021)) mustBe None
        cleanedUserAnswers.get(PensionSchemeDetailsPage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(AddAnotherSchemePage(_2021, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(WhichSchemePage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(PayAChargePage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1))) mustBe None
        cleanedUserAnswers.get(OtherDefinedBenefitOrContributionPage(_2021)) mustBe None
        cleanedUserAnswers.get(ContributedToDuringRemedyPeriodPage(_2021)) mustBe None
        cleanedUserAnswers.get(DefinedContributionAmountPage(_2021)) mustBe None
        cleanedUserAnswers.get(FlexiAccessDefinedContributionAmountPage(_2021)) mustBe None
        cleanedUserAnswers.get(DefinedBenefitAmountPage(_2021)) mustBe None
        cleanedUserAnswers.get(ThresholdIncomePage(_2021)) mustBe None
        cleanedUserAnswers.get(TotalIncomePage(_2021)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe None
        cleanedUserAnswers.get(TaxReliefPage(_2021)) mustBe None
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2021)) mustBe None
        cleanedUserAnswers.get(RASContributionAmountPage(_2021)) mustBe None
        cleanedUserAnswers.get(KnowAdjustedAmountPage(_2021)) mustBe None
        cleanedUserAnswers.get(AdjustedIncomePage(_2021)) mustBe None
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2021)) mustBe None
        cleanedUserAnswers.get(AmountOfGiftAidPage(_2021)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2021)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(_2021)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(_2021)) mustBe None
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2021)) mustBe None

        cleanedUserAnswers.get(MemberMoreThanOnePensionPage(_2022)) mustBe None
        cleanedUserAnswers.get(PensionSchemeDetailsPage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(PayAChargePage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(WhoPaidAAChargePage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0))) mustBe None
        cleanedUserAnswers.get(ThresholdIncomePage(_2022)) mustBe None
        cleanedUserAnswers.get(TotalIncomePage(_2022)) mustBe None
        cleanedUserAnswers.get(ClaimingTaxReliefPensionPage(_2021)) mustBe None
        cleanedUserAnswers.get(TaxReliefPage(_2022)) mustBe None
        cleanedUserAnswers.get(DidYouContributeToRASSchemePage(_2022)) mustBe None
        cleanedUserAnswers.get(RASContributionAmountPage(_2022)) mustBe None
        cleanedUserAnswers.get(DoYouHaveGiftAidPage(_2022)) mustBe None
        cleanedUserAnswers.get(AmountOfGiftAidPage(_2022)) mustBe None
        cleanedUserAnswers.get(DoYouKnowPersonalAllowancePage(_2022)) mustBe None
        cleanedUserAnswers.get(PersonalAllowancePage(_2022)) mustBe None
        cleanedUserAnswers.get(BlindAllowancePage(_2022)) mustBe None
        cleanedUserAnswers.get(BlindPersonsAllowanceAmountPage(_2022)) mustBe None

      }
    }
  }
}
