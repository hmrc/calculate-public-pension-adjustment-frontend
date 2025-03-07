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

package viewmodels.checkAnswers.lifetimeallowance

import models.{CheckMode, UserAnswers}
import pages.lifetimeallowance.NewAnnualPaymentValuePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object NewAnnualPaymentValueSummary {

  def row(answers: UserAnswers, changeAllowed: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(NewAnnualPaymentValuePage).map { answer =>
      if (changeAllowed) {
        SummaryListRowViewModel(
          key = "newAnnualPaymentValue.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(currencyFormat(answer))),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.lifetimeallowance.routes.NewAnnualPaymentValueController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText(messages("newAnnualPaymentValue.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = KeyViewModel(s"newAnnualPaymentValue.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent(currencyFormat(answer)))
        )
      }
    }
}
