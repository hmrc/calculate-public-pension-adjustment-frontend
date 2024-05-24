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

import connectors.SubmitBackendConnector
import logging.Logging
import models.Done
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class SubmitBackendService @Inject() (connector: SubmitBackendConnector) extends Logging {
  def clearUserAnswers()(implicit hc: HeaderCarrier): Future[Done] =
    connector.clearUserAnswers()

  def clearSubmissions()(implicit hc: HeaderCarrier): Future[Done] =
    connector.clearSubmissions()

  def clearCalcUserAnswers()(implicit hc: HeaderCarrier): Future[Done] =
    connector.clearCalcUserAnswers()

  def userAnswersPresentInSubmissionService(uniqueId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    connector.userAnswersPresentInSubmissionService(uniqueId)

  def submissionsPresentInSubmissionService(uniqueId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    connector.submissionsPresentInSubmissionService(uniqueId)

  def submissionsPresentInSubmissionServiceWithId(id: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    connector.submissionsPresentInSubmissionServiceWithId(id)

}
