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

package viewmodels.checkAnswers.annualallowance.taxyear

import controllers.annualallowance.taxyear.routes
import models.{CheckMode, Period, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.taxyear.KnowAdjustedAmountPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

class KnowAdjustedAmountSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val period      = Period._2018
      val userAnswers = UserAnswers("id")
        .set(
          KnowAdjustedAmountPage(Period._2018),
          true
        )
        .get
      KnowAdjustedAmountSummary.row(userAnswers, period) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "knowAdjustedAmount.checkYourAnswersLabel",
          value = ValueViewModel("site.yes"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.KnowAdjustedAmountController.onPageLoad(CheckMode, period).url
            )
              .withVisuallyHiddenText("knowAdjustedAmount.change.hidden")
          )
        )
      )
    }

    "when No is selected, return the summary row" in {
      val period      = Period._2018
      val userAnswers = UserAnswers("id")
        .set(
          KnowAdjustedAmountPage(Period._2018),
          false
        )
        .get
      KnowAdjustedAmountSummary.row(userAnswers, period) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "knowAdjustedAmount.checkYourAnswersLabel",
          value = ValueViewModel("site.no"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.KnowAdjustedAmountController.onPageLoad(CheckMode, period).url
            )
              .withVisuallyHiddenText("knowAdjustedAmount.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      val period      = Period._2018
      KnowAdjustedAmountSummary.row(userAnswers, period) `shouldBe` None
    }
  }

}
