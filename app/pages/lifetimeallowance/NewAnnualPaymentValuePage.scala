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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.lifetimeallowance.{routes => ltaRoutes}

case object NewAnnualPaymentValuePage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "newAnnualPaymentValue"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(NewAnnualPaymentValuePage) match {
      case Some(_) => navigateValueIncrease(answers, NormalMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(NewAnnualPaymentValuePage) match {
      case Some(_) => navigateValueIncrease(answers, CheckMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateValueIncrease(answers: UserAnswers, mode: Mode): Call = {
    val newLumpSumValue = answers.get(NewLumpSumValuePage)
    val oldLumpSumValue = answers.get(LumpSumValuePage)
    val newAnnualPaymentValue = answers.get(NewAnnualPaymentValuePage)
    val oldAnnualPaymentValue = answers.get(AnnualPaymentValuePage)
    val WhoPayingExtraLtaCharge = answers.get(WhoPayingExtraLtaChargePage)

    if (combinedIsValueIncreased(newLumpSumValue, oldLumpSumValue, newAnnualPaymentValue, oldAnnualPaymentValue) && WhoPayingExtraLtaCharge == None) {
      ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(mode)
    } else if (isValueIncreased(newLumpSumValue, oldLumpSumValue) || isValueIncreased(newAnnualPaymentValue, oldAnnualPaymentValue) && WhoPayingExtraLtaCharge == None) {
      ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(mode)
    } else {
      ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
    }
  }

  private def isValueIncreased(newValue: Option[BigInt], oldValue: Option[BigInt]): Boolean =
    (newValue.getOrElse(BigInt(0)) > oldValue.getOrElse(BigInt(0)))

  private def combinedIsValueIncreased(newLumpSumValue: Option[BigInt], oldLumpSumValue: Option[BigInt], newAnnualPaymentValue: Option[BigInt], oldAnnualPaymentValue: Option[BigInt]): Boolean =
    (newLumpSumValue.getOrElse(BigInt(0)) + newAnnualPaymentValue.getOrElse(BigInt(0))) >
      (oldLumpSumValue.getOrElse(BigInt(0)) + oldAnnualPaymentValue.getOrElse(BigInt(0)))

}
