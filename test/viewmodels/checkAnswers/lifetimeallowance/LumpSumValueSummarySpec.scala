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

import controllers.lifetimeallowance.routes
import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.lifetimeallowance.LumpSumValuePage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class LumpSumValueSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          LumpSumValuePage,
          BigInt("999")
        )
        .get
      LumpSumValueSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "lumpSumValue.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("£999")),
          actions = Seq(
            ActionItemViewModel("site.change", routes.LumpSumValueController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("lumpSumValue.change.hidden")
          )
        )
      )
    }

    "when value is entered, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          LumpSumValuePage,
          BigInt("999")
        )
        .get
      LumpSumValueSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"lumpSumValue.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("£999"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      LumpSumValueSummary.row(userAnswers, true) shouldBe None
    }
  }

}
