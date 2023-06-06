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

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.{Period, ReportingChange}
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.setupquestions.ReportingChangePage

import java.time.LocalDate

class AnnualAllowanceLogicServiceTest extends SpecBase {

  val aaLogicService = new AnnualAllowanceLogicService

  "Annual allowance reporting" - {

    " is required when specified in UserAnswers" in {

      val reportingChangeWithAA: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                     = emptyUserAnswers.set(ReportingChangePage, reportingChangeWithAA)

      aaLogicService.isRequired(answers.get, ReportingChange.AnnualAllowance) must be(true)
    }

    "is not required when not specified in UserAnswers" in {

      val reportingChangeWithoutAA: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers                                        = emptyUserAnswers.set(ReportingChangePage, reportingChangeWithoutAA)

      aaLogicService.isRequired(answers.get, ReportingChange.AnnualAllowance) must be(false)
    }
  }

  "Relevant remedy periods" - {

    "are included up to and including period in which user stopped paying in" in {
      val answers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2018, 1, 1))

      val expectedPeriods = Seq(
        Period._2016PreAlignment,
        Period._2016PostAlignment,
        Period._2017,
        Period._2018
      )

      aaLogicService.relevantPeriods(answers.get) must be(expectedPeriods)
    }

    "are included for all remedy periods if the user did not stop paying in" in {
      val answers = emptyUserAnswers

      val expectedPeriods = Seq(
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

      aaLogicService.relevantPeriods(answers) must be(expectedPeriods)
    }

    "are included for pre alignment period" in {
      val answers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 8))

      val expectedPeriods = Seq(
        Period._2016PreAlignment
      )

      aaLogicService.relevantPeriods(answers.get) must be(expectedPeriods)
    }

    "are included for post alignment period" in {
      val answers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 9))

      val expectedPeriods = Seq(
        Period._2016PreAlignment,
        Period._2016PostAlignment
      )

      aaLogicService.relevantPeriods(answers.get) must be(expectedPeriods)
    }
  }
}
