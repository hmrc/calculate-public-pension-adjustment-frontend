package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.InterestFromSavingsPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object InterestFromSavingsSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(InterestFromSavingsPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "interestFromSavings.checkYourAnswersLabel",
          value   = ValueViewModel(answer.toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.InterestFromSavingsController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("interestFromSavings.change.hidden"))
          )
        )
    }
}
