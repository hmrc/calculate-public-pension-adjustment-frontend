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

package pages.annualallowance.preaaquestions

import controllers.annualallowance.preaaquestions.routes as preAARoutes
import controllers.routes
import models.{NormalMode, Period, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object PayTaxCharge1415Page extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "payTaxCharge1415"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PayTaxCharge1415Page) match {
      case Some(false) =>
        preAARoutes.RegisteredYearController.onPageLoad(NormalMode, Period._2011)
      case Some(true)  =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PayTaxCharge1415Page) match {
      case Some(false) => preAARoutes.RegisteredYearController.onPageLoad(NormalMode, Period._2011)
      case Some(true)  =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => super.cleanup(value, userAnswers)
        case true  =>
          userAnswers
            .remove(PIAPreRemedyPage(Period._2011))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(Period._2012)))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(Period._2013)))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(Period._2014)))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(Period._2015)))
            .flatMap(_.remove(preaaquestions.RegisteredYearPage(Period._2011)))
            .flatMap(_.remove(preaaquestions.RegisteredYearPage(Period._2012)))
            .flatMap(_.remove(preaaquestions.RegisteredYearPage(Period._2013)))
            .flatMap(_.remove(preaaquestions.RegisteredYearPage(Period._2014)))
            .flatMap(_.remove(preaaquestions.RegisteredYearPage(Period._2015)))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
