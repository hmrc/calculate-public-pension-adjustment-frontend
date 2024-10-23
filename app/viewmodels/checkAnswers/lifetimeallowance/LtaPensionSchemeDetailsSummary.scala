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

import controllers.lifetimeallowance.routes
import models.{CheckMode, UserAnswers}
import pages.lifetimeallowance.LtaPensionSchemeDetailsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.checkAnswers.FormatUtils.keyCssClass

object LtaPensionSchemeDetailsSummary {

  def row(answers: UserAnswers, changeAllowed: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(LtaPensionSchemeDetailsPage).map { answer =>
      val value = HtmlFormat.escape(answer.name).toString + " / " + HtmlFormat.escape(answer.taxRef).toString

      if (changeAllowed) {
        SummaryListRowViewModel(
          key = "ltaPensionSchemeDetails.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(value)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.LtaPensionSchemeDetailsController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("ltaPensionSchemeDetails.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = KeyViewModel(s"ltaPensionSchemeDetails.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent(value))
        )
      }
    }
}
