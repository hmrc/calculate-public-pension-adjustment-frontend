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

import base.SpecBase
import models.CalculationResults._
import models.Income.BelowThreshold
import models.TaxYear2016To2023.PostFlexiblyAccessedTaxYear
import models.{AnnualAllowance, BeforeCalculationAuditEvent, CalculationAuditEvent, CalculationResults, CalculationStartAuditEvent, CalculationTaskListAuditEvent, IncomeSubJourney, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, Period, SectionStatus, TaxYear2011To2015, TaxYearScheme}
import org.apache.pekko.util.Timeout
import org.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.await
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AuditServiceSpec extends SpecBase with MockitoSugar {

  private val mockAuditConnector = mock[AuditConnector]

  implicit val ec: ExecutionContext.type = ExecutionContext

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

    "auditBeforeCalculationRequest" - {
      "should call the audit connector with the BeforeCalculationAudit event" in {

        implicit val hc = HeaderCarrier()

        val calculationInputs = CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(
              AnnualAllowanceSetup(
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(MaybePIAIncrease.No),
                Some(MaybePIAUnchangedOrDecreased.No),
                Some(false),
                Some(false),
                Some(false),
                Some(false)
              )
            ),
            Some(
              LifetimeAllowanceSetup(
                Some(true),
                Some(false),
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(true)
              )
            )
          ),
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
                    TaxYearScheme("Scheme 1", "00348916RT", 15000, 0, Some(25000)),
                    TaxYearScheme("Scheme 2", "00348916RG", 18000, 0, Some(25000))
                  ),
                  Period._2016,
                  IncomeSubJourney(
                    None,
                    None,
                    None,
                    None,
                    None,
                    Some(888),
                    None,
                    None,
                    None,
                    None,
                    None,
                    None,
                    None,
                    Some(2291),
                    None,
                    None
                  )
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  1200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 25000, 0, None),
                    TaxYearScheme("Scheme 2", "00348916RG", 13000, 0, None)
                  ),
                  Period._2017,
                  IncomeSubJourney(
                    Some(444),
                    Some(666),
                    Some(712),
                    Some(777),
                    Some(true),
                    Some(888),
                    None,
                    Some(1111),
                    Some(1212),
                    Some(1414),
                    Some(842),
                    None,
                    Some(90),
                    Some(2291),
                    None,
                    None
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 0, 0, None),
                    TaxYearScheme("Scheme 2", "00348916RG", 25000, 0, None)
                  ),
                  Period._2018,
                  IncomeSubJourney(
                    Some(444),
                    Some(666),
                    Some(712),
                    Some(777),
                    Some(true),
                    Some(888),
                    None,
                    Some(1111),
                    Some(1212),
                    Some(1414),
                    Some(842),
                    None,
                    None,
                    Some(2291),
                    None,
                    None
                  ),
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )

        val beforeCalculationSubmissionAuditEvent =
          BeforeCalculationAuditEvent(
            "8453ea66-e3fe-4f35-b6c2-a6aa87482661",
            true,
            "AA000000A",
            calculationInputs
          )

        await(service.auditBeforeCalculationRequest(beforeCalculationSubmissionAuditEvent)(hc)) mustBe ()
      }
    }

    "auditCalculationRequest" - {
      "should call the audit connector with the CalculationSubmission event" in {

        implicit val hc = HeaderCarrier()

        val calculationInputs = CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(
              AnnualAllowanceSetup(
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(false),
                Some(MaybePIAIncrease.No),
                Some(MaybePIAUnchangedOrDecreased.No),
                Some(false),
                Some(false),
                Some(false),
                Some(false)
              )
            ),
            Some(
              LifetimeAllowanceSetup(
                Some(true),
                Some(false),
                Some(true),
                Some(false),
                Some(false),
                Some(false),
                Some(true)
              )
            )
          ),
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
                    TaxYearScheme("Scheme 1", "00348916RT", 15000, 0, Some(25000)),
                    TaxYearScheme("Scheme 2", "00348916RG", 18000, 0, Some(25000))
                  ),
                  Period._2016,
                  IncomeSubJourney(
                    None,
                    None,
                    None,
                    None,
                    None,
                    Some(888),
                    None,
                    None,
                    None,
                    None,
                    None,
                    None,
                    None,
                    Some(2291),
                    None,
                    None
                  )
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  1200,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 25000, 0, None),
                    TaxYearScheme("Scheme 2", "00348916RG", 13000, 0, None)
                  ),
                  Period._2017,
                  IncomeSubJourney(
                    Some(444),
                    Some(666),
                    Some(712),
                    Some(777),
                    Some(true),
                    Some(888),
                    None,
                    Some(1111),
                    Some(1212),
                    Some(1414),
                    Some(842),
                    None,
                    Some(90),
                    Some(2291),
                    None,
                    None
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  38000,
                  0,
                  60000,
                  0,
                  List(
                    TaxYearScheme("Scheme 1", "00348916RT", 0, 0, None),
                    TaxYearScheme("Scheme 2", "00348916RG", 25000, 0, None)
                  ),
                  Period._2018,
                  IncomeSubJourney(
                    Some(444),
                    Some(666),
                    Some(712),
                    Some(777),
                    Some(true),
                    Some(888),
                    None,
                    Some(1111),
                    Some(1212),
                    Some(1414),
                    Some(842),
                    None,
                    None,
                    Some(2291),
                    None,
                    None
                  ),
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
              Period._2016,
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
          CalculationAuditEvent(
            "8453ea66-e3fe-4f35-b6c2-a6aa87482661",
            true,
            "AA000000A",
            calculationInputs,
            calculationResponse
          )

        await(service.auditCalculationRequest(calculationSubmissionAuditEvent)(hc)) mustBe ()
      }
    }

    "auditCalculationStart" - {
      "should call the audit connector with the CalculationStartAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val calculationStartAuditEvent =
          CalculationStartAuditEvent(
            "8453ea66-e3fe-4f35-b6c2-a6aa87482661",
            true
          )

        await(service.auditCalculationStart(calculationStartAuditEvent)(hc)) mustBe ()
      }
    }

    "auditCalculationTaskList" - {
      "should call the audit connector with the CalculationTaskListAuditEvent event" in {

        implicit val hc = HeaderCarrier()

        val calculationTaskListAuditEvent =
          CalculationTaskListAuditEvent(
            true,
            "8453ea66-e3fe-4f35-b6c2-a6aa87482661",
            "AA000000A",
            List(
              SectionStatus("setupQuestions", "Completed"),
              SectionStatus("annualAllowanceSetupQuestions", "Completed"),
              SectionStatus("annualAllowanceDetails2016", "NotStarted"),
              SectionStatus("annualAllowanceDetails2017", "NotStarted"),
              SectionStatus("annualAllowanceDetails2018", "NotStarted"),
              SectionStatus("annualAllowanceDetails2019", "NotStarted"),
              SectionStatus("annualAllowanceDetails2020", "NotStarted"),
              SectionStatus("annualAllowanceDetails2021", "NotStarted"),
              SectionStatus("annualAllowanceDetails2022", "NotStarted"),
              SectionStatus("annualAllowanceDetails2023", "NotStarted"),
              SectionStatus("nextStepsAction", "CannotStartYet")
            )
          )

        await(service.auditCalculationTaskList(calculationTaskListAuditEvent)(hc)) mustBe ()
      }
    }

  }

}
