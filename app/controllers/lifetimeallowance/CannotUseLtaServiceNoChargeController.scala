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

package controllers.lifetimeallowance

import config.FrontendAppConfig
import controllers.actions._
import models.{KickOffAuditEvent, ReportingChange}
import pages.setupquestions.ReportingChangePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.lifetimeallowance.CannotUseLtaServiceNoChargeView
import views.html.setupquestions.IneligibleView
import utils.Constants._

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CannotUseLtaServiceNoChargeController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  auditService: AuditService,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: CannotUseLtaServiceNoChargeView
)(implicit ec: ExecutionContext) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val annualAllowanceIncluded: Boolean =
      request.userAnswers.get(ReportingChangePage).exists(_.contains(ReportingChange.AnnualAllowance))

    auditService
      .auditKickOff(config.cannotUseLtaServiceNoCharge,
        KickOffAuditEvent(
          request.userAnswers.uniqueId,
          request.userAnswers.id,
          request.userAnswers.authenticated,
          CannotUseLtaServiceNoCharge
        )
      )
      .map(_ => Ok(view(annualAllowanceIncluded)))
  }
}
