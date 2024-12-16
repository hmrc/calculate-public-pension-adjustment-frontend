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
import pages.lifetimeallowance.AnnualPaymentValuePage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class AnnualPaymentValueSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row with change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          AnnualPaymentValuePage,
          BigInt("999")
        )
        .get
      AnnualPaymentValueSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "annualPaymentValue.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("£999")),
          actions = Seq(
            ActionItemViewModel("site.change", routes.AnnualPaymentValueController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("annualPaymentValue.change.hidden")
          )
        )
      )
    }

    "when value is entered, return the summary row without change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          AnnualPaymentValuePage,
          BigInt("999")
        )
        .get
      AnnualPaymentValueSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"annualPaymentValue.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("£999"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      AnnualPaymentValueSummary.row(userAnswers, true) shouldBe None
    }
  }

}
