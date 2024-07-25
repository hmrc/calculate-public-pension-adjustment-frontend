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

import models.{AboveThreshold, NormalMode, Period, ThresholdIncome, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class RASContributionAmountPage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "rASContributionAmount"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(RASContributionAmountPage(period)) match {
      case Some(_) => is2016Period(answers, period)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RASContributionAmountPage(period)) match {
      case Some(_) => is2016Period(answers, period)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def is2016Period(answers: UserAnswers, period: Period): Call =
    if (period == Period._2016) {
      controllers.annualallowance.taxyear.routes.DoYouHaveGiftAidController.onPageLoad(NormalMode, period)
    } else {
      thresholdAnswer(answers, period)
    }

  private def thresholdAnswer(answers: UserAnswers, period: Period): Call =
    answers.get(ThresholdIncomePage(period)) match {
      case Some(ThresholdIncome.Yes)        =>
        controllers.annualallowance.taxyear.routes.KnowAdjustedAmountController.onPageLoad(NormalMode, period)
      case Some(ThresholdIncome.No)         =>
        controllers.annualallowance.taxyear.routes.DoYouHaveGiftAidController.onPageLoad(NormalMode, period)
      case Some(ThresholdIncome.IDoNotKnow) => thresholdStatus(answers, period)
      case _                                => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  private def thresholdStatus(answers: UserAnswers, period: Period): Call =
    answers.get(AboveThreshold(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.KnowAdjustedAmountController.onPageLoad(NormalMode, period)
      case Some(false) =>
        controllers.annualallowance.taxyear.routes.DoYouHaveGiftAidController.onPageLoad(NormalMode, period)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] = {
    val thesholdAnswer = userAnswers.get(ThresholdIncomePage(period))
    if (thesholdAnswer.contains(ThresholdIncome.Yes)) {
      value
        .map { _ =>
          userAnswers
            .remove(AnyLumpSumDeathBenefitsPage(period))
            .flatMap(_.remove(LumpSumDeathBenefitsValuePage(period)))
            .flatMap(_.remove(AdjustedIncomePage(period)))
            .flatMap(_.remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)))
            .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
            .flatMap(_.remove(HowMuchContributionPensionSchemePage(period)))
            .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
            .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
            .flatMap(_.remove(DoYouHaveGiftAidPage(period)))
            .flatMap(_.remove(AmountOfGiftAidPage(period)))
            .flatMap(_.remove(DoYouKnowPersonalAllowancePage(period)))
            .flatMap(_.remove(PersonalAllowancePage(period)))
            .flatMap(_.remove(BlindAllowancePage(period)))
            .flatMap(_.remove(BlindPersonsAllowanceAmountPage(period)))
        }
        .getOrElse(super.cleanup(value, userAnswers))
    } else {
      value
        .map { _ =>
          userAnswers
            .remove(KnowAdjustedAmountPage(period))
            .flatMap(_.remove(AdjustedIncomePage(period)))
            .flatMap(_.remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)))
            .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
            .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
            .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
            .flatMap(_.remove(DoYouHaveGiftAidPage(period)))
            .flatMap(_.remove(AmountOfGiftAidPage(period)))
            .flatMap(_.remove(DoYouKnowPersonalAllowancePage(period)))
            .flatMap(_.remove(PersonalAllowancePage(period)))
            .flatMap(_.remove(BlindAllowancePage(period)))
            .flatMap(_.remove(BlindPersonsAllowanceAmountPage(period)))
        }
        .getOrElse(super.cleanup(value, userAnswers))
    }
  }
}
