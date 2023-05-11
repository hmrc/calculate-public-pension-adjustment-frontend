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

package viewmodels.checkAnswers

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.StopPayingPublicPensionPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object StopPayingPublicPensionSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(StopPayingPublicPensionPage).map { answer =>
      val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

      SummaryListRowViewModel(
        key = "stopPayingPublicPension.checkYourAnswersLabel",
        value = ValueViewModel(answer.format(dateFormatter)),
        actions = Seq(
          ActionItemViewModel("site.change", routes.StopPayingPublicPensionController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("stopPayingPublicPension.change.hidden"))
        )
      )
    }
}
