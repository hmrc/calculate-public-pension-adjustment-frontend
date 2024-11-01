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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import models.CalculationResults.IndividualAASummaryModel
import models.Period
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CalculationResultService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.CalculationReviewIndividualAAView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationReviewIndividualAAController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CalculationReviewIndividualAAView,
  calculationResultService: CalculationResultService,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad(period: Period): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      calculationResultService.sendRequest(request.userAnswers).flatMap { calculationResponse =>
        val allYears: Seq[IndividualAASummaryModel]  =
          calculationResultService.individualAASummaryModel(calculationResponse)
        val individualYear: IndividualAASummaryModel = allYears.find(year => year.period == period).get
        calculationResultService
          .calculationReviewIndividualAAViewModel(calculationResponse, Some(period.toString()), request.userAnswers)
          .map { calculationReviewIndividualAAViewModel =>
            val isInCredit: Boolean = calculationResponse.totalAmounts.inDatesCredit > 0
            val isInDebit: Boolean  = calculationResponse.totalAmounts.inDatesDebit > 0
            Ok(
              view(
                form,
                period.toString(),
                calculationReviewIndividualAAViewModel,
                isInCredit,
                isInDebit,
                individualYear,
                routes.CalculationReviewController.onPageLoad()
              )
            )
          }
      }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Redirect(routes.CalculationReviewController.onPageLoad()))
  }
}
