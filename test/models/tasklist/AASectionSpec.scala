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

import base.SpecBase
import models.{ContributedToDuringRemedyPeriod, Period, SchemeIndex}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import pages.annualallowance.taxyear.{ContributedToDuringRemedyPeriodPage, DefinedBenefitAmountPage, DefinedContributionAmountPage, MemberMoreThanOnePensionPage, OtherDefinedBenefitOrContributionPage, PayAChargePage, TotalIncomePage, WhoPaidAAChargePage}

class AASectionSpec extends SpecBase {

  "Status is first period" - {

    "when user has answered defined contribution as No in AA setup questions" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(PayAChargePage(Period._2016PreAlignment, SchemeIndex(0)), false)
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "when user has answered defined contribution as yes in AA setup questions but not answered pay a charge page" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "when user has defined benefit then status is complete when defined benefit amount page is answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), true)
        .get
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016PreAlignment),
          Set(ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .get
        .set(DefinedBenefitAmountPage(Period._2016PreAlignment), BigInt(999))
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "when user has defined benefit then status is in progress when defined benefit amount page is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), true)
        .get
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016PreAlignment),
          Set(ContributedToDuringRemedyPeriod.values.tail.head)
        )
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "when user has not answered if defined contribution or defined benefit then the status is in progress" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), true)
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "when user has defined contribution then status is complete when defined contribution amount page is answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), true)
        .get
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016PreAlignment),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .get
        .set(DefinedContributionAmountPage(Period._2016PreAlignment), BigInt(999))
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "when user has defined contribution then status is in progress when defined contribution amount page is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), true)
        .get
        .set(
          ContributedToDuringRemedyPeriodPage(Period._2016PreAlignment),
          Set(ContributedToDuringRemedyPeriod.values.head)
        )
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

    "when user has other defined contribution and benefit is answered as false" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get
        .set(OtherDefinedBenefitOrContributionPage(Period._2016PreAlignment), false)
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "when user has other defined contribution and benefit is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get

      val status = AASection(Period._2016PreAlignment, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }
  }

  "Status is not first period" - {

    "when user has defined benefit then status is complete when defined benefit amount page is answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2017), true)
        .get
        .set(TotalIncomePage(Period._2017), BigInt(999))
        .get

      val status = AASection(Period._2017, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.Completed)
    }

    "when user has defined benefit then status is complete when defined benefit amount page is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, false)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2017), true)
        .get

      val status = AASection(Period._2017, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

  }
}
