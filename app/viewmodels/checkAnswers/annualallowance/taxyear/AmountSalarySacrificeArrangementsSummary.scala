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

import models.{CheckMode, Period, UserAnswers}
import pages.annualallowance.taxyear.AmountSalarySacrificeArrangementsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.format.DateTimeFormatter
import java.util.Locale

object AmountSalarySacrificeArrangementsSummary {

  def row(answers: UserAnswers, period: Period)(implicit
    messages: Messages
  ): Option[SummaryListRow] = {

    val languageTag          = if (messages.lang.code == "cy") "cy" else "en"
    val formatter            = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))
    val startEndDate: String =
      period.start.format(formatter) + " " + messages("startEndDateTo") + " " + period.end.format(formatter)

    answers.get(AmountSalarySacrificeArrangementsPage(period)).map { answer =>
      SummaryListRowViewModel(
        key = messages("amountSalarySacrificeArrangements.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(HtmlContent(currencyFormat(answer))),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.AmountSalarySacrificeArrangementsController
              .onPageLoad(CheckMode, period)
              .url
          )
            .withVisuallyHiddenText(messages("amountSalarySacrificeArrangements.change.hidden"))
        )
      )
    }
  }
}
