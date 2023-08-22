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

package controllers.annualallowance.taxyear

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{Period, SchemeIndex}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.annualallowance.taxyear._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

class CheckYourAAPeriodAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(period: Period): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val schemeIndices = 0.to(4).map(i => SchemeIndex(i))

      val rowsThree: Seq[Option[SummaryListRow]] = Seq(
        OtherDefinedBenefitOrContributionSummary.row(request.userAnswers, period),
        ContributedToDuringRemedyPeriodSummary.row(request.userAnswers, period),
        DefinedContributionAmountSummary.row(request.userAnswers, period),
        FlexiAccessDefinedContributionAmountSummary.row(request.userAnswers, period),
        DefinedBenefitAmountSummary.row(request.userAnswers, period),
        ThresholdIncomeSummary.row(request.userAnswers, period),
        AdjustedIncomeSummary.row(request.userAnswers, period),
        TotalIncomeSummary.row(request.userAnswers, period)
      )

      val rowsTwo: Seq[Option[SummaryListRow]] = schemeIndices.flatMap(index =>
        Seq(
          PensionSchemeDetailsSummary.row(request.userAnswers, period, index),
          PensionSchemeInputAmountsSummary.row(request.userAnswers, period, index),
          PayAChargeSummary.row(request.userAnswers, period, index),
          WhoPaidAAChargeSummary.row(request.userAnswers, period, index),
          HowMuchAAChargeYouPaidSummary.row(request.userAnswers, period, index),
          HowMuchAAChargeSchemePaidSummary.row(request.userAnswers, period, index)
        )
      )

      val rowsOne: Seq[Option[SummaryListRow]] = Seq(
        MemberOfPublicPensionSchemeSummary.row(request.userAnswers, period),
        MemberMoreThanOnePensionSummary.row(request.userAnswers, period)
      ) ++ rowsTwo ++ rowsThree

      Ok(
        view(
          s"checkYourAnswers.aa.period.subHeading.$period",
          controllers.routes.TaskListController.onPageLoad(),
          SummaryListViewModel(rowsOne.flatten)
        )
      )
  }
}
