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

package viewmodels.checkAnswers.annualallowance.taxyear

import controllers.annualallowance.taxyear.routes
import models.{CheckMode, Period, SchemeIndex, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.taxyear.ThresholdIncomePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ThresholdIncomeSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val period      = Period._2018
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
        .set(
          ThresholdIncomePage(Period._2018, SchemeIndex(0)),
          true
        )
        .get
      ThresholdIncomeSummary.row(userAnswers, period, schemeIndex) shouldBe Some(
        SummaryListRowViewModel(
          key = s"thresholdIncome.checkYourAnswersLabel.$period",
          value = ValueViewModel("site.yes"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ThresholdIncomeController.onPageLoad(CheckMode, period, schemeIndex).url
            )
              .withVisuallyHiddenText("thresholdIncome.change.hidden")
          )
        )
      )
    }

    "when No is selected, return the summary row" in {
      val period      = Period._2018
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
        .set(
          ThresholdIncomePage(Period._2018, SchemeIndex(0)),
          false
        )
        .get
      ThresholdIncomeSummary.row(userAnswers, period, schemeIndex) shouldBe Some(
        SummaryListRowViewModel(
          key = s"thresholdIncome.checkYourAnswersLabel.$period",
          value = ValueViewModel("site.no"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ThresholdIncomeController.onPageLoad(CheckMode, period, schemeIndex).url
            )
              .withVisuallyHiddenText("thresholdIncome.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      val period      = Period._2018
      val schemeIndex = SchemeIndex(0)
      ThresholdIncomeSummary.row(userAnswers, period, schemeIndex) shouldBe None
    }
  }

}