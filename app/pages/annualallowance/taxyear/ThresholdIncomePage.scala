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

import controllers.annualallowance.taxyear.routes.TotalIncomeController
import models.{NormalMode, Period, ThresholdIncome, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class ThresholdIncomePage(period: Period) extends QuestionPage[ThresholdIncome] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "thresholdIncome"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ThresholdIncomePage(period)) match {
      case Some(_) => TotalIncomeController.onPageLoad(NormalMode, period)
      case None    => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ThresholdIncomePage(period)) match {
      case Some(_) => TotalIncomeController.onPageLoad(NormalMode, period)
      case None    => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[ThresholdIncome], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { _ =>
        userAnswers
          .remove(TotalIncomePage(period))
          .flatMap(_.remove(AnySalarySacrificeArrangementsPage(period)))
          .flatMap(_.remove(AmountSalarySacrificeArrangementsPage(period)))
          .flatMap(_.remove(FlexibleRemunerationArrangementsPage(period)))
          .flatMap(_.remove(AmountFlexibleRemunerationArrangementsPage(period)))
          .flatMap(_.remove(AnyLumpSumDeathBenefitsPage(period)))
          .flatMap(_.remove(LumpSumDeathBenefitsValuePage(period)))
          .flatMap(_.remove(ClaimingTaxReliefPensionPage(period)))
          .flatMap(_.remove(TaxReliefPage(period)))
          .flatMap(_.remove(KnowAdjustedAmountPage(period)))
          .flatMap(_.remove(AdjustedIncomePage(period)))
          .flatMap(_.remove(ClaimingTaxReliefPensionNotAdjustedIncomePage(period)))
          .flatMap(_.remove(HowMuchTaxReliefPensionPage(period)))
          .flatMap(_.remove(HowMuchContributionPensionSchemePage(period)))
          .flatMap(_.remove(HasReliefClaimedOnOverseasPensionPage(period)))
          .flatMap(_.remove(AmountClaimedOnOverseasPensionPage(period)))
          .flatMap(_.remove(DoYouHaveGiftAidPage(period)))
          .flatMap(_.remove(AmountOfGiftAidPage(period)))
          .flatMap(_.remove(DoYouKnowPersonalAllowancePage(period)))
          .flatMap(_.remove(TradeUnionReliefPage(period)))
          .flatMap(_.remove(UnionPoliceReliefAmountPage(period)))
          .flatMap(_.remove(PersonalAllowancePage(period)))
          .flatMap(_.remove(BlindAllowancePage(period)))
          .flatMap(_.remove(BlindPersonsAllowanceAmountPage(period)))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
