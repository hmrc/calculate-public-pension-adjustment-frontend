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
import models.{CheckMode, NormalMode, UserAnswers, WhoPayingExtraLtaCharge}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object WhoPayingExtraLtaChargePage extends QuestionPage[WhoPayingExtraLtaCharge] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "whoPayingExtraLtaCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val hasPreviousCharge = answers.get(LifetimeAllowanceChargePage).getOrElse(false)
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(PensionScheme)             => ltaRoutes.LtaPensionSchemeDetailsController.onPageLoad(NormalMode)
      case Some(You) if hasPreviousCharge  => ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
      case Some(You) if !hasPreviousCharge => ltaRoutes.UserSchemeDetailsController.onPageLoad(NormalMode)
      case None                            => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    val hasPreviousCharge = answers.get(LifetimeAllowanceChargePage).getOrElse(false)
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(PensionScheme)             => ltaRoutes.LtaPensionSchemeDetailsController.onPageLoad(CheckMode)
      case Some(You) if hasPreviousCharge  => ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
      case Some(You) if !hasPreviousCharge => ltaRoutes.UserSchemeDetailsController.onPageLoad(CheckMode)
      case None                            => generalRoutes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override def cleanup(value: Option[WhoPayingExtraLtaCharge], userAnswers: UserAnswers): Try[UserAnswers] = {
    val hasPreviousCharge = userAnswers.get(LifetimeAllowanceChargePage).getOrElse(false)
    val whoPaidChargeIsUser = userAnswers.get(WhoPayingExtraLtaChargePage).contains(WhoPayingExtraLtaCharge.You)
    value
      .map {
        case PensionScheme if !hasPreviousCharge || whoPaidChargeIsUser =>
          userAnswers
            .remove(UserSchemeDetailsPage)
        case PensionScheme if hasPreviousCharge  => super.cleanup(value, userAnswers)
        case You                                 =>
          userAnswers
            .remove(LtaPensionSchemeDetailsPage).flatMap(_.remove(UserSchemeDetailsPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }
}
