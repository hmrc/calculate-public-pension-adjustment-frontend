/*
 * Copyright 2023 HM Revenue & Customs
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
import pages.annualallowance.taxyear.DefinedBenefitAmountPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.format.DateTimeFormatter
import java.util.Locale

object DefinedBenefitAmountSummary {

  def row(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(DefinedBenefitAmountPage(period, schemeIndex)).map { answer =>
      val formatter            = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
      val startEndDate: String = period.start.format(formatter) + " to " + period.end.format(formatter)

      SummaryListRowViewModel(
        key = messages("definedBenefitAmount.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(answer.toString),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController
              .onPageLoad(CheckMode, period, schemeIndex)
              .url
          )
            .withVisuallyHiddenText(messages("definedBenefitAmount.change.hidden"))
        )
      )
    }
}
