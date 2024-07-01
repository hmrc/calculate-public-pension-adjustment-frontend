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

import controllers.routes
import models.{CheckMode, Period, UserAnswers}
import pages.annualallowance.taxyear.PayeCodeAdjustmentPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.format.DateTimeFormatter
import java.util.Locale

object PayeCodeAdjustmentSummary {

  def row(answers: UserAnswers, period: Period)(implicit messages: Messages): Option[SummaryListRow] = {
    val languageTag          = if (messages.lang.code == "cy") "cy" else "en"
    val formatter            = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))
    val startEndDate: String =
      period.start.format(formatter) + " " + messages("startEndDateTo") + " " + period.end.format(formatter)

    answers.get(PayeCodeAdjustmentPage(period)).map { answer =>
      val value = ValueViewModel(
        HtmlContent(
          HtmlFormat.escape(messages(s"payeCodeAdjustment.$answer"))
        )
      )

      SummaryListRowViewModel(
        key = messages("payeCodeAdjustment.checkYourAnswersLabel", startEndDate),
        value = value,
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.PayeCodeAdjustmentController.onPageLoad(CheckMode, period).url
          )
            .withVisuallyHiddenText(messages("payeCodeAdjustment.change.hidden"))
        )
      )
    }
  }
}
