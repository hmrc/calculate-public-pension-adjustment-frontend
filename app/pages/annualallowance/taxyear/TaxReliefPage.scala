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

import models.{CheckMode, NormalMode, Period, ThresholdIncome, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class TaxReliefPage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "taxRelief"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(TaxReliefPage(period)) match {
      case Some(_) => is2016Period(answers, period)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(TaxReliefPage(period)) match {
      case Some(_) => is2016Period(answers, period)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def is2016Period(answers: UserAnswers, period: Period): Call =
    if (period == Period._2016) {
      controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController.onPageLoad(NormalMode, period)
    } else {
      thresholdAnswer(answers, period)
    }

  private def thresholdAnswer(answers: UserAnswers, period: Period): Call =
    answers.get(ThresholdIncomePage(period)) match {
      case Some(ThresholdIncome.Yes)        =>
        controllers.annualallowance.taxyear.routes.KnowAdjustedAmountController.onPageLoad(NormalMode, period)
      case Some(ThresholdIncome.No)         =>
        controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController.onPageLoad(NormalMode, period)
      case Some(ThresholdIncome.IDoNotKnow) => thresholdStatus(answers, period)
      case _                                => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def thresholdStatus(answers: UserAnswers, period: Period): Call =
    if (period == Period._2016 || period == Period._2017 || period == Period._2018 || period == Period._2019) {
      thresholdRoutingPre2020(answers, period)
    } else {
      thresholdRoutingPost2019(answers, period)
    }

  private def thresholdRoutingPre2020(answers: UserAnswers, period: Period) =
    calculateThresholdStatus(answers, period) match {
      case a if a > 110000 =>
        controllers.annualallowance.taxyear.routes.KnowAdjustedAmountController.onPageLoad(NormalMode, period)
      case b if b < 110000 =>
        controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController.onPageLoad(NormalMode, period)
      case _               => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def thresholdRoutingPost2019(answers: UserAnswers, period: Period) =
    calculateThresholdStatus(answers, period) match {
      case a if a > 200000 =>
        controllers.annualallowance.taxyear.routes.KnowAdjustedAmountController.onPageLoad(NormalMode, period)
      case b if b < 200000 =>
        controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController.onPageLoad(NormalMode, period)
      case _               => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def calculateThresholdStatus(answers: UserAnswers, period: Period): BigInt =
    answers.get(TotalIncomePage(period)).get - answers.get(TaxReliefPage(period)).get +
      answers.get(AmountSalarySacrificeArrangementsPage(period)).getOrElse(BigInt(0)) + answers
        .get(AmountFlexibleRemunerationArrangementsPage(period))
        .getOrElse(BigInt(0)) -
      answers.get(HowMuchContributionPensionSchemePage(period)).get - answers
        .get(LumpSumDeathBenefitsValuePage(period))
        .getOrElse(BigInt(0))

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { _ =>
        userAnswers
          .remove(KnowAdjustedAmountPage(period))
          .flatMap(_.remove(AdjustedIncomePage(period)))
          .flatMap(_.remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)))
          .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
          .flatMap(_.remove(AreYouNonDomPage(period)))
          .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
          .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
          .flatMap(_.remove(DoYouKnowPersonalAllowancePage(period)))
          .flatMap(_.remove(PersonalAllowancePage(period)))
          .flatMap(_.remove(MarriageAllowancePage(period)))
          .flatMap(_.remove(MarriageAllowanceAmountPage(period)))
          .flatMap(_.remove(BlindAllowancePage(period)))
          .flatMap(_.remove(BlindPersonsAllowanceAmountPage(period)))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
