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
import models.{CheckMode, Period, ThresholdIncome, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.taxyear.ThresholdIncomePage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ThresholdIncomeSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val period      = Period._2018
      val userAnswers = UserAnswers("id")
        .set(
          ThresholdIncomePage(Period._2018),
          ThresholdIncome.Yes
        )
        .get
      ThresholdIncomeSummary.row(userAnswers, period) shouldBe Some(
        SummaryListRowViewModel(
          key = s"thresholdIncome.checkYourAnswersLabel.$period",
          value = ValueViewModel(HtmlContent("thresholdIncome.yes")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ThresholdIncomeController.onPageLoad(CheckMode, period).url
            )
              .withVisuallyHiddenText("thresholdIncome.change.hidden.2018")
          )
        )
      )
    }

    "when No is selected, return the summary row" in {
      val period      = Period._2018
      val userAnswers = UserAnswers("id")
        .set(
          ThresholdIncomePage(Period._2018),
          ThresholdIncome.No
        )
        .get
      ThresholdIncomeSummary.row(userAnswers, period) shouldBe Some(
        SummaryListRowViewModel(
          key = s"thresholdIncome.checkYourAnswersLabel.$period",
          value = ValueViewModel(HtmlContent("thresholdIncome.no")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ThresholdIncomeController.onPageLoad(CheckMode, period).url
            )
              .withVisuallyHiddenText("thresholdIncome.change.hidden.2018")
          )
        )
      )
    }

    "when IDoNotKnow is selected, return the summary row" in {
      val period = Period._2018
      val userAnswers = UserAnswers("id")
        .set(
          ThresholdIncomePage(Period._2018),
          ThresholdIncome.IDoNotKnow
        )
        .get
      ThresholdIncomeSummary.row(userAnswers, period) shouldBe Some(
        SummaryListRowViewModel(
          key = s"thresholdIncome.checkYourAnswersLabel.$period",
          value = ValueViewModel(HtmlContent("thresholdIncome.idk")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ThresholdIncomeController.onPageLoad(CheckMode, period).url
            )
              .withVisuallyHiddenText("thresholdIncome.change.hidden.2018")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      val period      = Period._2018
      ThresholdIncomeSummary.row(userAnswers, period) shouldBe None
    }
  }

}
