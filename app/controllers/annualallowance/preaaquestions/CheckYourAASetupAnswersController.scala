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

package controllers.annualallowance.preaaquestions

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import pages.annualallowance.preaaquestions.PayTaxCharge1516Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.annualallowance.preaaquestions._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAASetupAnswersView

class CheckYourAASetupAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAASetupAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val rows: Seq[Option[SummaryListRow]] = Seq(
      ScottishTaxpayerFrom2016Summary.row(request.userAnswers),
      WhichYearsScottishTaxpayerSummary.row(request.userAnswers),
      PayingPublicPensionSchemeSummary.row(request.userAnswers),
      StopPayingPublicPensionSummary.row(request.userAnswers),
      DefinedContributionPensionSchemeSummary.row(request.userAnswers),
      FlexiblyAccessedPensionSummary.row(request.userAnswers),
      FlexibleAccessStartDateSummary.row(request.userAnswers),
      PayTaxCharge1516Summary.row(request.userAnswers)
    )

    val pIARows: Seq[Option[SummaryListRow]] =
      PIAPreRemedySummary.rows(request.userAnswers)

    Ok(
      view(
//        maybePensionInputAmounts(request.userAnswers),
        "checkYourAnswers.aa.subHeading",
        controllers.routes.TaskListController.onPageLoad(),
        SummaryListViewModel(rows.flatten),
        "checkYourAnswers.aa.pIASubHeading",
        SummaryListViewModel(pIARows.flatten)
      )
    )


    //TODO MAKE DYNAMIC
//    def maybePensionInputAmounts(userAnswers: UserAnswers): Boolean =
//      userAnswers.get(PayTaxCharge1516Page) match {
//        case Some(true) => true
//        case _ => false
//      }


  }
}
