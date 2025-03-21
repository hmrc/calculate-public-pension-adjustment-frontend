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

import models.{NormalMode, Period, ThresholdIncome, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class LumpSumDeathBenefitsValuePage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "lumpSumDeathBenefitsValue"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    if (period != Period._2016) {
      answers.get(LumpSumDeathBenefitsValuePage(period)) match {
        case Some(_) =>
          answers.get(ThresholdIncomePage(period)) match {
            case Some(ThresholdIncome.IDoNotKnow) =>
              controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController
                .onPageLoad(NormalMode, period)
            case Some(ThresholdIncome.Yes)        =>
              controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionNotAdjustedIncomeController
                .onPageLoad(NormalMode, period)
            case _                                =>
              controllers.routes.JourneyRecoveryController.onPageLoad(None)
          }
        case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    } else {
      controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    if (period != Period._2016) {
      answers.get(LumpSumDeathBenefitsValuePage(period)) match {
        case Some(_) =>
          answers.get(ThresholdIncomePage(period)) match {
            case Some(ThresholdIncome.IDoNotKnow) =>
              controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController
                .onPageLoad(NormalMode, period)
            case Some(ThresholdIncome.Yes)        =>
              controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionNotAdjustedIncomeController
                .onPageLoad(NormalMode, period)
            case _                                =>
              controllers.routes.JourneyRecoveryController.onPageLoad(None)
          }
        case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    } else {
      controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.get(ThresholdIncomePage(period)) match {
      case Some(ThresholdIncome.Yes) =>
        value
          .map { _ =>
            userAnswers
              .remove(AdjustedIncomePage(period))
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
      case _                         =>
        value
          .map { _ =>
            userAnswers
              .remove(ClaimingTaxReliefPensionPage(period))
              .flatMap(_.remove(TaxReliefPage(period)))
              .flatMap(_.remove(DidYouContributeToRASSchemePage(period)))
              .flatMap(_.remove(RASContributionAmountPage(period)))
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
}
