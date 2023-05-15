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

import controllers.routes
import models.TaxYear.{TaxYear2012, TaxYear2013, TaxYear2014}
import models.{NormalMode, TaxYear, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class PIAPreRemedyPage(taxYear: TaxYear) extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ taxYear.value.toString \ toString

  override def toString: String = "pIAPreRemedy"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PIAPreRemedyPage(taxYear)) match {
      case Some(_) => navigateInNormalMode
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateInNormalMode =
    if (taxYear == TaxYear2012 || taxYear == TaxYear2013) {
      routes.PIAPreRemedyController.onPageLoad(NormalMode, TaxYear(taxYear.value + 1))
    } else if (taxYear == TaxYear2014) {
      routes.CheckYourAnswersController.onPageLoad
    } else routes.JourneyRecoveryController.onPageLoad(None)

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PIAPreRemedyPage(taxYear)) match {
      case Some(_) => routes.CheckYourAnswersController.onPageLoad
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }
}
