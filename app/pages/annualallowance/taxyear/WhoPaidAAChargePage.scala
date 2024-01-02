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

import controllers.routes
import models.WhoPaidAACharge.{Both, Scheme, You}
import models.{CheckMode, Mode, NormalMode, Period, SchemeIndex, UserAnswers, WhoPaidAACharge}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class WhoPaidAAChargePage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[WhoPaidAACharge] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "whoPaidAACharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = navigateInEitherMode(answers, NormalMode)

  override protected def navigateInCheckMode(answers: UserAnswers): Call = navigateInEitherMode(answers, CheckMode)

  private def navigateInEitherMode(answers: UserAnswers, mode: Mode): Call =
    answers.get(WhoPaidAAChargePage(period, schemeIndex)) match {
      case Some(You)    =>
        controllers.annualallowance.taxyear.routes.HowMuchAAChargeYouPaidController
          .onPageLoad(mode, period, schemeIndex)
      case Some(Both)   =>
        controllers.annualallowance.taxyear.routes.HowMuchAAChargeYouPaidController
          .onPageLoad(mode, period, schemeIndex)
      case Some(Scheme) =>
        controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
          .onPageLoad(mode, period, schemeIndex)

      case _ => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[WhoPaidAACharge], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { case _ =>
        for {
          updated1 <- userAnswers.remove(HowMuchAAChargeYouPaidPage(period, schemeIndex))
          updated2 <- updated1.remove(HowMuchAAChargeSchemePaidPage(period, schemeIndex))
        } yield updated2
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
