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

package models.tasklist

import models.UserAnswers
import pages.Page
import pages.lifetimeallowance.{CheckYourLTAAnswersPage, DateOfBenefitCrystallisationEventPage, HadBenefitCrystallisationEventPage}

case object LTASection extends Section {
  override def pages(): Seq[Page] =
    Seq(HadBenefitCrystallisationEventPage, DateOfBenefitCrystallisationEventPage)

  override def status(answers: UserAnswers): SectionStatus =
    if (answers.get(HadBenefitCrystallisationEventPage).isDefined) {
      answers.get(HadBenefitCrystallisationEventPage) match {
        case Some(false)                                                                => SectionStatus.Completed
        case Some(true) if answers.get(DateOfBenefitCrystallisationEventPage).isDefined =>
          SectionStatus.Completed
        case _                                                                          => SectionStatus.InProgress
      }
    } else SectionStatus.NotStarted

  override def checkYourAnswersPage: Page = CheckYourLTAAnswersPage
}
