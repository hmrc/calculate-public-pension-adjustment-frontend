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

package models

import base.SpecBase
import pages.annualallowance.taxyear.{AddAnotherSchemePage, WhatYouWillNeedPage}
import pages.setupquestions.ReasonForResubmissionPage
import pages.setupquestions.annualallowance.SavingsStatementPage

class UserAnswersSpec extends SpecBase {

  "should correctly identify when answers are contained for a question page with a static path" in {
    val userAnswers: UserAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

    userAnswers.containsAnswerFor(SavingsStatementPage)      must be(true)
    userAnswers.containsAnswerFor(ReasonForResubmissionPage) must be(false)
  }

  "should correctly identify when answers are contained for a question page with a parameterised path" in {
    val page1 = AddAnotherSchemePage(Period._2016: Period, SchemeIndex(0))
    val page2 = AddAnotherSchemePage(Period._2016: Period, SchemeIndex(1))

    val userAnswers: UserAnswers = emptyUserAnswers.set(page1, true).get

    userAnswers.containsAnswerFor(page1) must be(true)
    userAnswers.containsAnswerFor(page2) must be(false)
  }

  "should correctly identify if answers are contained for a non question page" in {
    val userAnswers: UserAnswers = emptyUserAnswers

    userAnswers.containsAnswerFor(WhatYouWillNeedPage(Period._2016)) must be(false)
  }
}
