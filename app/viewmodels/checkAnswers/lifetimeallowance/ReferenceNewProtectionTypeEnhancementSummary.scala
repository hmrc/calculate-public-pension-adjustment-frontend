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
import pages.lifetimeallowance.ReferenceNewProtectionTypeEnhancementPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object ReferenceNewProtectionTypeEnhancementSummary {

  def row(answers: UserAnswers, changeAllowed: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ReferenceNewProtectionTypeEnhancementPage).map { answer =>
      if (changeAllowed) {
        SummaryListRowViewModel(
          key = "referenceNewProtectionTypeEnhancement.checkYourAnswersLabel",
          value = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ReferenceNewProtectionTypeEnhancementController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText(messages("referenceNewProtectionTypeEnhancement.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = KeyViewModel(s"referenceNewProtectionTypeEnhancement.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlFormat.escape(answer).toString)
        )
      }
    }
}
