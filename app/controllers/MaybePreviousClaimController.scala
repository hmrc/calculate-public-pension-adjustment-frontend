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

import controllers.actions._
import models.requests.{AuthenticatedIdentifierRequest, OptionalDataRequest}
import models.{Done, NormalMode, SubmissionStatusResponse, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendService, SubmitBackendService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MaybePreviousClaimController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submitBackendService: SubmitBackendService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  calculateBackendService: CalculateBackendService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def redirect(): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    for {
      _             <- if (isAuthenticated(request) && request.userAnswers.isEmpty) {
                         calculateBackendService.updateUserAnswersFromCalcUA(request.userId)
                       } else {
                         Future.successful(Done)
                       }
      updatedAnswers = request.userAnswers.getOrElse(constructUserAnswers(request))
      redirectUrl   <- generateRedirect(request, updatedAnswers)
    } yield Redirect(redirectUrl)
  }

  private def generateRedirect(
    request: OptionalDataRequest[AnyContent],
    userAnswers: UserAnswers
  ): Future[String] =
    if (userAnswers.authenticated) {
      val headerCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      userDataService
        .checkSubmissionStatusWithId(request.userId)(headerCarrier)
        .flatMap {
          case Some(SubmissionStatusResponse(_, true))  =>
            submitBackendService
              .submissionsPresentInSubmissionServiceWithId(request.userId)(headerCarrier)
              .map {
                case true  =>
                  routes.PreviousClaimContinueController.onPageLoad().url
                case false =>
                  for {
                    _ <- userDataService.updateSubmissionStatus(request.userId)(headerCarrier)
                    _ <- submitBackendService.clearUserAnswers()(headerCarrier)
                    r <- submitBackendService.clearSubmissions()(headerCarrier)
                  } yield r
                  routes.PreviousClaimContinueController.onPageLoad().url
              }
          case Some(SubmissionStatusResponse(_, false)) =>
            Future.successful(routes.PreviousClaimContinueController.onPageLoad().url)
          case None                                     =>
            submitBackendService
              .submissionsPresentInSubmissionServiceWithId(request.userId)(headerCarrier)
              .map {
                case true  =>
                  routes.PreviousClaimContinueController.onPageLoad().url
                case false =>
                  controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode).url
              }
        }
    } else {
      Future.successful(controllers.setupquestions.routes.ResubmittingAdjustmentController.onPageLoad(NormalMode).url)
    }

  private def constructUserAnswers(request: OptionalDataRequest[AnyContent]) =
    UserAnswers(request.userId, authenticated = isAuthenticated(request))

  private def isAuthenticated(request: OptionalDataRequest[AnyContent]): Boolean =
    request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }
}
