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
import models.{CheckMode, Period, SchemeIndex, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PensionSchemeInput2016postAmountsPage}
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

class PensionSchemeInput2016postAmountsSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val period      = Period._2016
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
        .set(
          PensionSchemeInput2016postAmountsPage(period, schemeIndex),
          models.PensionSchemeInput2016postAmounts(BigInt("100"))
        )
        .get
        .set(
          PensionSchemeDetailsPage(period, schemeIndex),
          models.PensionSchemeDetails("SomeScheme", "01234567TR")
        )
        .get
      PensionSchemeInput2016postAmountsSummary.row(userAnswers, period, schemeIndex) shouldBe Some(
        SummaryListRowViewModel(
          key = messages("pensionSchemeInputAmounts.2016-post.checkYourAnswersLabel", "SomeScheme"),
          value = ValueViewModel(HtmlContent("£100")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.PensionSchemeInput2016postAmountsController.onPageLoad(CheckMode, period, schemeIndex).url
            )
              .withVisuallyHiddenText(messages("pensionSchemeInputAmounts.2016-post.change.hidden", "SomeScheme"))
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val period      = Period._2016
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
      PensionSchemeInput2016postAmountsSummary.row(userAnswers, period, schemeIndex) shouldBe None
    }
  }

}
