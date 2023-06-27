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

import controllers.annualallowance.taxyear.routes.ContributedToDuringRemedyPeriodController
import models.{CheckMode, Period, SchemeIndex, UserAnswers}
import pages.annualallowance.taxyear.ContributedToDuringRemedyPeriodPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ContributedToDuringRemedyPeriodSummary {

  def row(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(ContributedToDuringRemedyPeriodPage(period, schemeIndex)).map { answers =>
      val value = ValueViewModel(
        HtmlContent(
          answers
            .map { answer =>
              HtmlFormat.escape(messages(s"contributedToDuringRemedyPeriod.$answer")).toString
            }
            .mkString(",<br>")
        )
      )

      SummaryListRowViewModel(
        key = "contributedToDuringRemedyPeriod.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            ContributedToDuringRemedyPeriodController.onPageLoad(CheckMode, period, schemeIndex).url
          )
            .withVisuallyHiddenText(messages("contributedToDuringRemedyPeriod.change.hidden"))
        )
      )
    }
}
