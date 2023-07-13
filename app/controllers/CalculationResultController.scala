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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import models.submission
import models.submission.SubmissionResponse
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CalculationResultService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CalculationResultsView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CalculationResultController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CalculationResultsView,
  calculationResultService: CalculationResultService,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    calculationResultService.sendRequest(request.userAnswers).map { calculationResponse =>
      val includeCompensation2015: Boolean = calculationResponse.totalAmounts.outDatesCompensation > 0
      Ok(
        view(
          form,
          calculationResultService.calculationResultsViewModel(calculationResponse),
          includeCompensation2015
        )
      )
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    calculationResultService.submitUserAnswersAndCalculation(request.userAnswers).map {
      submissionResponse: SubmissionResponse =>
        submissionResponse match {
          case submission.Success(uniqueId) => Redirect(submitFrontendLandingPageUrl(uniqueId))
          case submission.Failure(_)        => Redirect(routes.JourneyRecoveryController.onPageLoad())
        }
    }
  }

  private def submitFrontendLandingPageUrl(uniqueId: String) =
    s"${config.submitFrontend}/landing-page?submissionUniqueId=$uniqueId"
}
