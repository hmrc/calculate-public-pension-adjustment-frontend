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

package models.tasklist

import models.ContributedToDuringRemedyPeriod.Definedbenefit
import models.{ContributedToDuringRemedyPeriod, Period, SchemeIndex, UserAnswers}
import pages.Page
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.annualallowance.taxyear._
import models.WhoPaidAACharge.{Both, Scheme, You}

case class AASection(period: Period, schemeIndex: SchemeIndex) extends Section {

  override def pages(): Seq[Page] =
    Seq(
      WhatYouWillNeedPage(period),
      MemberMoreThanOnePensionPage(period),
      WhichSchemePage(period, schemeIndex),
      PensionSchemeDetailsPage(period, schemeIndex),
      PensionSchemeInputAmountsPage(period, schemeIndex),
      PayAChargePage(period, schemeIndex),
      WhoPaidAAChargePage(period, schemeIndex),
      HowMuchAAChargeYouPaidPage(period, schemeIndex),
      HowMuchAAChargeSchemePaidPage(period, schemeIndex),
      AddAnotherSchemePage(period, schemeIndex),
      OtherDefinedBenefitOrContributionPage(period),
      DefinedBenefitAmountPage(period),
      DefinedContributionAmountPage(period),
      ContributedToDuringRemedyPeriodPage(period),
      FlexiAccessDefinedContributionAmountPage(period),
      ThresholdIncomePage(period),
      AdjustedIncomePage(period),
      TotalIncomePage(period)
    )

  override def status(answers: UserAnswers): SectionStatus =
    if (firstPageIsAnswered(answers)) {
      if (isFirstPeriod) {
        statusInFirstPeriod(answers)
      } else {
        statusInSubsequentPeriod(answers)
      }
    } else SectionStatus.NotStarted

  private def statusInFirstPeriod(answers: UserAnswers) =
    answers.get(DefinedContributionPensionSchemePage) match {
      case Some(true)  => statusInDefinedBenefitOrContributionSection(answers)
      case Some(false) => statusOfPayACharge(answers)
      case None        => SectionStatus.InProgress
    }

  private def statusInDefinedBenefitOrContributionSection(answers: UserAnswers) =
    answers.get(OtherDefinedBenefitOrContributionPage(period)) match {
      case Some(true)  =>
        answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
          case Some(contributions) => statusWhenContributionsInPeriod(answers, contributions)
          case None                => SectionStatus.InProgress
        }
      case Some(false) => statusWhenNoContributionsInPeriod(answers)
      case None        => SectionStatus.InProgress
    }

  private def statusInSubsequentPeriod(answers: UserAnswers) =
    answers.get(TotalIncomePage(period)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def statusWhenNoContributionsInPeriod(answers: UserAnswers) =
    answers.get(OtherDefinedBenefitOrContributionPage(period)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def statusWhenContributionsInPeriod(
    answers: UserAnswers,
    contributions: Set[ContributedToDuringRemedyPeriod]
  ) =
    if (contributions.contains(Definedbenefit)) {
      statusWhenDefinedBenefitAmountSpecified(answers)
    } else {
      statusWhenNoDefinedBenefitAmountSpecified(answers)
    }

  private def statusWhenNoDefinedBenefitAmountSpecified(answers: UserAnswers) =
    answers.get(DefinedContributionAmountPage(period)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def statusWhenDefinedBenefitAmountSpecified(answers: UserAnswers) =
    answers.get(DefinedBenefitAmountPage(period)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def statusOfPayACharge(answers: UserAnswers) =
    answers.get(PayAChargePage(period, schemeIndex)) match {
      case Some(true)  => whoPaidChargeCheck(answers: UserAnswers)
      case Some(false) => SectionStatus.Completed
      case None        => SectionStatus.InProgress
    }

  private def whoPaidChargeCheck(answers: UserAnswers) =
    answers.get(WhoPaidAAChargePage(period, schemeIndex)) match {
      case Some(You)    => whenUserPaid(answers)
      case Some(Scheme) => whenSchemePaid(answers)
      case Some(Both)   => whenSchemePaid(answers)
      case None         => SectionStatus.InProgress
    }

  private def whenUserPaid(answers: UserAnswers) =
    answers.get(HowMuchAAChargeYouPaidPage(period, schemeIndex)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def whenSchemePaid(answers: UserAnswers) =
    answers.get(HowMuchAAChargeSchemePaidPage(period, schemeIndex)) match {
      case Some(_) => SectionStatus.Completed
      case None    => SectionStatus.InProgress
    }

  private def isFirstPeriod =
    period == Period._2016PreAlignment

  private def firstPageIsAnswered(answers: UserAnswers) =
    answers.get(MemberMoreThanOnePensionPage(period)).isDefined

  override def checkYourAnswersPage: Page = CheckYourAAPeriodAnswersPage(period)
}
