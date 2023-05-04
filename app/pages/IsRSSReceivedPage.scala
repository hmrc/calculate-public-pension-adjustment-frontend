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

package pages

import models.{NormalMode, CheckMode, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes

case object IsRSSReceivedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isRSSReceived"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = answers.get(IsRSSReceivedPage) match {
    case Some(true)  => routes.ResubmittingAdjustmentController.onPageLoad(NormalMode)
    case Some(false) => routes.CheckYourAnswersController.onPageLoad //Redirect to kick out page upon implementation
  }
  
  override protected def navigateInCheckMode(answers: UserAnswers): Call = answers.get(IsRSSReceivedPage) match {
    case Some(true)  => routes.ResubmittingAdjustmentController.onPageLoad(CheckMode)
    case Some(false) => routes.CheckYourAnswersController.onPageLoad //Redirect to kick out page upon implementation
  }
}
