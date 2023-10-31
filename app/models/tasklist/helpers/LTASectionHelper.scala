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

package models.tasklist.helpers

import models.NewExcessLifetimeAllowancePaid.{Annualpayment, Both, Lumpsum}
import models.WhoPayingExtraLtaCharge.{PensionScheme, You}
import models.tasklist.SectionStatus
import models.{ChangeInTaxCharge, UserAnswers}
import pages.lifetimeallowance._

object LTASectionHelper {

  def anyLtaKickoutReached(answers: UserAnswers): Boolean = if (
    statusOfHadBCE(answers) &&
      statusOfInformedBCEChange(answers) &&
      statusOfChangeInTaxCharge(answers) &&
      !noPreviousChargeKickoutReached(answers)
  ) false
  else true

  def isLTAElligble(answers: UserAnswers): SectionStatus =
    if (
      statusOfHadBCE(answers) &&
        statusOfInformedBCEChange(answers) &&
        statusOfChangeInTaxCharge(answers) &&
        !noPreviousChargeKickoutReached(answers)
    ) SectionStatus.InProgress
    else SectionStatus.Completed

  def isLastPageAnswered(answers: UserAnswers): Boolean =
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(You) => true
      case Some(PensionScheme) =>
        answers.get(LtaPensionSchemeDetailsPage) match {
          case Some(_) => true
          case None => false
        }
      case None => noValueIncreaseAndPreviousChargeLastPage(answers)
    }

  def checkNewPaymentValuesExist(answers: UserAnswers): Boolean =
    answers.get(NewExcessLifetimeAllowancePaidPage) match {
      case Some(Both) | Some(Annualpayment) => answers.get(NewAnnualPaymentValuePage).isDefined
      case Some(Lumpsum) => answers.get(NewLumpSumValuePage).isDefined
      case _ => false
    }

  def combinedIsValueIncreased(
                                        newLumpSumValue: Option[BigInt],
                                        oldLumpSumValue: Option[BigInt],
                                        newAnnualPaymentValue: Option[BigInt],
                                        oldAnnualPaymentValue: Option[BigInt]
                                      ): Boolean =
    (newLumpSumValue.getOrElse(BigInt(0)) + newAnnualPaymentValue.getOrElse(BigInt(0))) >
      (oldLumpSumValue.getOrElse(BigInt(0)) + oldAnnualPaymentValue.getOrElse(BigInt(0)))

  def noValueIncreaseAndPreviousChargeLastPage(answers: UserAnswers): Boolean =
    !combinedIsValueIncreased(
      answers.get(NewLumpSumValuePage),
      answers.get(LumpSumValuePage),
      answers.get(NewAnnualPaymentValuePage),
      answers.get(AnnualPaymentValuePage)
    ) && answers.get(LifetimeAllowanceChargePage).getOrElse(false) && checkNewPaymentValuesExist(answers)

  def statusOfHadBCE(answers: UserAnswers): Boolean =
    answers.get(HadBenefitCrystallisationEventPage) match {
      case Some(false) => false
      case Some(true) => true
      case None => true
    }

  def statusOfInformedBCEChange(answers: UserAnswers): Boolean =
    answers.get(ChangeInLifetimeAllowancePage) match {
      case Some(false) => false
      case Some(true) => true
      case None => true
    }

  def statusOfChangeInTaxCharge(answers: UserAnswers): Boolean =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.DecreasedCharge) | Some(
      ChangeInTaxCharge.IncreasedCharge
      ) =>
        true
      case Some(ChangeInTaxCharge.None) => false
      case None => true
    }

  def noPreviousChargeKickoutReached(answers: UserAnswers): Boolean =
    !answers.get(LifetimeAllowanceChargePage).getOrElse(false) && checkNewPaymentValuesExist(answers) &&
      (answers.get(NewLumpSumValuePage).getOrElse(0) == 0 && answers.get(NewAnnualPaymentValuePage).getOrElse(0) == 0)

  def firstPageIsAnswered(answers: UserAnswers) =
    answers.get(HadBenefitCrystallisationEventPage).isDefined

  def nextStepsLTAStatus(answers: UserAnswers): SectionStatus =
    if(isLastPageAnswered(answers) || (isLTAElligble(answers) == SectionStatus.Completed)) SectionStatus.NotStarted else SectionStatus.CannotStartYet

}