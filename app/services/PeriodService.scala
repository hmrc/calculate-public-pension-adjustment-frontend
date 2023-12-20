/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import models.{Period, ReportingChange, UserAnswers}
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import pages.setupquestions.ReportingChangePage

import java.time.LocalDate

object PeriodService {

  def isFirstPeriod(answers: UserAnswers, thisPeriod: Period) =
    !allRemedyPeriods
      .filter(period => period != thisPeriod)
      .exists(period => answers.get(MemberMoreThanOnePensionPage(period)).isDefined)

  def isRequired(answers: UserAnswers, reportingChange: ReportingChange): Boolean =
    answers.get(ReportingChangePage) match {
      case Some(set) if set.contains(reportingChange) => true
      case _                                          => false
    }

  def relevantPeriods(answers: UserAnswers): Seq[Period] = {

    val exitDateOption = answers.get(StopPayingPublicPensionPage)

    exitDateOption match {
      case Some(exitDate) => remedyPeriodsFor(exitDate)
      case None           => allRemedyPeriods
    }
  }

  def notRelevantPeriods(answers: UserAnswers): Seq[Period] =
    allRemedyPeriods.diff(relevantPeriods(answers))

  private def remedyPeriodsFor(exitDate: LocalDate): Seq[Period] =
    allRemedyPeriods.filter(period => !period.start.isAfter(exitDate))

  def allRemedyPeriods =
    Seq(
      Period._2016,
      Period._2017,
      Period._2018,
      Period._2019,
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    )
}
