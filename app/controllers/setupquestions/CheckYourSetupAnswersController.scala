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

package controllers.setupquestions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{NormalMode, ReportingChange}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.setupquestions.{ReasonForResubmissionSummary, ReportingChangeSummary, ResubmittingAdjustmentSummary, SavingsStatementSummary}
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView
import pages.setupquestions.ReportingChangePage
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page

class CheckYourSetupAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val rows: Seq[Option[SummaryListRow]] = Seq(
      SavingsStatementSummary.row(request.userAnswers),
      ResubmittingAdjustmentSummary.row(request.userAnswers),
      ReasonForResubmissionSummary.row(request.userAnswers),
      ReportingChangeSummary.row(request.userAnswers)
    )

    val continueURL = request.userAnswers.get(ReportingChangePage) match {
      case Some(set) if set.contains(ReportingChange.AnnualAllowance) =>
        request.userAnswers.get(ScottishTaxpayerFrom2016Page) match {
          case None    =>
            controllers.annualallowance.preaaquestions.routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode)
          case Some(_) => controllers.routes.TaskListController.onPageLoad()
        }
      case _                                                          => controllers.routes.TaskListController.onPageLoad()
    }
    Ok(view("checkYourAnswers.setup.subHeading", continueURL, SummaryListViewModel(rows.flatten)))
  }
}
