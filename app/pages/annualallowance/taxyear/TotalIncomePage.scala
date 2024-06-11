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

case class TotalIncomePage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "totalIncome"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    if (period != Period._2016) {
      answers.get(ThresholdIncomePage(period)) match {
        case Some(ThresholdIncome.IDoNotKnow) =>
          controllers.annualallowance.taxyear.routes.AnySalarySacrificeArrangementsController
            .onPageLoad(NormalMode, period)
        case Some(ThresholdIncome.Yes)        =>
          controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(NormalMode, period)
        case Some(ThresholdIncome.No)        =>
          controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(NormalMode, period)
        case _                                => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    } else {
      controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(NormalMode, period)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    if (period != Period._2016) {
      answers.get(ThresholdIncomePage(period)) match {
        case Some(ThresholdIncome.IDoNotKnow) =>
          controllers.annualallowance.taxyear.routes.AnySalarySacrificeArrangementsController
            .onPageLoad(NormalMode, period)
        case Some(ThresholdIncome.Yes)        =>
          controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(CheckMode, period)
        case Some(ThresholdIncome.No)        =>
          controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(CheckMode, period)
        case _                                => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      }
    } else {
      controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(CheckMode, period)
    }
}
