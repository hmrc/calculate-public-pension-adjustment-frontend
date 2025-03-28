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

package base

import controllers.actions.*
import models.Period.{_2013, _2014, _2015, _2021, _2022}
import models.{AAKickOutStatus, ContributedToDuringRemedyPeriod, EnhancementType, ExcessLifetimeAllowancePaid, LTAKickOutStatus, LtaPensionSchemeDetails, LtaProtectionOrEnhancements, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, NewEnhancementType, NewExcessLifetimeAllowancePaid, PensionSchemeDetails, PensionSchemeInputAmounts, ProtectionEnhancedChanged, ProtectionType, QuarterChargePaid, ReportingChange, SchemeIndex, SchemeNameAndTaxRef, ThresholdIncome, UserAnswers, UserSchemeDetails, WhatNewProtectionTypeEnhancement, WhichYearsScottishTaxpayer, WhoPaidAACharge, WhoPaidLTACharge, WhoPayingExtraLtaCharge, YearChargePaid}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages.annualallowance.preaaquestions.*
import pages.annualallowance.taxyear.*
import pages.lifetimeallowance.*
import pages.setupquestions.ReportingChangePage
import pages.setupquestions.annualallowance.*
import pages.setupquestions.lifetimeallowance.*
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Binding, bind}
import play.api.test.FakeRequest

import java.time.LocalDate

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience {

  val userAnswersId: String = "id"

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  protected def applicationBuilder(
    userAnswers: Option[UserAnswers] = None,
    userIsAuthenticated: Boolean = false
  ): GuiceApplicationBuilder = {

    val identifierActionBinding: Binding[IdentifierAction] = if (userIsAuthenticated) {
      bind[IdentifierAction].to[FakeAuthenticatedIdentifierAction]
    } else {
      bind[IdentifierAction].to[FakeUnauthenticatedIdentifierAction]
    }
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        identifierActionBinding,
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
  }

  def testCalulationServiceData: UserAnswers = {
    emptyUserAnswers
      .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head, ReportingChange.values.tail.head))
      .success
      .value
      .set(SavingsStatementPage, true)
      .success
      .value
      .set(PensionProtectedMemberPage, true)
      .success
      .value
      .set(MaybePIAIncreasePage, MaybePIAIncrease.No)
      .success
      .value
      .set(PIAAboveAnnualAllowanceIn2023Page, false)
      .success
      .value
      .set(NetIncomeAbove190KIn2023Page, false)
      .success
      .value
      .set(FlexibleAccessDcSchemePage, true)
      .success
      .value
      .set(Contribution4000ToDirectContributionSchemePage, false)
      .success
      .value
      .set(HadBenefitCrystallisationEventPage, true)
      .success
      .value
      .set(PreviousLTAChargePage, false)
      .success
      .value
      .set(ChangeInLifetimeAllowancePage, true)
      .success
      .value
      .set(IncreaseInLTAChargePage, true)
      .success
      .value
      .set(NewLTAChargePage, false)
      .success
      .value
      .set(MultipleBenefitCrystallisationEventPage, true)
      .success
      .value
      .set(OtherSchemeNotificationPage, true)
      .success
      .value
      .set(ScottishTaxpayerFrom2016Page, true)
      .success
      .value
      .set(WhichYearsScottishTaxpayerPage, Set[WhichYearsScottishTaxpayer](WhichYearsScottishTaxpayer._2017))
      .success
      .value
      .set(PayingPublicPensionSchemePage, false)
      .success
      .value
      .set(StopPayingPublicPensionPage, LocalDate.of(2021, 1, 1))
      .success
      .value
      .set(DefinedContributionPensionSchemePage, true)
      .success
      .value
      .set(PayTaxCharge1415Page, false)
      .success
      .value
      .set(PIAPreRemedyPage(_2013), BigInt(123))
      .success
      .value
      .set(PIAPreRemedyPage(_2014), BigInt(123))
      .success
      .value
      .set(PIAPreRemedyPage(_2015), BigInt(123))
      .success
      .value
      .set(DateOfBenefitCrystallisationEventPage, LocalDate.of(2021, 1, 1))
      .success
      .value
      .set(LtaProtectionOrEnhancementsPage, LtaProtectionOrEnhancements.Both)
      .success
      .value
      .set(ProtectionTypePage, ProtectionType.EnhancedProtection)
      .success
      .value
      .set(ProtectionReferencePage, "123")
      .success
      .value
      .set(EnhancementTypePage, EnhancementType.InternationalEnhancement)
      .success
      .value
      .set(InternationalEnhancementReferencePage, "123")
      .success
      .value
      .set(PensionCreditReferencePage, "123")
      .success
      .value
      .set(ProtectionEnhancedChangedPage, ProtectionEnhancedChanged.Both)
      .success
      .value
      .set(WhatNewProtectionTypeEnhancementPage, WhatNewProtectionTypeEnhancement.EnhancedProtection)
      .success
      .value
      .set(ReferenceNewProtectionTypeEnhancementPage, "123")
      .success
      .value
      .set(NewEnhancementTypePage, NewEnhancementType.Both)
      .success
      .value
      .set(NewInternationalEnhancementReferencePage, "123")
      .success
      .value
      .set(NewPensionCreditReferencePage, "123")
      .success
      .value
      .set(LifetimeAllowanceChargePage, true)
      .success
      .value
      .set(ExcessLifetimeAllowancePaidPage, ExcessLifetimeAllowancePaid.Both)
      .success
      .value
      .set(LumpSumValuePage, BigInt(123))
      .success
      .value
      .set(AnnualPaymentValuePage, BigInt(123))
      .success
      .value
      .set(WhoPaidLTAChargePage, WhoPaidLTACharge.PensionScheme)
      .success
      .value
      .set(UserSchemeDetailsPage, UserSchemeDetails("schemename", "taxref"))
      .success
      .value
      .set(SchemeNameAndTaxRefPage, SchemeNameAndTaxRef("schemename", "taxref"))
      .success
      .value
      .set(QuarterChargePaidPage, QuarterChargePaid.AprToJul)
      .success
      .value
      .set(YearChargePaidPage, YearChargePaid._2020To2021)
      .success
      .value
      .set(NewExcessLifetimeAllowancePaidPage, NewExcessLifetimeAllowancePaid.Both)
      .success
      .value
      .set(NewLumpSumValuePage, BigInt(123))
      .success
      .value
      .set(NewAnnualPaymentValuePage, BigInt(123))
      .success
      .value
      .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.PensionScheme)
      .success
      .value
      .set(LtaPensionSchemeDetailsPage, LtaPensionSchemeDetails("schemename", "taxref"))
      .success
      .value
      .set(MemberMoreThanOnePensionPage(_2021), true)
      .success
      .value
      .set(PensionSchemeDetailsPage(_2021, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
      .success
      .value
      .set(PensionSchemeInputAmountsPage(_2021, SchemeIndex(0)), PensionSchemeInputAmounts(BigInt(123)))
      .success
      .value
      .set(PayAChargePage(_2021, SchemeIndex(0)), true)
      .success
      .value
      .set(WhoPaidAAChargePage(_2021, SchemeIndex(0)), WhoPaidAACharge.Both)
      .success
      .value
      .set(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(0)), BigInt(123))
      .success
      .value
      .set(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(0)), BigInt(123))
      .success
      .value
      .set(AddAnotherSchemePage(_2021, SchemeIndex(0)), true)
      .success
      .value
      .set(WhichSchemePage(_2021, SchemeIndex(1)), "schemeName")
      .success
      .value
      .set(PensionSchemeInputAmountsPage(_2021, SchemeIndex(1)), PensionSchemeInputAmounts(BigInt(123)))
      .success
      .value
      .set(PayAChargePage(_2021, SchemeIndex(1)), true)
      .success
      .value
      .set(WhoPaidAAChargePage(_2021, SchemeIndex(1)), WhoPaidAACharge.Both)
      .success
      .value
      .set(HowMuchAAChargeYouPaidPage(_2021, SchemeIndex(1)), BigInt(123))
      .success
      .value
      .set(HowMuchAAChargeSchemePaidPage(_2021, SchemeIndex(1)), BigInt(123))
      .success
      .value
      .set(OtherDefinedBenefitOrContributionPage(_2021), true)
      .success
      .value
      .set(
        ContributedToDuringRemedyPeriodPage(_2021),
        Set(ContributedToDuringRemedyPeriod.values.head, ContributedToDuringRemedyPeriod.values.tail.head)
      )
      .success
      .value
      .set(DefinedContributionAmountPage(_2021), BigInt(123))
      .success
      .value
      .set(FlexiAccessDefinedContributionAmountPage(_2021), BigInt(123))
      .success
      .value
      .set(DefinedBenefitAmountPage(_2021), BigInt(123))
      .success
      .value
      .set(ThresholdIncomePage(_2021), ThresholdIncome.Yes)
      .success
      .value
      .set(TotalIncomePage(_2021), BigInt(220000))
      .success
      .value
      .set(ClaimingTaxReliefPensionPage(_2021), true)
      .success
      .value
      .set(TaxReliefPage(_2021), BigInt(1))
      .success
      .value
      .set(DidYouContributeToRASSchemePage(_2021), true)
      .success
      .value
      .set(RASContributionAmountPage(_2021), BigInt(1))
      .success
      .value
      .set(KnowAdjustedAmountPage(_2021), true)
      .success
      .value
      .set(AdjustedIncomePage(_2021), BigInt("1"))
      .success
      .value
      .set(DoYouHaveGiftAidPage(_2021), true)
      .success
      .value
      .set(AmountOfGiftAidPage(_2021), BigInt("1"))
      .success
      .value
      .set(DoYouKnowPersonalAllowancePage(_2021), true)
      .success
      .value
      .set(PersonalAllowancePage(_2021), BigInt(1))
      .success
      .value
      .set(BlindAllowancePage(_2021), true)
      .success
      .value
      .set(BlindPersonsAllowanceAmountPage(_2021), BigInt(1))
      .success
      .value
      .set(MemberMoreThanOnePensionPage(_2022), false)
      .success
      .value
      .set(PensionSchemeDetailsPage(_2022, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeRef"))
      .success
      .value
      .set(PensionSchemeInputAmountsPage(_2022, SchemeIndex(0)), PensionSchemeInputAmounts(BigInt(123)))
      .success
      .value
      .set(PayAChargePage(_2022, SchemeIndex(0)), true)
      .success
      .value
      .set(WhoPaidAAChargePage(_2022, SchemeIndex(0)), WhoPaidAACharge.Both)
      .success
      .value
      .set(HowMuchAAChargeYouPaidPage(_2022, SchemeIndex(0)), BigInt(123))
      .success
      .value
      .set(HowMuchAAChargeSchemePaidPage(_2022, SchemeIndex(0)), BigInt(123))
      .success
      .value
      .set(ThresholdIncomePage(_2022), ThresholdIncome.No)
      .success
      .value
      .set(TotalIncomePage(_2022), BigInt(123))
      .success
      .value
      .set(ClaimingTaxReliefPensionPage(_2022), true)
      .success
      .value
      .set(TaxReliefPage(_2022), BigInt(1))
      .success
      .value
      .set(DidYouContributeToRASSchemePage(_2022), true)
      .success
      .value
      .set(RASContributionAmountPage(_2022), BigInt(1))
      .success
      .value
      .set(DoYouHaveGiftAidPage(_2022), true)
      .success
      .value
      .set(AmountOfGiftAidPage(_2022), BigInt("1"))
      .success
      .value
      .set(DoYouKnowPersonalAllowancePage(_2022), true)
      .success
      .value
      .set(PersonalAllowancePage(_2022), BigInt(1))
      .success
      .value
      .set(BlindAllowancePage(_2022), true)
      .success
      .value
      .set(BlindPersonsAllowanceAmountPage(_2022), BigInt(1))
      .success
      .value
      .set(AAKickOutStatus(), 1)
      .success
      .value
      .set(LTAKickOutStatus(), 1)
      .success
      .value
  }

  def incomeSubJourneyData: UserAnswers =
    emptyUserAnswers
      .set(ThresholdIncomePage(_2022), ThresholdIncome.IDoNotKnow)
      .success
      .value
      .set(TotalIncomePage(_2022), BigInt(2000))
      .success
      .value
      .set(AnySalarySacrificeArrangementsPage(_2022), true)
      .success
      .value
      .set(AmountSalarySacrificeArrangementsPage(_2022), BigInt(1))
      .success
      .value
      .set(FlexibleRemunerationArrangementsPage(_2022), true)
      .success
      .value
      .set(AmountFlexibleRemunerationArrangementsPage(_2022), BigInt(1))
      .success
      .value
      .set(AnyLumpSumDeathBenefitsPage(_2022), true)
      .success
      .value
      .set(LumpSumDeathBenefitsValuePage(_2022), BigInt(1))
      .success
      .value
      .set(ClaimingTaxReliefPensionPage(_2022), true)
      .success
      .value
      .set(TaxReliefPage(_2022), BigInt(1))
      .success
      .value
      .set(DidYouContributeToRASSchemePage(_2022), true)
      .success
      .value
      .set(RASContributionAmountPage(_2022), BigInt(1))
      .success
      .value
      .set(KnowAdjustedAmountPage(_2022), true)
      .success
      .value
      .set(AdjustedIncomePage(_2022), BigInt(1))
      .success
      .value
      .set(ClaimingTaxReliefPensionNotAdjustedIncomePage(_2022), true)
      .success
      .value
      .set(HowMuchTaxReliefPensionPage(_2022), BigInt(1))
      .success
      .value
      .set(HasReliefClaimedOnOverseasPensionPage(_2022), true)
      .success
      .value
      .set(AmountClaimedOnOverseasPensionPage(_2022), BigInt(1))
      .success
      .value
      .set(DoYouHaveGiftAidPage(_2022), true)
      .success
      .value
      .set(AmountOfGiftAidPage(_2022), BigInt("1"))
      .success
      .value
      .set(DoYouKnowPersonalAllowancePage(_2022), true)
      .success
      .value
      .set(PersonalAllowancePage(_2022), BigInt(1))
      .success
      .value
      .set(BlindAllowancePage(_2022), true)
      .success
      .value
      .set(BlindPersonsAllowanceAmountPage(_2022), BigInt(1))
      .success
      .value

  def incomeSubJourneyDataThresholdIncomeYes: UserAnswers =
    emptyUserAnswers
      .set(ThresholdIncomePage(_2022), ThresholdIncome.Yes)
      .success
      .value
      .set(TotalIncomePage(_2022), BigInt(2000))
      .success
      .value
      .set(ClaimingTaxReliefPensionPage(_2022), true)
      .success
      .value
      .set(TaxReliefPage(_2022), BigInt(1))
      .success
      .value
      .set(DidYouContributeToRASSchemePage(_2022), true)
      .success
      .value
      .set(RASContributionAmountPage(_2022), BigInt(1))
      .success
      .value
      .set(KnowAdjustedAmountPage(_2022), false)
      .success
      .value
      .set(AnyLumpSumDeathBenefitsPage(_2022), true)
      .success
      .value
      .set(LumpSumDeathBenefitsValuePage(_2022), BigInt(1))
      .success
      .value
      .set(ClaimingTaxReliefPensionNotAdjustedIncomePage(_2022), true)
      .success
      .value
      .set(HowMuchTaxReliefPensionPage(_2022), BigInt(1))
      .success
      .value
      .set(HasReliefClaimedOnOverseasPensionPage(_2022), true)
      .success
      .value
      .set(AmountClaimedOnOverseasPensionPage(_2022), BigInt(1))
      .success
      .value
      .set(DoYouHaveGiftAidPage(_2022), true)
      .success
      .value
      .set(AmountOfGiftAidPage(_2022), BigInt(1))
      .success
      .value
      .set(DoYouKnowPersonalAllowancePage(_2022), true)
      .success
      .value
      .set(PersonalAllowancePage(_2022), BigInt(1))
      .success
      .value
      .set(BlindAllowancePage(_2022), true)
      .success
      .value
      .set(BlindPersonsAllowanceAmountPage(_2022), BigInt(1))
      .success
      .value

  def userAnswersLTATriage: UserAnswers = emptyUserAnswers
    .set(HadBenefitCrystallisationEventPage, true)
    .success
    .value
    .set(PreviousLTAChargePage, false)
    .success
    .value
    .set(ChangeInLifetimeAllowancePage, true)
    .success
    .value
    .set(IncreaseInLTAChargePage, true)
    .success
    .value
    .set(NewLTAChargePage, true)
    .success
    .value
    .set(MultipleBenefitCrystallisationEventPage, true)
    .success
    .value
    .set(OtherSchemeNotificationPage, true)
    .success
    .value

  def userAnswersAATriage: UserAnswers = emptyUserAnswers
    .set(SavingsStatementPage, true)
    .success
    .value
    .set(PensionProtectedMemberPage, false)
    .success
    .value
    .set(HadAAChargePage, false)
    .success
    .value
    .set(ContributionRefundsPage, true)
    .success
    .value
    .set(NetIncomeAbove100KPage, false)
    .success
    .value
    .set(NetIncomeAbove190KPage, false)
    .success
    .value
    .set(MaybePIAIncreasePage, MaybePIAIncrease.IDoNotKnow)
    .success
    .value
    .set(MaybePIAUnchangedOrDecreasedPage, MaybePIAUnchangedOrDecreased.Yes)
    .success
    .value
    .set(PIAAboveAnnualAllowanceIn2023Page, false)
    .success
    .value
    .set(NetIncomeAbove190KIn2023Page, false)
    .success
    .value
    .set(FlexibleAccessDcSchemePage, true)
    .success
    .value
    .set(Contribution4000ToDirectContributionSchemePage, true)
    .success
    .value
}
