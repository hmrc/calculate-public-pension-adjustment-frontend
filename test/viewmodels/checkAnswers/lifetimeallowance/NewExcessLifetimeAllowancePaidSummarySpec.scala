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
import pages.lifetimeallowance.NewExcessLifetimeAllowancePaidPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class NewExcessLifetimeAllowancePaidSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Annual payment is selected, return the summary row and change link when true" in {
      val userAnswers = UserAnswers("id")
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Annualpayment
        )
        .get
      NewExcessLifetimeAllowancePaidSummary.row(userAnswers, true) shouldBe Some(
        SummaryListRowViewModel(
          key = "newExcessLifetimeAllowancePaid.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("newExcessLifetimeAllowancePaid.annualPayment")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.NewExcessLifetimeAllowancePaidController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("newExcessLifetimeAllowancePaid.change.hidden")
          )
        )
      )
    }

    "when Annual payment is selected, return the summary row and not change link when false" in {
      val userAnswers = UserAnswers("id")
        .set(
          NewExcessLifetimeAllowancePaidPage,
          models.NewExcessLifetimeAllowancePaid.Annualpayment
        )
        .get
      NewExcessLifetimeAllowancePaidSummary.row(userAnswers, false) shouldBe Some(
        SummaryListRowViewModel(
          key = "newExcessLifetimeAllowancePaid.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("newExcessLifetimeAllowancePaid.annualPayment"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      NewExcessLifetimeAllowancePaidSummary.row(userAnswers, true) shouldBe None
    }
  }

}
