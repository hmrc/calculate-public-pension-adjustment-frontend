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

import base.SpecBase
import models.Period
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage

import java.time.LocalDate

class PeriodServiceSpec extends SpecBase {

  "First period" - {

    "is identified correctly when a user has not completed any previous year" in {
      val answers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2018), false).get

      PeriodService.isFirstPeriod(answers, Period._2018) must be(true)
    }

    "is identified correctly when a user has completed a previous year" in {
      val answers = emptyUserAnswers.set(MemberMoreThanOnePensionPage(Period._2019), false).get

      PeriodService.isFirstPeriod(answers, Period._2018) must be(false)
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

      PeriodService.relevantPeriods(answers.get) must be(expectedPeriods)
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

      PeriodService.relevantPeriods(answers) must be(expectedPeriods)
    }

    "are included for pre alignment period" in {
      val answers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 8))

      val expectedPeriods = Seq(
        Period._2016PreAlignment
      )

      PeriodService.relevantPeriods(answers.get) must be(expectedPeriods)
    }

    "are included for post alignment period" in {
      val answers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 9))

      val expectedPeriods = Seq(
        Period._2016PreAlignment,
        Period._2016PostAlignment
      )

      PeriodService.relevantPeriods(answers.get) must be(expectedPeriods)
    }
  }
}
