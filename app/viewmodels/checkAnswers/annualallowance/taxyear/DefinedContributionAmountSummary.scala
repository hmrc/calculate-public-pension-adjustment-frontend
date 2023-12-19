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

package viewmodels.checkAnswers.annualallowance.taxyear

import controllers.annualallowance.taxyear.routes.DefinedContributionAmountController
import models.{CheckMode, Period, UserAnswers}
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.DefinedContributionAmountPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DefinedContributionAmountSummary {

  def row(answers: UserAnswers, period: Period)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(DefinedContributionAmountPage(period)).map { answer =>
      val flexibleStartDate = answers.get(FlexibleAccessStartDatePage)

      val startEndDate: String = getStartEndDate(period, flexibleStartDate)

      SummaryListRowViewModel(
        key = messages("definedContributionAmount.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(HtmlContent(currencyFormat(answer))),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            DefinedContributionAmountController.onPageLoad(CheckMode, period).url
          )
            .withVisuallyHiddenText(messages("definedContributionAmount.change.hidden." + period))
        )
      )
    }

  private def getStartEndDate(period: Period, flexibleStartDate: Option[LocalDate]): String = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(period.start) && date.isBefore(period.end) =>
          period.start.format(formatter) + " to " + date.format(formatter)
        case _                                                                     => period.start.format(formatter) + " to " + period.end.format(formatter)
      }

    if (flexibleStartDate == Some(period.start)) {
      period.start.format(formatter) + " to " + flexibleStartDate.get.format(formatter)
    } else {
      normalDateFormatter
    }
  }
}
