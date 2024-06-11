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
import pages.annualallowance.taxyear.DoYouKnowPersonalAllowancePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DoYouKnowPersonalAllowanceSummary {

  def row(answers: UserAnswers, period: Period)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DoYouKnowPersonalAllowancePage(period)).map { answer =>
      val value = if (answer) "site.yes" else "site.no"

      SummaryListRowViewModel(
        key = "doYouKnowPersonalAllowance.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController
              .onPageLoad(CheckMode, period)
              .url
          )
            .withVisuallyHiddenText(messages("doYouKnowPersonalAllowance.change.hidden"))
        )
      )
    }
}