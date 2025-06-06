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

import models.{CheckMode, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class RegisteredYearPage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ period.toString \ toString

  override def toString: String = "registeredYear"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(RegisteredYearPage(period)), period) match {
      case (Some(true), Period._2011 | Period._2012 | Period._2013 | Period._2014 | Period._2015) =>
        controllers.annualallowance.preaaquestions.routes.PIAPreRemedyController.onPageLoad(NormalMode, period)
      case (Some(false), Period._2011 | Period._2012 | Period._2013 | Period._2014)               =>
        controllers.annualallowance.preaaquestions.routes.RegisteredYearController
          .onPageLoad(NormalMode, Period.Year(period.end.getYear + 1))
      case (Some(false), Period._2015)                                                            =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _                                                                                      =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RegisteredYearPage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.preaaquestions.routes.PIAPreRemedyController.onPageLoad(CheckMode, period)
      case Some(false) =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          userAnswers
            .remove(PIAPreRemedyPage(period))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
