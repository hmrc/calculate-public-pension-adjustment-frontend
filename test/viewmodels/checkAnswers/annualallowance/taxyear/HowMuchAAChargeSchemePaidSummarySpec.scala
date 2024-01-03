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
import pages.annualallowance.taxyear.HowMuchAAChargeSchemePaidPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class HowMuchAAChargeSchemePaidSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val period      = Period._2018
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
        .set(
          HowMuchAAChargeSchemePaidPage(period, schemeIndex),
          BigInt("100")
        )
        .get
      HowMuchAAChargeSchemePaidSummary.row(userAnswers, period, schemeIndex) shouldBe Some(
        SummaryListRowViewModel(
          key = "howMuchAAChargeSchemePaid.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("&pound;100")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.HowMuchAAChargeSchemePaidController.onPageLoad(CheckMode, period, schemeIndex).url
            )
              .withVisuallyHiddenText("howMuchAAChargeSchemePaid.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val period      = Period._2018
      val schemeIndex = SchemeIndex(0)
      val userAnswers = UserAnswers("id")
      HowMuchAAChargeSchemePaidSummary.row(userAnswers, period, schemeIndex) shouldBe None
    }
  }

}
