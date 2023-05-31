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

package viewmodels.checkAnswers.annualallowance.preaaquestions

import models.PIAPreRemedyTaxYear.{TaxYear2012, TaxYear2013, TaxYear2014}
import models.{CheckMode, PIAPreRemedyTaxYear, UserAnswers}
import pages.annualallowance.preaaquestions
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import controllers.annualallowance.preaaquestions.routes

object PIAPreRemedySummary {

  def rows(answers: UserAnswers)(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    Seq(
      row(answers, TaxYear2012),
      row(answers, TaxYear2013),
      row(answers, TaxYear2014)
    )

  private def row(answers: UserAnswers, taxYear: PIAPreRemedyTaxYear)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(preaaquestions.PIAPreRemedyPage(taxYear)).map { answer =>
      SummaryListRowViewModel(
        key = s"pIAPreRemedy.checkYourAnswersLabel.${taxYear.value}",
        value = ValueViewModel(HtmlContent(currencyFormat(answer))),
        actions = Seq(
          ActionItemViewModel("site.change", routes.PIAPreRemedyController.onPageLoad(CheckMode, taxYear).url)
            .withVisuallyHiddenText(messages("pIAPreRemedy.change.hidden"))
        )
      )
    }
}
