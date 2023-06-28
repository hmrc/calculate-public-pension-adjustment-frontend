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
import models.{CheckMode, Mode, NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class PayAChargePage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "payACharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PayAChargePage(period, schemeIndex)) match {
      case Some(true)  => navigateToWhoPaidOrHowMuchSchemePaid(NormalMode)
      case Some(false) => AddAnotherSchemeMaybe.navigate(answers, period, schemeIndex)
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PayAChargePage(period, schemeIndex)) match {
      case Some(true)  => navigateToWhoPaidOrHowMuchSchemePaid(CheckMode)
      case Some(false) =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false =>
          for {
            updated1 <- userAnswers.remove(WhoPaidAAChargePage(period, schemeIndex))
            updated2 <- updated1.remove(HowMuchAAChargeYouPaidPage(period, schemeIndex))
            updated3 <- updated2.remove(HowMuchAAChargeSchemePaidPage(period, schemeIndex))
          } yield updated3
        case true  => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def navigateToWhoPaidOrHowMuchSchemePaid(mode: Mode) =
    if (isFirstSchemeInPeriod)
      controllers.annualallowance.taxyear.routes.WhoPaidAAChargeController.onPageLoad(mode, period, schemeIndex)
    else
      controllers.annualallowance.taxyear.routes.HowMuchAAChargeSchemePaidController
        .onPageLoad(mode, period, schemeIndex)

  private def isFirstSchemeInPeriod =
    schemeIndex.value == 0

}
