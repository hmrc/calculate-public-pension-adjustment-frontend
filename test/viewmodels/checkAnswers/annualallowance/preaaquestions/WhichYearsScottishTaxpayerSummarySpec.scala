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

package viewmodels.checkAnswers.annualallowance.preaaquestions

import models.WhichYearsScottishTaxpayer.{_2017, _2018, _2019, _2020, _2021, _2022, _2023}
import models.{CheckMode, UserAnswers, _}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.annualallowance.preaaquestions.WhichYearsScottishTaxpayerPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import controllers.annualallowance.preaaquestions.routes

class WhichYearsScottishTaxpayerSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when all checkboxes are selected, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set[Set[WhichYearsScottishTaxpayer]](
          WhichYearsScottishTaxpayerPage,
          Set(_2023, _2022, _2021, _2020, _2019, _2018, _2017)
        )
        .get
      WhichYearsScottishTaxpayerSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "whichYearsScottishTaxpayer.checkYourAnswersLabel",
          value = ValueViewModel(
            HtmlContent(
              "whichYearsScottishTaxpayer.2021,<br>" +
                "whichYearsScottishTaxpayer.2018,<br>" +
                "whichYearsScottishTaxpayer.2020,<br>" +
                "whichYearsScottishTaxpayer.2019,<br>" +
                "whichYearsScottishTaxpayer.2017,<br>" +
                "whichYearsScottishTaxpayer.2023,<br>" +
                "whichYearsScottishTaxpayer.2022"
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
