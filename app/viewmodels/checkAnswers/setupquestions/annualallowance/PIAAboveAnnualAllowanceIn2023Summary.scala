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

package viewmodels.checkAnswers.setupquestions.annualallowance

import models.{CheckMode, UserAnswers}
import pages.setupquestions.annualallowance.PIAAboveAnnualAllowanceIn2023Page
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PIAAboveAnnualAllowanceIn2023Summary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PIAAboveAnnualAllowanceIn2023Page).map { answer =>
      val value = if (answer) "site.yes" else "site.no"

      SummaryListRowViewModel(
        key = "pIAAboveAnnualAllowanceIn2023.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.setupquestions.annualallowance.routes.PIAAboveAnnualAllowanceIn2023Controller
              .onPageLoad(CheckMode)
              .url
          )
            .withVisuallyHiddenText(messages("pIAAboveAnnualAllowanceIn2023.change.hidden"))
        )
      )
    }
}
