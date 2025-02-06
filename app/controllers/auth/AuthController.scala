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

package controllers.auth

import config.FrontendAppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.AuthenticatedUserSaveAndReturnAuditEvent
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AuditService, SubmissionDataService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  userDataService: UserDataService,
  submissionDataService: SubmissionDataService,
  auditService: AuditService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def signOut(): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    auditService
      .auditAuthenticatedUserSignOut(
        AuthenticatedUserSaveAndReturnAuditEvent(
          request.userId,
          request.userAnswers.map(_.uniqueId),
          request.userAnswers.map(_.authenticated)
        )
      )
      .map { _ =>
        Redirect(
          config.signOutUrl,
          Map("continue" -> Seq(config.baseUrl + routes.SignedOutController.onPageLoad().url))
        )
      }
  }

  def signOutUnauthorised(): Action[AnyContent] = Action(
    Redirect(config.signOutUrl, Map("continue" -> Seq(config.redirectToStartPage)))
  )

  def sessionTimeout(): Action[AnyContent] = identify.async { implicit request =>
    if (request.authenticated)
      Future.successful(
        Redirect(
          config.signOutUrl,
          Map("continue" -> Seq(config.baseUrl + routes.SignedOutController.onPageLoad().url))
        )
      )
    else
      for {
        _ <- userDataService.clear()
        _ <- submissionDataService.clear()
      } yield Redirect(config.exitSurveyUrl)
  }
}
