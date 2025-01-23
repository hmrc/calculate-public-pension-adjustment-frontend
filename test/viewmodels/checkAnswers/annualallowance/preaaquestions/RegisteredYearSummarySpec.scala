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

package viewmodels.checkAnswers.annualallowance.preaaquestions

import controllers.annualallowance.preaaquestions.routes.RegisteredYearController
import models.{CheckMode, Period, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.preaaquestions.RegisteredYearPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

class RegisteredYearSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          RegisteredYearPage(Period._2011),
          true
        )
        .get

      RegisteredYearSummary.row(userAnswers, Period._2011) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "registeredYear.checkYourAnswersLabel.2011",
          value = ValueViewModel("site.yes"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              RegisteredYearController.onPageLoad(CheckMode, Period._2011).url
            )
              .withVisuallyHiddenText("registeredYear.change.hidden")
          )
        )
      )
    }

    "when No is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          RegisteredYearPage(Period._2011),
          false
        )
        .get

      RegisteredYearSummary.row(userAnswers, Period._2011) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "registeredYear.checkYourAnswersLabel.2011",
          value = ValueViewModel("site.no"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              RegisteredYearController.onPageLoad(CheckMode, Period._2011).url
            )
              .withVisuallyHiddenText("registeredYear.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      val period      = Period._2014
      RegisteredYearSummary.row(userAnswers, period) `shouldBe` None
    }
  }
}
