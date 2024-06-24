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

import connectors.CalculateBackendConnector
import logging.Logging
import models.{Done, Period, TaxRateStatusRequest, UserAnswers}
import pages.annualallowance.preaaquestions.WhichYearsScottishTaxpayerPage
import pages.annualallowance.taxyear.TotalIncomePage
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class CalculateBackendService @Inject() (connector: CalculateBackendConnector) extends Logging {

  def findTaxRateStatus(userAnswers: UserAnswers, period: Period)(implicit
    headerCarrier: HeaderCarrier
  ): Future[Boolean] =
    connector.findTaxRateStatus(buildTaxRateStatusRequest(userAnswers, period))

  def updateUserAnswersFromCalcUA(id: String)(implicit hc: HeaderCarrier): Future[Done] =
    connector.updateUserAnswersFromCalcUA(id)

  def buildTaxRateStatusRequest(userAnswers: UserAnswers, period: Period): TaxRateStatusRequest = {

    val income = userAnswers
      .get(
        TotalIncomePage(period)
      )
      .get
      .toInt

    val scottishTaxYears: List[Period] = userAnswers.data.fields
      .find(_._1 == WhichYearsScottishTaxpayerPage.toString)
      .fold {
        List.empty[Period]
      } {
        _._2.as[List[String]] flatMap { sYear =>
          Period.fromString(sYear)
        }
      }

    TaxRateStatusRequest(
      period,
      income,
      scottishTaxYears
    )

  }
}
