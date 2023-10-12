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
import models.NewExcessLifetimeAllowancePaid.{Both, Lumpsum}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.lifetimeallowance.{routes => ltaRoutes}

import scala.util.Try

case object NewLumpSumValuePage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "newLumpSumValue"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(NewExcessLifetimeAllowancePaidPage) match {
      case Some(Lumpsum) =>
        navigateValueIncrease(answers, NormalMode)
      case Some(Both)    => controllers.lifetimeallowance.routes.NewAnnualPaymentValueController.onPageLoad(NormalMode)
      case _             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(NewExcessLifetimeAllowancePaidPage) match {
      case Some(Lumpsum) => navigateValueIncrease(answers, CheckMode)
      case Some(Both)    => controllers.lifetimeallowance.routes.NewAnnualPaymentValueController.onPageLoad(CheckMode)
      case _             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateValueIncrease(answers: UserAnswers, mode: Mode): Call = {
    val newLumpSumValue         = answers.get(NewLumpSumValuePage)
    val oldLumpSumValue         = answers.get(LumpSumValuePage)
    val newAnnualPaymentValue   = answers.get(NewAnnualPaymentValuePage)
    val oldAnnualPaymentValue   = answers.get(AnnualPaymentValuePage)
    val whoPayingExtraLtaCharge = answers.get(WhoPayingExtraLtaChargePage)
    val hasPreviousCharge = answers.get(LifetimeAllowanceChargePage).getOrElse(false)

    if (
      combinedIsValueIncreased(
        newLumpSumValue,
        oldLumpSumValue,
        newAnnualPaymentValue,
        oldAnnualPaymentValue
      ) && whoPayingExtraLtaCharge.isEmpty
    ) {
      ltaRoutes.WhoPayingExtraLtaChargeController.onPageLoad(mode)
    } else if (hasPreviousCharge){
      ltaRoutes.CheckYourLTAAnswersController.onPageLoad()
    }
    else{
      ltaRoutes.WhatYouWillNeedLtaController.onPageLoad()
    }
  }

  private def combinedIsValueIncreased(
    newLumpSumValue: Option[BigInt],
    oldLumpSumValue: Option[BigInt],
    newAnnualPaymentValue: Option[BigInt],
    oldAnnualPaymentValue: Option[BigInt]
  ): Boolean =
    (newLumpSumValue.getOrElse(BigInt(0)) + newAnnualPaymentValue.getOrElse(BigInt(0))) >
      (oldLumpSumValue.getOrElse(BigInt(0)) + oldAnnualPaymentValue.getOrElse(BigInt(0)))

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] = {
    val oldLumpSumValue       = userAnswers.get(LumpSumValuePage)
    val newAnnualPaymentValue = userAnswers.get(NewAnnualPaymentValuePage)
    val oldAnnualPaymentValue = userAnswers.get(AnnualPaymentValuePage)

    if (combinedIsValueIncreased(value, oldLumpSumValue, newAnnualPaymentValue, oldAnnualPaymentValue)) {
      super.cleanup(value, userAnswers)
    } else {
      userAnswers.remove(WhoPayingExtraLtaChargePage).flatMap(_.remove(LtaPensionSchemeDetailsPage))
    }
  }

}
