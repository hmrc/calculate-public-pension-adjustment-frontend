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
import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

import java.time.LocalDate

class StopPayingPublicPensionSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()
  val validAnswer: LocalDate              = LocalDate.of(2015, 4, 6)
  val languageTag                         = if (messages.lang.code == "cy") "cy" else "en"

  "row" - {
    "when value is entered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          StopPayingPublicPensionPage,
          validAnswer
        )
        .get
      StopPayingPublicPensionSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "stopPayingPublicPension.checkYourAnswersLabel",
          value = ValueViewModel(StopPayingPublicPensionSummary.dateToString(validAnswer, languageTag)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.StopPayingPublicPensionController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("stopPayingPublicPension.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      StopPayingPublicPensionSummary.row(userAnswers) shouldBe None
    }
  }

}
