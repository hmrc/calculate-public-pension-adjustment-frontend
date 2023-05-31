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

package services

import models.{Period, ReportingChange, UserAnswers}
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.setupquestions.ReportingChangePage

import java.time.LocalDate
import javax.inject.Inject

class AnnualAllowanceLogicService @Inject() (
) {

  def isRequired(answers: UserAnswers, reportingChange: ReportingChange): Boolean =
    answers.get(ReportingChangePage) match {
      case Some(set) if set.contains(reportingChange) => true
      case _                                          => false
    }

  private def remedyPeriodsFor(exitDate: LocalDate): Seq[Period] =
    allRemedyPeriods.filter(period => !period.start.isAfter(exitDate))

  def relevantPeriods(answers: UserAnswers): Seq[Period] = {

    val exitDateOption = answers.get(StopPayingPublicPensionPage)

    exitDateOption match {
      case Some(exitDate) => remedyPeriodsFor(exitDate)
      case None           => allRemedyPeriods
    }
  }

  private def allRemedyPeriods =
    Seq(
      Period._2016PreAlignment,
      Period._2016PostAlignment,
      Period._2017,
      Period._2018,
      Period._2019,
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    )
}
