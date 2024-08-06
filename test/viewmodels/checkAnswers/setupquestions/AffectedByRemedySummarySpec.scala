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

package viewmodels.checkAnswers.setupquestions

import controllers.setupquestions.routes
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.setupquestions.AffectedByRemedyPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.checkAnswers.AffectedByRemedySummary
import viewmodels.checkAnswers.setupquestions.annualallowance.SavingsStatementSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class AffectedByRemedySummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          AffectedByRemedyPage,
          true
        )
        .get
      AffectedByRemedySummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "affectedByRemedy.checkYourAnswersLabel",
          value = ValueViewModel("site.yes"),
          actions = Seq(
            ActionItemViewModel("site.change", routes.AffectedByRemedyController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("affectedByRemedy.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      SavingsStatementSummary.row(userAnswers) shouldBe None
    }
  }

}
