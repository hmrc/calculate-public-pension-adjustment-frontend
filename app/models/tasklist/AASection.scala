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

import models.{Period, SchemeIndex, UserAnswers}
import pages.Page
import pages.annualallowance.taxyear._

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
      OtherDefinedBenefitOrContributionPage(period, schemeIndex),
      DefinedBenefitAmountPage(period, schemeIndex),
      DefinedContributionAmountPage(period, schemeIndex),
      ContributedToDuringRemedyPeriodPage(period, schemeIndex),
      FlexiAccessDefinedContributionAmountPage(period, schemeIndex),
      ThresholdIncomePage(period, schemeIndex),
      AdjustedIncomePage(period, schemeIndex),
      TotalIncomePage(period, schemeIndex)
    )

  override def status(answers: UserAnswers): SectionStatus =
    if (answers.get(MemberMoreThanOnePensionPage(period)).isDefined) {
      answers.get(TotalIncomePage(period, schemeIndex)) match {
        case Some(_) => SectionStatus.Completed
        case None    => SectionStatus.InProgress
      }
    } else SectionStatus.NotStarted

  override def checkYourAnswersPage: Page = CheckYourAAPeriodAnswersPage(period)
}
