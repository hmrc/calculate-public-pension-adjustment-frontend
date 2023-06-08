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
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try
case object ValueNewLtaChargePage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "valueNewLtaCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(ValueNewLtaChargePage), answers.get(LifetimeAllowanceChargeAmountPage)) match {
      case (Some(newLtaCharge), Some(oldLtaCharge)) if newLtaCharge < oldLtaCharge =>
        ltaRoutes.CheckYourLTAAnswersController.onPageLoad
      case (Some(newLtaCharge), Some(oldLtaCharge)) if newLtaCharge > oldLtaCharge =>
        ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(NormalMode)
      case _ =>
        ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(NormalMode)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    (answers.get(ValueNewLtaChargePage), answers.get(LifetimeAllowanceChargeAmountPage), answers.get(WhoPayingExtraLtaChargePage)) match {
      case (Some(newLtaCharge), Some(oldLtaCharge), None) if newLtaCharge > oldLtaCharge =>
        ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(NormalMode)
      case (Some(_), None, None) =>
        ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(NormalMode)
      case _ =>
        ltaRoutes.CheckYourLTAAnswersController.onPageLoad
    }

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] =
    (value, userAnswers.get(LifetimeAllowanceChargeAmountPage)) match {
      case (Some(newLtaCharge), Some(oldLtaCharge)) if newLtaCharge < oldLtaCharge =>
        userAnswers.remove(WhoPayingExtraLtaChargePage).flatMap(_.remove(LtaPensionSchemeDetailsPage))
      case (Some(newLtaCharge), Some(oldLtaCharge)) if newLtaCharge > oldLtaCharge =>
        super.cleanup(value, userAnswers)
      case (Some(_), None) =>
        super.cleanup(value, userAnswers)
      case _ => super.cleanup(value, userAnswers)
    }
}
