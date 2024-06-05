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

import config.FrontendAppConfig
import controllers.actions._
import controllers.routes
import forms.SavingsStatementFormProvider
import models.requests.{AuthenticatedIdentifierRequest, OptionalDataRequest}
import models.tasklist.sections.SetupSection
import models.{Done, Mode, SubmissionStatusResponse, UserAnswers}
import pages.setupquestions.SavingsStatementPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendService, SubmitBackendService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.setupquestions.SavingsStatementView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SavingsStatementController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submitBackendService: SubmitBackendService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: SavingsStatementFormProvider,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: SavingsStatementView,
  calculateBackendService: CalculateBackendService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    for {
      _ <- if (isAuthenticated(request) && request.userAnswers.isEmpty) {
             calculateBackendService.updateUserAnswersFromCalcUA(request.userId)
           } else {
             Future.successful(Done)
           }
    } yield {
      val preparedForm =
        request.userAnswers
          .getOrElse(UserAnswers(request.userId))
          .get(SavingsStatementPage(config.optionalAuthEnabled)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

      Ok(view(preparedForm, mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          for {
            updatedAnswers <-
              Future
                .fromTry(
                  request.userAnswers
                    .getOrElse(constructUserAnswers(request))
                    .set(SavingsStatementPage(config.optionalAuthEnabled), value)
                )
            redirectUrl    <- generateRedirect(request, mode, updatedAnswers)
            answersWithNav  = generateNav(updatedAnswers, mode, redirectUrl)
            _              <- userDataService.set(answersWithNav)
          } yield Redirect(redirectUrl)
      )
  }

  private def generateRedirect(
    request: OptionalDataRequest[AnyContent],
    mode: Mode,
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
                  SavingsStatementPage(config.optionalAuthEnabled).navigate(mode, userAnswers).url
              }
        }
    } else {
      Future.successful(SavingsStatementPage(config.optionalAuthEnabled).navigate(mode, userAnswers).url)
    }

  private def generateNav(userAnswers: UserAnswers, mode: Mode, redirectUrl: String): UserAnswers =
    if (redirectUrl.equals(SavingsStatementPage(config.optionalAuthEnabled).navigate(mode, userAnswers).url)) {
      SetupSection.saveNavigation(userAnswers, redirectUrl)
    } else {
      userAnswers
    }

  private def constructUserAnswers(request: OptionalDataRequest[AnyContent]) =
    UserAnswers(request.userId, authenticated = isAuthenticated(request))

  private def isAuthenticated(request: OptionalDataRequest[AnyContent]): Boolean =
    request.request match {
      case AuthenticatedIdentifierRequest(_, _) => true
      case _                                    => false
    }

}
