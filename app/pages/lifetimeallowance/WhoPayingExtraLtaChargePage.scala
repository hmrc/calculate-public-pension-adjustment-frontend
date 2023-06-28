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

package pages.lifetimeallowance

import controllers.lifetimeallowance.{routes => ltaRoutes}
import controllers.{routes => generalRoutes}
import models.WhoPayingExtraLtaCharge.{PensionScheme, You}
import models.{NormalMode, UserAnswers, WhoPayingExtraLtaCharge}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object WhoPayingExtraLtaChargePage extends QuestionPage[WhoPayingExtraLtaCharge] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "whoPayingExtraLtaCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(PensionScheme) => ltaRoutes.LtaPensionSchemeDetailsController.onPageLoad(NormalMode)
      case Some(You)           => ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
      case None                => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(PensionScheme) => ltaRoutes.LtaPensionSchemeDetailsController.onPageLoad(NormalMode)
      case Some(You)           => ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
      case None                => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[WhoPayingExtraLtaCharge], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case PensionScheme => super.cleanup(value, userAnswers)
        case You           =>
          userAnswers
            .remove(LtaPensionSchemeDetailsPage)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
