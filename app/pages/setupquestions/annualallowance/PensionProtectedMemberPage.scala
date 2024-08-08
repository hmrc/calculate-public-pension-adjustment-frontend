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

case object PensionProtectedMemberPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionProtectedMember"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(PensionProtectedMemberPage) match {
      case Some(true) =>
        savingsStatementStatus(answers)
      case Some(false) =>
        //TO AA Charge Page
      controllers.routes.JourneyRecoveryController.onPageLoad()
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(PensionProtectedMemberPage) match {
      case Some(_) => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  private def savingsStatementStatus(answers: UserAnswers) = {
    answers.get(SavingsStatementPage) match {
      case Some(true) =>
        //TODO  change with future scaffolds
        // Was your 22/23 PIA > 40k
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case Some(false) =>
        //TODO AA Kickout
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
      case None => controllers.routes.JourneyRecoveryController.onPageLoad()
    }
  }
}
