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

import base.SpecBase
import models.{Period, SchemeIndex}

class AASectionTest extends SpecBase {

  "Status is first period" - {

    "when user has defined benefit then status is complete when defined benefit amount page is answered" in {
      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

      val status = AASection(Period._2013, SchemeIndex(0)).status(userAnswers)

      status mustBe(SectionStatus.Completed)
    }

    "when user has defined benefit then status is incomplete when defined benefit amount page is not answered" in {
      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

      val status = AASection(Period._2013, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.InProgress)
    }

  }

  "Status is not first period" - {

    "when user has defined benefit then status is complete when defined benefit amount page is answered" in {
      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

      val status = AASection(Period._2013, SchemeIndex(0)).status(userAnswers)

      status mustBe (SectionStatus.NotStarted)
    }

  }
}