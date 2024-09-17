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

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.requests.{AuthenticatedIdentifierRequest, OptionalDataRequest}
import models.{Done, Mode, NormalMode, SubmissionStatusResponse, UserAnswers}

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.{CalculateBackendService, SubmitBackendService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  userDataService: UserDataService,
  submitBackendService: SubmitBackendService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  calculateBackendService: CalculateBackendService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    for {
      _           <- if (isAuthenticated(request) && request.userAnswers.isEmpty) {
                       calculateBackendService.updateUserAnswersFromCalcUA(request.userId)
                     } else {
                       Future.successful(Done)
                     }
      redirectUrl <- generateRedirect(request)
    } yield Redirect(redirectUrl)
  }

  private def generateRedirect(
    request: OptionalDataRequest[AnyContent]
  ): Future[String] =
    if (request.userAnswers.getOrElse(constructUserAnswers(request)).authenticated) {
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
      Future.successful(controllers.routes.OptionalSignInController.onPageLoad().url)
    }

  private def constructUserAnswers(request: OptionalDataRequest[AnyContent]) =
    UserAnswers(request.userId, authenticated = isAuthenticated(request))

  private def isAuthenticated(request: OptionalDataRequest[AnyContent]): Boolean =
    request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }
}
