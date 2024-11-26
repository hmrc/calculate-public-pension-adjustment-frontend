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
import forms.PreviousClaimContinueFormProvider

import javax.inject.Inject
import models.{Done, NormalMode}
import pages.PreviousClaimContinuePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.{SubmissionDataService, SubmitBackendService, UserDataService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PreviousClaimContinueView

import scala.concurrent.{ExecutionContext, Future}

class PreviousClaimContinueController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submitBackendService: SubmitBackendService,
  submissionDataService: SubmissionDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PreviousClaimContinueFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PreviousClaimContinueView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      Ok(view(form))
    }

  def onSubmit(): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            for {
              _              <- clearIfNo(value)
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(PreviousClaimContinuePage, value))
            } yield Redirect(
              PreviousClaimContinuePage
                .navigate(NormalMode, updatedAnswers)
            )
        )
    }

  private def clearIfNo(value: Boolean)(implicit hc: HeaderCarrier): Future[Done] =
    if (!value) {
      for {
        _ <- submitBackendService.clearUserAnswers()
        _ <- submitBackendService.clearSubmissions()
        _ <- submitBackendService.clearCalcUserAnswers()
        _ <- submissionDataService.clear()
        r <- userDataService.clear()
      } yield r
    } else {
      Future.successful(Done)
    }

  private def submitFrontendCalculationResultPageUrl() =
    if (config.calculationReviewEnabled) {
      s"${config.submitFrontend}/calculation-results"
    } else {
      s"${config.submitFrontend}/calculation-result"
    }

  def redirect(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    submitBackendService.submissionsPresentInSubmissionService(request.userAnswers.uniqueId)(hc).map {
      result: Boolean =>
        result match {
          case true  =>
            Redirect(Call("GET", submitFrontendCalculationResultPageUrl))
          case false =>
            Redirect(controllers.routes.TaskListController.onPageLoad)
          case _     =>
            Redirect(controllers.routes.JourneyRecoveryController.onPageLoad(None))
        }
    }
  }
}
