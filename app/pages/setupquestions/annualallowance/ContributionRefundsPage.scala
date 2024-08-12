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

package pages.setupquestions.annualallowance

import models.UserAnswers
import pages.QuestionPage
import pages.setupquestions.SavingsStatementPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object ContributionRefundsPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "aa" \ toString

  override def toString: String = "contributionRefunds"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(ContributionRefundsPage), answers.get(SavingsStatementPage)) match {
      case (Some(true), Some(_))      =>
        // TODO to Net income above 100k 16/17 - 19/20
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case (Some(false), Some(true))  =>
        // TODO to have any PIAs increase 15/16 - 21/22
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case (Some(false), Some(false)) =>
        controllers.setupquestions.annualallowance.routes.NotAbleToUseThisServiceAAController.onPageLoad()
      case _                          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ContributionRefundsPage) match {
      case Some(_) => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
