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

import controllers.annualallowance.taxyear.{routes => aaRoutes}
import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.{Section, SectionStatus}
import models.{Period, SectionNavigation, UserAnswers, UserAnswersPeriod}
import play.api.mvc.Call
import services.PeriodService

case class AASection(period: Period) extends Section {

  val initialPage: Call                  = aaRoutes.WhatYouWillNeedAAController.onPageLoad(period)
  val checkYourAAPeriodAnswersPage: Call = aaRoutes.CheckYourAAPeriodAnswersController.onPageLoad(period)

  def status(answers: UserAnswers): SectionStatus =
    navigateTo(answers) match {
      case initialPage.url                  => NotStarted
      case checkYourAAPeriodAnswersPage.url => Completed
      case _                                => InProgress
    }

  private val sectionNavigation: SectionNavigation = SectionNavigation(s"aaSection$period")

  def navigateTo(answers: UserAnswers): String =
    answers.get(sectionNavigation).getOrElse(initialPage.url)

  def saveNavigation(answers: UserAnswers, urlFragment: String): UserAnswers =
    answers.set(sectionNavigation, urlFragment).get
}

object AASection {
  def removeAllAAPeriodAnswersAndNavigation(answers: UserAnswers): UserAnswers =
    removeAAPeriodAnswersAndNavigation(answers, PeriodService.allRemedyPeriods)

  def removeAAPeriodAnswersAndNavigation(answers: UserAnswers, periods: Seq[Period]): UserAnswers =
    periods.headOption match {
      case Some(period) =>
        removeAAPeriodAnswersAndNavigation(
          answers.remove(UserAnswersPeriod(period)).get.remove(SectionNavigation(s"aaSection$period")).get,
          periods.tail
        )
      case None         => answers
    }
}
