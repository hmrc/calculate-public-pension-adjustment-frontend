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
import models.WhichYearsScottishTaxpayer.{ToEighteen, ToNineteen, ToSeventeen, ToTwenty, ToTwentyOne, ToTwentyThree, ToTwentyTwo}
import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.WhichYearsScottishTaxpayerPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class WhichYearsScottishTaxpayerSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when all checkboxes are selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set[Set[WhichYearsScottishTaxpayer]](
          WhichYearsScottishTaxpayerPage,
          Set(ToTwentyThree, ToTwentyTwo, ToTwentyOne, ToTwenty, ToNineteen, ToEighteen, ToSeventeen)
        )
        .get
      WhichYearsScottishTaxpayerSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "whichYearsScottishTaxpayer.checkYourAnswersLabel",
          value = ValueViewModel(
            HtmlContent(
              "whichYearsScottishTaxpayer.toTwentyOne,<br>" +
                "whichYearsScottishTaxpayer.toSeventeen,<br>" +
                "whichYearsScottishTaxpayer.toTwentyTwo,<br>" +
                "whichYearsScottishTaxpayer.toTwenty,<br>" +
                "whichYearsScottishTaxpayer.toNineteen,<br>" +
                "whichYearsScottishTaxpayer.toEighteen,<br>" +
                "whichYearsScottishTaxpayer.toTwentyThree"
            )
          ),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhichYearsScottishTaxpayerController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("whichYearsScottishTaxpayer.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      WhichYearsScottishTaxpayerSummary.row(userAnswers) shouldBe None
    }
  }
}
