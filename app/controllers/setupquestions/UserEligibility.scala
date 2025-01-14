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
import models.{AAKickOutStatus, EligibilityAuditEvent, LTAKickOutStatus, NormalMode, ReportingChange}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.setupquestions.ReportingChangePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.setupquestions.UserEligibilityView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UserEligibility @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  auditService: AuditService,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: UserEligibilityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val continueURL                      = request.userAnswers.get(ReportingChangePage) match {
      case Some(set)
          if set.contains(ReportingChange.AnnualAllowance) && request.userAnswers.get(AAKickOutStatus()).contains(2) =>
        request.userAnswers.get(ScottishTaxpayerFrom2016Page) match {
          case None    =>
            controllers.annualallowance.preaaquestions.routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode)
          case Some(_) => controllers.routes.TaskListController.onPageLoad()
        }
      case _ => controllers.routes.TaskListController.onPageLoad()
    }
    val annualAllowanceIncluded: Boolean =
      request.userAnswers.get(ReportingChangePage).exists(_.contains(ReportingChange.AnnualAllowance))

    val eligibleForAA: Boolean = request.userAnswers.get(AAKickOutStatus()) match {
      case Some(2) => true
      case _       => false
    }

    val lifetimeAllowanceIncluded: Boolean =
      request.userAnswers.get(ReportingChangePage).exists(_.contains(ReportingChange.LifetimeAllowance))

    val eligibleForLTA: Boolean = request.userAnswers.get(LTAKickOutStatus()) match {
      case Some(2) => true
      case _       => false
    }

    auditService
      .auditEligibility(
        EligibilityAuditEvent(
          request.userAnswers.uniqueId,
          request.userAnswers.id,
          request.userAnswers.authenticated,
          annualAllowanceIncluded,
          lifetimeAllowanceIncluded,
          eligibleForAA,
          eligibleForLTA
        )
      )
      .map(_ =>
        Ok(view(annualAllowanceIncluded, lifetimeAllowanceIncluded, continueURL, eligibleForLTA, eligibleForAA))
      )

  }
}
