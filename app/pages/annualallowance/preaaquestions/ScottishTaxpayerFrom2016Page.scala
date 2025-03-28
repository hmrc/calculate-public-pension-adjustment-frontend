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
import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ScottishTaxpayerFrom2016Page extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "scottishTaxpayerFrom2016"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ScottishTaxpayerFrom2016Page) match {
      case Some(true)  =>
        controllers.annualallowance.preaaquestions.routes.WhichYearsScottishTaxpayerController.onPageLoad(NormalMode)
      case Some(false) =>
        controllers.annualallowance.preaaquestions.routes.PayingPublicPensionSchemeController.onPageLoad(NormalMode)
      case None        =>
        controllers.annualallowance.preaaquestions.routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ScottishTaxpayerFrom2016Page) match {
      case Some(true)  =>
        controllers.annualallowance.preaaquestions.routes.WhichYearsScottishTaxpayerController.onPageLoad(CheckMode)
      case Some(false) =>
        controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false => userAnswers.remove(WhichYearsScottishTaxpayerPage)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
