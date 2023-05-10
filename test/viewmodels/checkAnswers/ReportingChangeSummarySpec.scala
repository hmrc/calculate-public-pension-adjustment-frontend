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

package viewmodels.checkAnswers

import controllers.routes
import models._
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance, OtherCompensation}
import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.ReportingChangePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class ReportingChangeSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when all checkboxes are selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set[Set[ReportingChange]](
          ReportingChangePage,
          Set(
            AnnualAllowance,
            LifetimeAllowance,
            OtherCompensation
          )
        )
        .get
      ReportingChangeSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "reportingChange.checkYourAnswersLabel",
          value = ValueViewModel(
            HtmlContent(
              "reportingChange.annualAllowance,<br>reportingChange.lifetimeAllowance,<br>reportingChange.otherCompensation"
            )
          ),
          actions = Seq(
            ActionItemViewModel("site.change", routes.ReportingChangeController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("reportingChange.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      ReportingChangeSummary.row(userAnswers) shouldBe None
    }
  }
}
