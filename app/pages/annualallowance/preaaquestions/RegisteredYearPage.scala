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

package pages.annualallowance.preaaquestions

import models.{NormalMode, Period, UserAnswers}
import controllers.annualallowance.preaaquestions.{routes => preAARoutes}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class RegisteredYearPage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ period.toString \ toString

  override def toString: String = "registeredYear"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(RegisteredYearPage(period)), period) match {
      case (Some(true), Period._2011 | Period._2012 | Period._2013 | Period._2014)  =>
        preAARoutes.PIAPreRemedyController.onPageLoad(NormalMode, period)
      case (Some(false), Period._2011 | Period._2012 | Period._2013 | Period._2014) =>
        preAARoutes.RegisteredYearController.onPageLoad(NormalMode, Period.Year(period.end.getYear + 1))
      case (Some(false), Period._2015)                                              =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _                                                                        =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RegisteredYearPage(period)) match {
      case Some(_) => controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
