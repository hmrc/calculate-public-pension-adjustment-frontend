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

package viewmodels.checkAnswers.annualallowance.preaaquestions

import controllers.annualallowance.preaaquestions.routes
import models.{CheckMode, Period, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.preaaquestions.PIAPreRemedyPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class PIAPreRemedySummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when PIA entered, return summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          PIAPreRemedyPage(Period._2011),
          BigInt(123)
        )
        .get

      PIAPreRemedySummary.rows(userAnswers) shouldBe List(
        Some(
          SummaryListRowViewModel(
            key = "pIAPreRemedy.checkYourAnswersLabel.2011",
            value = ValueViewModel(HtmlContent("£123")),
            actions = Seq(
              ActionItemViewModel("site.change", routes.PIAPreRemedyController.onPageLoad(CheckMode, Period._2011).url)
                .withVisuallyHiddenText("pIAPreRemedy.change.hidden")
            )
          )
        ),
        None,
        None,
        None,
        None
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      PIAPreRemedySummary.rows(userAnswers) shouldBe List(None, None, None, None, None)
    }
  }
}
