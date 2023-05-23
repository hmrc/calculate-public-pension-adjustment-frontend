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
import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object DefinedContributionPensionSchemePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "definedContributionPensionScheme"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(DefinedContributionPensionSchemePage) match {
      case Some(true)  => preAARoutes.FlexiblyAccessedPensionController.onPageLoad(NormalMode)
      case Some(false) =>
        preAARoutes.PayTaxCharge1516Controller.onPageLoad(NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(DefinedContributionPensionSchemePage) match {
      case Some(true)  => preAARoutes.FlexiblyAccessedPensionController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false => userAnswers.remove(FlexiblyAccessedPensionPage).flatMap(_.remove(FlexibleAccessStartDatePage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
