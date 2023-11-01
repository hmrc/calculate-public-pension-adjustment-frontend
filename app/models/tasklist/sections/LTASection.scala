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

import models.UserAnswers
import models.tasklist.helpers.LTASectionHelper
import models.tasklist.{Section, SectionStatus}
import pages.Page
import pages.lifetimeallowance._
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
    if (LTASectionHelper.firstPageIsAnswered(answers)) {
      if (LTASectionHelper.isLastPageAnswered(answers)) {
        SectionStatus.Completed
      } else LTASectionHelper.isLTAEligible(answers)
    } else SectionStatus.NotStarted

  def navigateTo(answers: UserAnswers): Page =
    if (status(answers) == SectionStatus.Completed) {
      CheckYourLTAAnswersPage
    } else {
      pages().findLast(page => answers.containsAnswerFor(page)).getOrElse(pages().head)
    }
}
