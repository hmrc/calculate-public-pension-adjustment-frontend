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

import controllers.routes
import controllers.annualallowance.preaaquestions.{routes => preAARoutes}
import models.TaxYear.{TaxYear2012, TaxYear2013, TaxYear2014}
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object PayTaxCharge1516Page extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "payTaxCharge1516"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PayTaxCharge1516Page) match {
      case Some(false) => preAARoutes.PIAPreRemedyController.onPageLoad(NormalMode, TaxYear2012)
      case Some(true)  => routes.CheckYourAnswersController.onPageLoad // TODO once subsequent page is implemented
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PayTaxCharge1516Page) match {
      case Some(false) => preAARoutes.PIAPreRemedyController.onPageLoad(NormalMode, TaxYear2012)
      case Some(true)  => routes.CheckYourAnswersController.onPageLoad
      case _           => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => super.cleanup(value, userAnswers)
        case true  =>
          userAnswers
            .remove(PIAPreRemedyPage(TaxYear2012))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(TaxYear2013)))
            .flatMap(_.remove(preaaquestions.PIAPreRemedyPage(TaxYear2014)))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}