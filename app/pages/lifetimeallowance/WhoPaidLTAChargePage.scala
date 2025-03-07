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

package pages.lifetimeallowance

import models.WhoPaidLTACharge.{PensionScheme, You}
import models.{CheckMode, NormalMode, UserAnswers, WhoPaidLTACharge}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object WhoPaidLTAChargePage extends QuestionPage[WhoPaidLTACharge] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "whoPaidLTACharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = answers.get(WhoPaidLTAChargePage) match {
    case Some(WhoPaidLTACharge.PensionScheme) =>
      controllers.lifetimeallowance.routes.SchemeNameAndTaxRefController.onPageLoad(NormalMode)
    case Some(WhoPaidLTACharge.You)           =>
      controllers.lifetimeallowance.routes.UserSchemeDetailsController.onPageLoad(NormalMode)
    case _                                    => controllers.routes.JourneyRecoveryController.onPageLoad()
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = answers.get(WhoPaidLTAChargePage) match {
    case Some(WhoPaidLTACharge.PensionScheme) =>
      controllers.lifetimeallowance.routes.SchemeNameAndTaxRefController.onPageLoad(CheckMode)
    case Some(WhoPaidLTACharge.You)           =>
      controllers.lifetimeallowance.routes.UserSchemeDetailsController.onPageLoad(CheckMode)
    case _                                    => controllers.routes.JourneyRecoveryController.onPageLoad()
  }

  override def cleanup(value: Option[WhoPaidLTACharge], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case PensionScheme => userAnswers.remove(UserSchemeDetailsPage)
        case You           =>
          userAnswers
            .remove(SchemeNameAndTaxRefPage)
            .flatMap(_.remove(QuarterChargePaidPage))
            .flatMap(_.remove(YearChargePaidPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
