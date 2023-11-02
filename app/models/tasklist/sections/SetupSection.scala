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

import controllers.setupquestions.{routes => setupRoutes}
import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.{Section, SectionStatus}
import models.{NormalMode, SectionNavigation, UserAnswers}
import play.api.mvc.Call

case object SetupSection extends Section {

  val initialPage: Call               = setupRoutes.SavingsStatementController.onPageLoad(NormalMode)
  val checkYourSetupAnswersPage: Call = setupRoutes.CheckYourSetupAnswersController.onPageLoad()
  val ineligiblePage: Call            = setupRoutes.IneligibleController.onPageLoad

  def status(answers: UserAnswers): SectionStatus =
    navigateTo(answers) match {
      case initialPage.url               => NotStarted
      case checkYourSetupAnswersPage.url => Completed
      case _                             => InProgress
    }

  private val sectionNavigation: SectionNavigation = SectionNavigation("setupSection")

  def navigateTo(answers: UserAnswers): String = {
    val taskListNavLink = answers.get(sectionNavigation).getOrElse(initialPage.url)
    if (taskListNavLink == ineligiblePage.url) { checkYourSetupAnswersPage.url }
    else { taskListNavLink }
  }

  def saveNavigation(answers: UserAnswers, urlFragment: String): UserAnswers =
    answers.set(sectionNavigation, urlFragment).get
}
