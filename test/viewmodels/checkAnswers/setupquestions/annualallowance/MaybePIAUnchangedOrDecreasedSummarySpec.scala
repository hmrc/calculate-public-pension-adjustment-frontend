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

package viewmodels.checkAnswers.setupquestions.annualallowance

import controllers.setupquestions.annualallowance.routes
import models.{CheckMode, MaybePIAUnchangedOrDecreased, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.setupquestions.annualallowance.MaybePIAUnchangedOrDecreasedPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.all.{ActionItemViewModel, FluentActionItem, SummaryListRowViewModel, ValueViewModel}
import viewmodels.implicits.*

class MaybePIAUnchangedOrDecreasedSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          MaybePIAUnchangedOrDecreasedPage,
          MaybePIAUnchangedOrDecreased.Yes
        )
        .get
      MaybePIAUnchangedOrDecreasedSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "maybePIAUnchangedOrDecreased.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("maybePIAUnchangedOrDecreased.yes")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.MaybePIAUnchangedOrDecreasedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("maybePIAUnchangedOrDecreased.change.hidden")
          )
        )
      )
    }

    "when No is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          MaybePIAUnchangedOrDecreasedPage,
          MaybePIAUnchangedOrDecreased.No
        )
        .get
      MaybePIAUnchangedOrDecreasedSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "maybePIAUnchangedOrDecreased.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("maybePIAUnchangedOrDecreased.no")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.MaybePIAUnchangedOrDecreasedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("maybePIAUnchangedOrDecreased.change.hidden")
          )
        )
      )
    }

    "when IDoNotKnow is selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          MaybePIAUnchangedOrDecreasedPage,
          MaybePIAUnchangedOrDecreased.IDoNotKnow
        )
        .get
      MaybePIAUnchangedOrDecreasedSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "maybePIAUnchangedOrDecreased.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("maybePIAUnchangedOrDecreased.idk")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.MaybePIAUnchangedOrDecreasedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("maybePIAUnchangedOrDecreased.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      MaybePIAUnchangedOrDecreasedSummary.row(userAnswers) `shouldBe` None
    }
  }
}
