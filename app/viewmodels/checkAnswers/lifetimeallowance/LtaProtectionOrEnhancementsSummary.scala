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
import pages.lifetimeallowance.LtaProtectionOrEnhancementsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object LtaProtectionOrEnhancementsSummary {

  def row(answers: UserAnswers, changeAllowed: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(LtaProtectionOrEnhancementsPage).map { answer =>
      val value = ValueViewModel(
        HtmlContent(
          HtmlFormat.escape(messages(s"ltaProtectionOrEnhancements.$answer"))
        )
      )

      if (changeAllowed) {
        SummaryListRowViewModel(
          key = "ltaProtectionOrEnhancements.checkYourAnswersLabel",
          value = value,
          actions = Seq(
            ActionItemViewModel("site.change", routes.LtaProtectionOrEnhancementsController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("ltaProtectionOrEnhancements.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = KeyViewModel(s"ltaProtectionOrEnhancements.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = value
        )
      }
    }
}
