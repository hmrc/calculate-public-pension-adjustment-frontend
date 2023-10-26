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

package pages.annualallowance.taxyear

import controllers.routes
import models.WhoPaidAACharge.{Both, You}
import models.{CheckMode, NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class HowMuchAAChargeYouPaidPage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "howMuchAAChargeYouPaid"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(WhoPaidAAChargePage(period, schemeIndex)) match {
      case Some(You)  =>
        AddAnotherSchemeMaybe.navigate(answers, period, schemeIndex)
      case Some(Both) =>
        controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
          .onPageLoad(NormalMode, period, schemeIndex)

      case _ => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(WhoPaidAAChargePage(period, schemeIndex)) match {
      case Some(You)  =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController
          .onPageLoad(period)
      case Some(Both) =>
        controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
          .onPageLoad(CheckMode, period, schemeIndex)

      case _ => routes.JourneyRecoveryController.onPageLoad(None)
    }
}
