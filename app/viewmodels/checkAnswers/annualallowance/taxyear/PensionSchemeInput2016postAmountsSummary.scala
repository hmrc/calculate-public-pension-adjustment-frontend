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

import models.{CheckMode, Period, SchemeIndex, UserAnswers}
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PensionSchemeInput2016postAmountsPage, PensionSchemeInputAmountsPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PensionSchemeInput2016postAmountsSummary {

  def row(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(PensionSchemeInput2016postAmountsPage(period, schemeIndex)).map { answer =>
      val value = HtmlContent(currencyFormat(answer.originalPIA) + " / " + currencyFormat(answer.revisedPIA))

      val schemeName = answers.get(PensionSchemeDetailsPage(period, schemeIndex)).map { answer =>
        answer.schemeName
      }

      SummaryListRowViewModel(
        key = messages("pensionSchemeInputAmounts.2016-post.checkYourAnswersLabel", schemeName.get),
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.PensionSchemeInput2016postAmountsController
              .onPageLoad(CheckMode, period, schemeIndex)
              .url
          )
            .withVisuallyHiddenText(messages("pensionSchemeInputAmounts.change.hidden"))
        )
      )
    }
}
