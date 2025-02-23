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

package controllers.setupquestions.annualallowance

import config.FrontendAppConfig
import controllers.actions._
import models.{KickOffAuditEvent, LTAKickOutStatus, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Constants.TriageJourneyNotEligibleNoRpssKickOff
import views.html.setupquestions.annualallowance.NotAbleToUseThisServiceAAView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class NotAbleToUseThisServiceAAController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  auditService: AuditService,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: NotAbleToUseThisServiceAAView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val ltaKickOutStatusStatus = request.userAnswers.get(LTAKickOutStatus())

    val shouldShowContinueButton = ltaKickOutStatusStatus match {
      case Some(1) =>
        true
      case Some(2) =>
        true
      case Some(_) =>
        false
      case None    =>
        false
    }

    val urlFromStatus = ltaKickOutStatusStatus match {
      case Some(1) =>
        controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
          .onPageLoad(NormalMode)
          .url
      case Some(2) =>
        controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad().url
      case Some(_) =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None).url
      case None    =>
        controllers.routes.JourneyRecoveryController.onPageLoad(None).url
    }

    auditService
      .auditKickOff(
        config.triageJourneyNotEligibleNoRpssKickOff,
        KickOffAuditEvent(
          request.userAnswers.uniqueId,
          request.userAnswers.id,
          request.userAnswers.authenticated,
          TriageJourneyNotEligibleNoRpssKickOff
        )
      )
      .map(_ => Ok(view(shouldShowContinueButton, urlFromStatus)))
  }
}
