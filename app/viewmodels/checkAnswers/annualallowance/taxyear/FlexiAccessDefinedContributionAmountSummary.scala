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
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.FlexiAccessDefinedContributionAmountPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object FlexiAccessDefinedContributionAmountSummary {

  def row(answers: UserAnswers, period: Period)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(FlexiAccessDefinedContributionAmountPage(period)).map { answer =>
      val flexibleStartDate = answers.get(FlexibleAccessStartDatePage)

      val startEndDate: String = getStartEndDate(period, flexibleStartDate)

      SummaryListRowViewModel(
        key = messages("flexiAccessDefinedContributionAmount.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(HtmlContent(currencyFormat(answer))),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.FlexiAccessDefinedContributionAmountController
              .onPageLoad(CheckMode, period)
              .url
          )
            .withVisuallyHiddenText(messages("flexiAccessDefinedContributionAmount.change.hidden", startEndDate))
        )
      )
    }

  private def getStartEndDate(period: Period, flexibleStartDate: Option[LocalDate])(implicit
    messages: Messages
  ): String = {
    val languageTag = if (messages.lang.code == "cy") "cy" else "en"
    val formatter   = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(period.start) && date.isBefore(period.end) =>
          date.plusDays(1).format(formatter) + " to " + period.end.format(formatter)
        case _                                                                     => period.start.format(formatter) + " to " + period.end.format(formatter)
      }

    if (flexibleStartDate == Some(period.start)) {
      flexibleStartDate.get.plusDays(1).format(formatter) + " to " + period.end.format(formatter)
    } else {
      normalDateFormatter
    }

  }
}
