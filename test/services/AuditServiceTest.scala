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

import akka.util.Timeout
import base.SpecBase
import models.CalculationResults._
import models.Income.BelowThreshold
import models.TaxYear2016To2023.PostFlexiblyAccessedTaxYear
import models.{AnnualAllowance, CalculationAuditEvent, CalculationResults, Period, TaxYear2011To2015, TaxYearScheme}
import org.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.await
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AuditServiceTest extends SpecBase with MockitoSugar {

  private val mockAuditConnector = mock[AuditConnector]

  implicit val ec = ExecutionContext

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[AuditConnector].toInstance(mockAuditConnector)
    )
    .configure(
      "auditing.calculation-submission-request-event-name" -> "calculation-submission-event"
    )
    .build()

  private val service = app.injector.instanceOf[AuditService]

  implicit def defaultAwaitTimeout: Timeout = 60.seconds

  "AuditService" - {

    "auditCalculationSubmissionRequest" - {
      "should call the audit connector with the CalculationSubmission event" in {

        implicit val hc = HeaderCarrier()

        val calculationInputs = CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Some(
            AnnualAllowance(
              List(Period._2021, Period._2019, Period._2017),
              List(
                TaxYear2011To2015(40000, Period._2013),
                TaxYear2011To2015(40000, Period._2014),
                TaxYear2011To2015(40000, Period._2015),
                PostFlexiblyAccessedTaxYear(
                  33000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 20000, 15000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 20000, 18000, 0)
                  ),
                  Period._2016PreAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  40000,
                  0,
                  60000,
                  3200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 30000, 25000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 18000, 15000, 0)
                  ),
                  Period._2016PostAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  1200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 28000, 25000, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 15000, 13000, 0)
                  ),
                  Period._2017,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 0, 0, 0),
                    TaxYearScheme("Scheme 2", "00348916RG", 30000, 25000, 0)
                  ),
                  Period._2018,
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )

        val calculationResponse = CalculationResponse(
          models.CalculationResults.Resubmission(false, None),
          TotalAmounts(0, 0, 0),
          List(
            OutOfDatesTaxYearsCalculation(
              Period._2016PreAlignment,
              0,
              0,
              0,
              0,
              0,
              0,
              34000,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              Period._2016PostAlignment,
              0,
              0,
              3200,
              0,
              46000,
              18400,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              Period._2017,
              0,
              0,
              1200,
              0,
              36000,
              14400,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            ),
            OutOfDatesTaxYearsCalculation(
              Period._2018,
              0,
              0,
              0,
              0,
              23000,
              9200,
              0,
              List(
                OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0),
                OutOfDatesTaxYearSchemeCalculation("Scheme 2", "00348916RG", 0)
              )
            )
          ),
          List()
        )

        val calculationSubmissionAuditEvent =
          CalculationAuditEvent(calculationInputs, calculationResponse)

        await(service.auditCalculationRequest(calculationSubmissionAuditEvent)(hc)) mustBe ()
      }
    }

  }

}
