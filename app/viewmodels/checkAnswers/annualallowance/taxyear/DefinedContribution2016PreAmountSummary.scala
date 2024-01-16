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
import pages.annualallowance.taxyear.DefinedContribution2016PreAmountPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DefinedContribution2016PreAmountSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DefinedContribution2016PreAmountPage).map { answer =>
      val flexibleStartDate = answers.get(FlexibleAccessStartDatePage)

      val startEndDate: String = getStartEndDate(flexibleStartDate)
      SummaryListRowViewModel(
        key = messages("definedContribution2016PreAmount.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(HtmlContent(currencyFormat(answer))),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.DefinedContribution2016PreAmountController
              .onPageLoad(CheckMode)
              .url
          )
            .withVisuallyHiddenText(messages("definedContribution2016PreAmount.change.hidden"))
        )
      )
    }

  private def getStartEndDate(flexibleStartDate: Option[LocalDate])(implicit messages: Messages): String = {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)

    def normalDateFormatter =
      flexibleStartDate match {
        case Some(date) if date.isAfter(Period.pre2016Start) && date.isBefore(Period.pre2016End) =>
          Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + date.format(formatter)
        case _                                                                                   =>
          Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + Period.pre2016End.format(
            formatter
          )
      }

    if (flexibleStartDate == Some(Period.pre2016Start)) {
      Period.pre2016Start.format(formatter) + " " + messages("startEndDateAnd") + " " + flexibleStartDate.get.format(
        formatter
      )
    } else {
      normalDateFormatter
    }
  }
}
