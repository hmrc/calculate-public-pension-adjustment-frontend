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

case class KnowAdjustedAmountPage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "knowAdjustedAmount"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(KnowAdjustedAmountPage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.AdjustedIncomeController.onPageLoad(NormalMode, period)
      case Some(false) =>
        answers.get(ThresholdIncomePage(period)) match {
          case Some(ThresholdIncome.IDoNotKnow) =>
            controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionNotAdjustedIncomeController
              .onPageLoad(NormalMode, period)
          case Some(ThresholdIncome.Yes)        =>
            controllers.annualallowance.taxyear.routes.AnyLumpSumDeathBenefitsController
              .onPageLoad(NormalMode, period)
          case _                                =>
            controllers.routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(KnowAdjustedAmountPage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.AdjustedIncomeController.onPageLoad(CheckMode, period)
      case Some(false) =>
        answers.get(ThresholdIncomePage(period)) match {
          case Some(ThresholdIncome.IDoNotKnow) =>
            controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionNotAdjustedIncomeController
              .onPageLoad(NormalMode, period)
          case Some(ThresholdIncome.Yes)        =>
            controllers.annualallowance.taxyear.routes.AnyLumpSumDeathBenefitsController
              .onPageLoad(NormalMode, period)
          case _                                =>
            controllers.routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val thresholdStatus = userAnswers.get(ThresholdIncomePage(period))
    value
      .map {
        case true  =>
          if (thresholdStatus.contains(ThresholdIncome.Yes)) {
            userAnswers
              .remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period))
              .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
              .flatMap(_.remove(HowMuchContributionPensionSchemePage(period)))
              .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
              .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
              .flatMap(_.remove(AnyLumpSumDeathBenefitsPage(period)))
              .flatMap(_.remove(LumpSumDeathBenefitsValuePage(period)))
          } else {
            userAnswers
              .remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period))
              .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
              .flatMap(_.remove(HowMuchContributionPensionSchemePage(period)))
              .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
              .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
          }
        case false => userAnswers.remove(AdjustedIncomePage(period))
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }

}
