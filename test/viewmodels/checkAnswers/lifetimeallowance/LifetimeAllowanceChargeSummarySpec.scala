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
import pages.lifetimeallowance.LifetimeAllowanceChargePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

class LifetimeAllowanceChargeSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Yes is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          LifetimeAllowanceChargePage,
          true
        )
        .get
      LifetimeAllowanceChargeSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "lifetimeAllowanceCharge.checkYourAnswersLabel",
          value = ValueViewModel("site.yes"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.LifetimeAllowanceChargeController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("lifetimeAllowanceCharge.change.hidden")
          )
        )
      )
    }

    "when Yes is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          LifetimeAllowanceChargePage,
          true
        )
        .get
      LifetimeAllowanceChargeSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"lifetimeAllowanceCharge.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel("site.yes")
        )
      )
    }

    "when No is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          LifetimeAllowanceChargePage,
          false
        )
        .get
      LifetimeAllowanceChargeSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "lifetimeAllowanceCharge.checkYourAnswersLabel",
          value = ValueViewModel("site.no"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.LifetimeAllowanceChargeController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("lifetimeAllowanceCharge.change.hidden")
          )
        )
      )
    }

    "when No is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          LifetimeAllowanceChargePage,
          false
        )
        .get
      LifetimeAllowanceChargeSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"lifetimeAllowanceCharge.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel("site.no")
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      LifetimeAllowanceChargeSummary.row(userAnswers, true) shouldBe None
    }
  }

}
