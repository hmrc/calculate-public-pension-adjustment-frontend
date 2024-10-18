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

import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.lifetimeallowance.WhoPaidLTAChargePage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class WhoPaidLTAChargeSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Pension Scheme is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.PensionScheme
        )
        .get
      WhoPaidLTAChargeSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "whoPaidLTACharge.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("whoPaidLTACharge.pensionScheme")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.WhoPaidLTAChargeController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("whoPaidLTACharge.change.hidden")
          )
        )
      )
    }

    "when Pension Scheme is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.PensionScheme
        )
        .get
      WhoPaidLTAChargeSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = "whoPaidLTACharge.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("whoPaidLTACharge.pensionScheme"))
        )
      )
    }

    "when You is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.You
        )
        .get
      WhoPaidLTAChargeSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "whoPaidLTACharge.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("whoPaidLTACharge.you")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.WhoPaidLTAChargeController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("whoPaidLTACharge.change.hidden")
          )
        )
      )
    }

    "when You is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          WhoPaidLTAChargePage,
          models.WhoPaidLTACharge.You
        )
        .get
      WhoPaidLTAChargeSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = "whoPaidLTACharge.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("whoPaidLTACharge.you"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      WhoPaidLTAChargeSummary.row(userAnswers, true) shouldBe None
    }
  }

}
