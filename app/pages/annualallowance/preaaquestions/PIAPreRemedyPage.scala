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

import controllers.routes
import models.{NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class PIAPreRemedyPage(period: Period) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ period.toString \ toString

  override def toString: String = "pIAPreRemedy"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PIAPreRemedyPage(period)) match {
      case Some(_) => navigateInNormalMode
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateInNormalMode =
    if (period == Period._2011 || period == Period._2012 || period == Period._2013 || period == Period._2014) {
      controllers.annualallowance.preaaquestions.routes.RegisteredYearController
        .onPageLoad(NormalMode, Period.Year(period.end.getYear + 1))
    } else if (period == Period._2015) {
      controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
    } else routes.JourneyRecoveryController.onPageLoad(None)

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PIAPreRemedyPage(period)) match {
      case Some(_) => controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }
}
