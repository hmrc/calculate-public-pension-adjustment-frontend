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

package viewmodels.checkAnswers.lifetimeallowance

import models.ProtectionEnhancedChanged.Enhancement
import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.lifetimeallowance.ProtectionEnhancedChangedPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

class ProtectionEnhancedChangedSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when a radio button is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          ProtectionEnhancedChangedPage,
          Enhancement
        )
        .get
      ProtectionEnhancedChangedSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "protectionEnhancedChanged.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("protectionEnhancedChanged.enhancement")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.ProtectionEnhancedChangedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("protectionEnhancedChanged.change.hidden")
          )
        )
      )
    }

    "when a radio button is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          ProtectionEnhancedChangedPage,
          Enhancement
        )
        .get
      ProtectionEnhancedChangedSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"protectionEnhancedChanged.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("protectionEnhancedChanged.enhancement"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      ProtectionEnhancedChangedSummary.row(userAnswers, true) shouldBe None
    }
  }
}
