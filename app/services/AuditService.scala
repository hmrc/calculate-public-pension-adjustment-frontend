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

package services

import config.FrontendAppConfig
import models.{BeforeCalculationAuditEvent, CalculationAuditEvent, CalculationStartAuditEvent, CalculationTaskListAuditEvent}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject() (
  auditConnector: AuditConnector,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext) {

  def auditBeforeCalculationRequest(event: BeforeCalculationAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.beforeCalculationAuditEventName, event))

  def auditCalculationRequest(event: CalculationAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.calculationAuditEventName, event))

  def auditCalculationStart(event: CalculationStartAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.calculationStartAuditEventName, event))

  def auditCalculationTaskList(event: CalculationTaskListAuditEvent)(implicit
    hc: HeaderCarrier
  ): Future[Unit] =
    Future.successful(auditConnector.sendExplicitAudit(config.calculationTaskListAuditEventName, event))

}
