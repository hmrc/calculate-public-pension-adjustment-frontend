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

import controllers.annualallowance.taxyear.routes.{InterestFromSavingsController, TaxReliefController}
import models.{CheckMode, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class ClaimingTaxReliefPensionPage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimingTaxReliefPension"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(ClaimingTaxReliefPensionPage(period)) match {
      case Some(true) => TaxReliefController.onPageLoad(NormalMode, period)
      case Some(false) => InterestFromSavingsController.onPageLoad(NormalMode, period)
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(ClaimingTaxReliefPensionPage(period)) match {
      case Some(true) => TaxReliefController.onPageLoad(CheckMode, period)
      case Some(false) => InterestFromSavingsController.onPageLoad(CheckMode, period)
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
