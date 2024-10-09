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
import pages.lifetimeallowance.DateOfBenefitCrystallisationEventPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.helpers.ImplicitDateFormatter

object DateOfBenefitCrystallisationEventSummary extends ImplicitDateFormatter {

  def row(answers: UserAnswers, changeAllowed: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DateOfBenefitCrystallisationEventPage).map { answer =>
      val languageTag = if (messages.lang.code == "cy") "cy" else "en"

      if(changeAllowed) {
        SummaryListRowViewModel(
          key = "dateOfBenefitCrystallisationEvent.checkYourAnswersLabel",
          value = ValueViewModel(dateToString(answer, languageTag)),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.DateOfBenefitCrystallisationEventController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText(messages("dateOfBenefitCrystallisationEvent.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = "dateOfBenefitCrystallisationEvent.checkYourAnswersLabel",
          value = ValueViewModel(dateToString(answer, languageTag)).withCssClass("govuk-!-width-one-half")
        )
      }
    }
}
