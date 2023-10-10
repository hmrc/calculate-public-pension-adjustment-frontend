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
import models.Period
import models.tasklist.sections.PreAASection
import pages.annualallowance.preaaquestions.{PIAPreRemedyPage, PayTaxCharge1415Page, RegisteredYearPage, ScottishTaxpayerFrom2016Page}

class PreAASetupSectionSpec extends SpecBase {

  "when user has not answered first page" in {

    val userAnswers = emptyUserAnswers

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.NotStarted)
  }

  "when user has answered ScottishTaxpayerFrom2016 page" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.InProgress)
  }

  "when user answers false PayTaxCharge1415 page" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1415Page, false)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.InProgress)
  }

  "when user answers true PayTaxCharge1415 page" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1415Page, true)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }

  "when user answers false RegisteredYear page in the 2015 period" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1415Page, false)
      .get
      .set(RegisteredYearPage(Period._2015), false)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }

  "when user answers true RegisteredYear page in the 2015 period and has not entered 2015 PIA" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1415Page, false)
      .get
      .set(RegisteredYearPage(Period._2015), true)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.InProgress)
  }

  "when user answers true RegisteredYear page in the 2015 period and has entered 2015 PIA" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1415Page, false)
      .get
      .set(RegisteredYearPage(Period._2015), true)
      .get
      .set(PIAPreRemedyPage(Period._2015), BigInt(1))
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }
}
