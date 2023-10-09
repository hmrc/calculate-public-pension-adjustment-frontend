package models.tasklist

import base.SpecBase
import models.Period
import models.tasklist.sections.PreAASection
import pages.annualallowance.preaaquestions.{PIAPreRemedyPage, PayTaxCharge1516Page, RegisteredYearPage, ScottishTaxpayerFrom2016Page}

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
      .set(PayTaxCharge1516Page, false)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.InProgress)
  }

  "when user answers true PayTaxCharge1415 page" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1516Page, true)
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }

  "when user answers false RegisteredYear page in the 2015 period" in {

    val userAnswers = emptyUserAnswers
      .set(ScottishTaxpayerFrom2016Page, false)
      .get
      .set(PayTaxCharge1516Page, false)
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
      .set(PayTaxCharge1516Page, false)
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
      .set(PayTaxCharge1516Page, false)
      .get
      .set(RegisteredYearPage(Period._2015), true)
      .get
      .set(PIAPreRemedyPage(Period._2015), BigInt(1))
      .get

    val status = PreAASection.status(userAnswers)

    status mustBe (SectionStatus.Completed)
  }
}
