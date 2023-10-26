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

package models.tasklist.sections

import models.WhoPayingExtraLtaCharge.{PensionScheme, You}
import models.{ChangeInTaxCharge, ReportingChange, UserAnswers}
import models.tasklist.{Section, SectionStatus}
import pages.Page
import pages.lifetimeallowance._
import pages.setupquestions.ReportingChangePage
import play.api.libs.json.JsPath

import scala.util.Try

case object LTASection extends Section {

  def deleteAllAnswers(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.removePath(JsPath \ "lta")

  override def pages(): Seq[Page] =
    Seq(
      WhatYouWillNeedLtaPage,
      HadBenefitCrystallisationEventPage,
      DateOfBenefitCrystallisationEventPage,
      ChangeInLifetimeAllowancePage,
      ChangeInTaxChargePage,
      MultipleBenefitCrystallisationEventPage,
      LtaProtectionOrEnhancementsPage,
      ProtectionTypePage,
      ProtectionReferencePage,
      EnhancementTypePage,
      InternationalEnhancementReferencePage,
      PensionCreditReferencePage,
      ProtectionEnhancedChangedPage,
      WhatNewProtectionTypeEnhancementPage,
      ReferenceNewProtectionTypeEnhancementPage,
      NewEnhancementTypePage,
      NewInternationalEnhancementReferencePage,
      NewPensionCreditReferencePage,
      LifetimeAllowanceChargePage,
      ExcessLifetimeAllowancePaidPage,
      LumpSumValuePage,
      AnnualPaymentValuePage,
      WhoPaidLTAChargePage,
      UserSchemeDetailsPage,
      SchemeNameAndTaxRefPage,
      QuarterChargePaidPage,
      YearChargePaidPage,
      NewExcessLifetimeAllowancePaidPage,
      NewLumpSumValuePage,
      NewAnnualPaymentValuePage,
      WhoPayingExtraLtaChargePage,
      LtaPensionSchemeDetailsPage
    )

  def status(answers: UserAnswers): SectionStatus =
    if (firstPageIsAnswered(answers)) {
      if (isLastPageAnswered(answers)) {
        SectionStatus.Completed
      } else isLTAElligble(answers)
    } else SectionStatus.NotStarted

  private def isLTAElligble(answers: UserAnswers): SectionStatus = {
    if (
      statusOfHadBCE(answers) &&
        statusOfInformedBCEChange(answers) &&
        statusOfChangeInTaxCharge(answers) &&
        !noPreviousChargeKickoutReached(answers)
    ) SectionStatus.InProgress
    else SectionStatus.Completed
  }

  private def isLastPageAnswered(answers: UserAnswers): Boolean =
    answers.get(WhoPayingExtraLtaChargePage) match {
      case Some(You)           => true
      case Some(PensionScheme) =>
        answers.get(LtaPensionSchemeDetailsPage) match {
          case Some(_) => true
          case None    => false
        }
      case None                => false
    }

  private def statusOfHadBCE(answers: UserAnswers): Boolean =
    answers.get(HadBenefitCrystallisationEventPage) match {
      case Some(false) => false
      case Some(true)  => true
      case None        => true
    }

  private def statusOfInformedBCEChange(answers: UserAnswers): Boolean =
    answers.get(ChangeInLifetimeAllowancePage) match {
      case Some(false) => false
      case Some(true)  => true
      case None        => true
    }

  private def statusOfChangeInTaxCharge(answers: UserAnswers): Boolean =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.DecreasedCharge) | Some(
            ChangeInTaxCharge.IncreasedCharge
          ) =>
        true
      case Some(ChangeInTaxCharge.None) => false
      case None                         => true
    }

  private def noPreviousChargeKickoutReached(answers: UserAnswers): Boolean =
    !answers.get(LifetimeAllowanceChargePage).getOrElse(false) &&
      (answers.get(NewLumpSumValuePage).contains(0) || answers.get(NewAnnualPaymentValuePage).contains(0))

  private def firstPageIsAnswered(answers: UserAnswers) =
    answers.get(HadBenefitCrystallisationEventPage).isDefined

  def navigateTo(answers: UserAnswers): Page =
    if (status(answers) == SectionStatus.Completed) {
      CheckYourLTAAnswersPage
    } else {
      pages().findLast(page => answers.containsAnswerFor(page)).getOrElse(pages().head)
    }
}