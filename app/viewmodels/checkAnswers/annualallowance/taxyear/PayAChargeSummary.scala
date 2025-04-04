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
import pages.annualallowance.taxyear.PayAChargePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.format.DateTimeFormatter
import java.util.Locale

object PayAChargeSummary {

  def row(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(PayAChargePage(period, schemeIndex)).map { answer =>
      val value = if (answer) "site.yes" else "site.no"

      val languageTag          = if (messages.lang.code == "cy") "cy" else "en"
      val formatter            = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(languageTag))
      val startEndDate: String =
        period.start.format(formatter) + " " + messages("startEndDateTo") + " " + period.end.format(formatter)

      SummaryListRowViewModel(
        key = messages("payACharge.checkYourAnswersLabel", startEndDate),
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            controllers.annualallowance.taxyear.routes.PayAChargeController
              .onPageLoad(CheckMode, period, schemeIndex)
              .url
          )
            .withVisuallyHiddenText(messages("payACharge.change.hidden", startEndDate))
        )
      )
    }
}
