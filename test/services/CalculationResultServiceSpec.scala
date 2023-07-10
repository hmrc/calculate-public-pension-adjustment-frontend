/*
 * Copyright 2023 HM Revenue & Customs
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
import connectors.{BackendConnector, CalculationResultConnector}
import models.CalculationResults.{CalculationResponse, CalculationResultsViewModel, RowViewModel}
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023._
import models.submission.Success
import models.{AnnualAllowance, CalculationUserAnswers, Period, Resubmission, TaxYear2013To2015, TaxYearScheme, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import play.api.libs.json.{JsObject, JsValue, Json}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

class CalculationResultServiceSpec extends SpecBase with MockitoSugar {

  private val mockCalculationResultConnector = mock[CalculationResultConnector]
  private val mockBackendConnector           = mock[BackendConnector]
  private val mockAuditService               = mock[AuditService]

  private val service =
    new CalculationResultService(mockCalculationResultConnector, mockBackendConnector, mockAuditService)

  private def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }

  "CalculationResultService" - {

    val data1 = Json
      .parse(s"""
           |{
           |    "savingsStatement" : true,
           |    "resubmittingAdjustment" : true,
           |    "reasonForResubmission" : "Change in amounts",
           |    "reportingChange" : [ "annualAllowance" ],
           |    "scottishTaxpayerFrom2016" : false,
           |    "payingPublicPensionScheme" : true,
           |    "definedContributionPensionScheme" : true,
           |    "flexiblyAccessedPension" : true,
           |    "flexibleAccessStartDate" : "2015-05-25",
           |    "payTaxCharge1516" : false,
           |    "2013" : {
           |      "pIAPreRemedy" : 40000
           |    },
           |    "2014" : {
           |      "pIAPreRemedy" : 20000
           |    },
           |    "2015" : {
           |      "pIAPreRemedy" : 60000
           |    },
           |    "aa" : {
           |      "years" : {
           |        "2016-pre" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 35000,
           |                "revisedPIA" : 30000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
           |          "definedContributionAmount" : 6000,
           |          "flexiAccessDefinedContributionAmount" : 10000,
           |          "definedBenefitAmount" : 30000
           |        },
           |        "2016-post" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "you",
           |              "howMuchAAChargeYouPaid" : 2000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 40000,
           |          "totalIncome" : 60000
           |        },
           |        "2017" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 40000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2018" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "both",
           |              "howMuchAAChargeYouPaid" : 1000,
           |              "howMuchAAChargeSchemePaid" : 1000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : false,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2019" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 37000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2020" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 36000,
           |                "revisedPIA" : 34000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 34000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2021" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 38000,
           |                "revisedPIA" : 36000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : false,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2022" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 44000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 44000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2023" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 55000,
           |                "revisedPIA" : 53000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "scheme",
           |              "howMuchAAChargeSchemePaid" : 4400
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 53000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        }
           |      }
           |    }
           |  }
           |""".stripMargin)
      .as[JsObject]

    val data2 = Json
      .parse(s"""
           |{
           |    "savingsStatement" : true,
           |    "resubmittingAdjustment" : true,
           |    "reasonForResubmission" : "Change in amounts",
           |    "reportingChange" : [ "annualAllowance" ],
           |    "scottishTaxpayerFrom2016" : false,
           |    "payingPublicPensionScheme" : true,
           |    "definedContributionPensionScheme" : true,
           |    "flexiblyAccessedPension" : true,
           |    "flexibleAccessStartDate" : "2015-05-25",
           |    "payTaxCharge1516" : false,
           |    "aa" : {
           |      "years" : {
           |        "2016-pre" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 35000,
           |                "revisedPIA" : 30000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
           |          "definedContributionAmount" : 6000,
           |          "flexiAccessDefinedContributionAmount" : 10000,
           |          "definedBenefitAmount" : 30000
           |        },
           |        "2016-post" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "you",
           |              "howMuchAAChargeYouPaid" : 2000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 40000,
           |          "totalIncome" : 60000
           |        },
           |        "2017" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 40000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2018" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "both",
           |              "howMuchAAChargeYouPaid" : 1000,
           |              "howMuchAAChargeSchemePaid" : 1000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : false,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2019" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 37000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2020" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 36000,
           |                "revisedPIA" : 34000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 34000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2021" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 38000,
           |                "revisedPIA" : 36000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : false,
           |          "thresholdIncome" : true,
           |          "adjustedIncome" : 160000,
           |          "totalIncome" : 60000
           |        },
           |        "2022" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 44000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 44000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2023" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 55000,
           |                "revisedPIA" : 53000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "scheme",
           |              "howMuchAAChargeSchemePaid" : 4400
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 53000,
           |          "thresholdIncome" : true,
           |          "adjustedIncome" : 120000,
           |          "totalIncome" : 60000
           |        }
           |      }
           |    }
           |  }
           |""".stripMargin)
      .as[JsObject]

    val data3 = Json
      .parse(s"""
           |{
           |    "savingsStatement" : true,
           |    "resubmittingAdjustment" : false,
           |    "reportingChange" : [ "annualAllowance" ],
           |    "scottishTaxpayerFrom2016" : false,
           |    "payingPublicPensionScheme" : true,
           |    "definedContributionPensionScheme" : true,
           |    "flexiblyAccessedPension" : true,
           |    "flexibleAccessStartDate" : "2015-05-25",
           |    "payTaxCharge1516" : false,
           |    "2013" : {
           |      "pIAPreRemedy" : 40000
           |    },
           |    "2014" : {
           |      "pIAPreRemedy" : 20000
           |    },
           |    "2015" : {
           |      "pIAPreRemedy" : 60000
           |    },
           |    "aa" : {
           |      "years" : {
           |        "2016-pre" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 35000,
           |                "revisedPIA" : 30000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
           |          "definedContributionAmount" : 6000,
           |          "flexiAccessDefinedContributionAmount" : 10000,
           |          "definedBenefitAmount" : 30000
           |        },
           |        "2016-post" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "you",
           |              "howMuchAAChargeYouPaid" : 2000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 40000,
           |          "totalIncome" : 60000
           |        },
           |        "2017" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 40000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2018" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 45000,
           |                "revisedPIA" : 40000
           |              },
           |              "payACharge" : true,
           |              "whoPaidAACharge" : "both",
           |              "howMuchAAChargeYouPaid" : 1000,
           |              "howMuchAAChargeSchemePaid" : 1000
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : false,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        },
           |        "2019" : {
           |          "memberMoreThanOnePension" : false,
           |          "schemes" : {
           |            "0" : {
           |              "pensionSchemeDetails" : {
           |                "schemeName" : "Scheme 1",
           |                "schemeTaxRef" : "00348916RT"
           |              },
           |              "whichScheme" : "00348916RT",
           |              "pensionSchemeInputAmounts" : {
           |                "originalPIA" : 37000,
           |                "revisedPIA" : 35000
           |              },
           |              "payACharge" : false
           |            }
           |          },
           |          "otherDefinedBenefitOrContribution" : true,
           |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
           |          "definedBenefitAmount" : 35000,
           |          "thresholdIncome" : false,
           |          "totalIncome" : 60000
           |        }
           |      }
           |    }
           |  }
           |""".stripMargin)
      .as[JsObject]

    val userAnswers1 = UserAnswers(
      id = "session-5a356f3a-c83e-4e8c-a957-226163ba285f",
      data = data1
    )

    "toTaxYear2013To2015" - {

      "should return valid TaxYear2013To2015 for a Period 2013" in {
        val result = service.toTaxYear2013To2015(userAnswers1, Period._2013)

        result mustBe Some(TaxYear2013To2015(40000, Period._2013))
      }

      "should return valid TaxYear2013To2015 for a Period 2014" in {
        val result = service.toTaxYear2013To2015(userAnswers1, Period._2014)

        result mustBe Some(TaxYear2013To2015(20000, Period._2014))
      }

      "should return valid TaxYear2013To2015 for a Period 2015" in {
        val result = service.toTaxYear2013To2015(userAnswers1, Period._2015)

        result mustBe Some(TaxYear2013To2015(60000, Period._2015))
      }

      "should return None for a missing Period 2015" in {
        val result = service.toTaxYear2013To2015(userAnswers1.copy(data = data2), Period._2015)

        result mustBe None
      }

    }

    "toTaxYear2016To2023" - {

      "should return valid TaxYear2016To2023.InitialFlexiblyAccessedTaxYear for a Period 2016PreAlignment" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2016PreAlignment)

        result mustBe Some(
          InitialFlexiblyAccessedTaxYear(
            30000,
            LocalDate.parse("2015-05-25"),
            6000,
            10000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 30000, 0)),
            60000,
            0,
            Period._2016PreAlignment
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2016PostAlignment" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2016PostAlignment)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            40000,
            0,
            60000,
            2000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 0)),
            Period._2016PostAlignment
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2017" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2017)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            35000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 35000, 0)),
            Period._2017,
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.NormalTaxYear for a Period 2018" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2018)

        result mustBe Some(
          NormalTaxYear(
            40000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 1000)),
            60000,
            1000,
            Period._2018,
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2019" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2019)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            35000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 37000, 35000, 0)),
            Period._2019,
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2020" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2020)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            34000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 36000, 34000, 0)),
            Period._2020,
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.NormalTaxYear for a Period 2021" in {
        val result = service.toTaxYear2016To2023(userAnswers1.copy(data = data2), Period._2021)

        result mustBe Some(
          NormalTaxYear(
            36000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 36000, 0)),
            60000,
            0,
            Period._2021,
            Some(AboveThreshold(160000))
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2022" in {
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2022)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            44000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 44000, 0)),
            Period._2022,
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2023" in {
        val result = service.toTaxYear2016To2023(userAnswers1.copy(data = data2), Period._2023)

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            53000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 55000, 53000, 4400)),
            Period._2023,
            Some(AboveThreshold(120000))
          )
        )
      }

    }

    "buildCalculationUserAnswers" - {

      "should return valid CalculationUserAnswers for a valid UserAnswers with all years" in {

        val result = service.buildCalculationUserAnswers(userAnswers1)

        result mustBe CalculationUserAnswers(
          Resubmission(true, Some("Change in amounts")),
          Some(
            AnnualAllowance(
              List.empty,
              List(
                TaxYear2013To2015(40000, Period._2013),
                TaxYear2013To2015(20000, Period._2014),
                TaxYear2013To2015(60000, Period._2015),
                InitialFlexiblyAccessedTaxYear(
                  30000,
                  LocalDate.parse("2015-05-25"),
                  6000,
                  10000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 30000, 0)),
                  60000,
                  0,
                  Period._2016PreAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  40000,
                  0,
                  60000,
                  2000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 0)),
                  Period._2016PostAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 35000, 0)),
                  Period._2017,
                  Some(BelowThreshold)
                ),
                NormalTaxYear(
                  40000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 1000)),
                  60000,
                  1000,
                  Period._2018,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 37000, 35000, 0)),
                  Period._2019,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  34000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 36000, 34000, 0)),
                  Period._2020,
                  Some(BelowThreshold)
                ),
                NormalTaxYear(
                  36000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 36000, 0)),
                  60000,
                  0,
                  Period._2021,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  44000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 44000, 0)),
                  Period._2022,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  53000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 55000, 53000, 4400)),
                  Period._2023,
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )
      }

      "should return valid CalculationUserAnswers for a valid UserAnswers with missing years" in {

        val result = service.buildCalculationUserAnswers(userAnswers1.copy(data = data3))

        result mustBe CalculationUserAnswers(
          Resubmission(false, None),
          Some(
            AnnualAllowance(
              List.empty,
              List(
                TaxYear2013To2015(40000, Period._2013),
                TaxYear2013To2015(20000, Period._2014),
                TaxYear2013To2015(60000, Period._2015),
                InitialFlexiblyAccessedTaxYear(
                  30000,
                  LocalDate.parse("2015-05-25"),
                  6000,
                  10000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 30000, 0)),
                  60000,
                  0,
                  Period._2016PreAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  40000,
                  0,
                  60000,
                  2000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 0)),
                  Period._2016PostAlignment
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 35000, 0)),
                  Period._2017,
                  Some(BelowThreshold)
                ),
                NormalTaxYear(
                  40000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 40000, 1000)),
                  60000,
                  1000,
                  Period._2018,
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 37000, 35000, 0)),
                  Period._2019,
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )
      }

    }

    "resubmission details should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val rows: Seq[RowViewModel] = viewModel.resubmissionVal

      rows.size mustBe 2
      checkRowNameAndValue(rows, 0, "calculationResults.annualResults.isResubmission", "")
      checkRowNameAndValue(rows, 1, "calculationResults.annualResults.reason", "Change in amounts")

      viewModel.resubmissionData mustBe List(
        List(
          RowViewModel("calculationResults.annualResults.isResubmission", ""),
          RowViewModel("calculationResults.annualResults.reason", "Change in amounts")
        )
      )
    }

    "total amounts should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val rows: Seq[RowViewModel] = viewModel.totalAmounts

      rows.size mustBe 3
      checkRowNameAndValue(rows, 0, "calculationResults.outDatesCompensation", "8400")
      checkRowNameAndValue(rows, 1, "calculationResults.inDatesDebit", "0")
      checkRowNameAndValue(rows, 2, "calculationResults.inDatesCredit", "0")

      viewModel.calculationData mustBe List(
        List(
          RowViewModel("calculationResults.outDatesCompensation", "8400"),
          RowViewModel("calculationResults.inDatesDebit", "0"),
          RowViewModel("calculationResults.inDatesCredit", "0")
        )
      )
    }

    "out dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.outDates
      sections.size mustBe 5

      val year = sections(0)

      checkRowNameAndValue(year, 0, "periodDateRangeAA.2016-pre", "2016-pre")
      checkRowNameAndValue(year, 1, "calculationResults.annualResults.chargePaidBySchemes", "0")
      checkRowNameAndValue(year, 2, "calculationResults.annualResults.chargePaidByMember", "0")
      checkRowNameAndValue(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0")
      checkRowNameAndValue(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0")
      checkRowNameAndValue(year, 5, "calculationResults.annualResults.directCompensation", "0")
      checkRowNameAndValue(year, 6, "calculationResults.annualResults.indirectCompensation", "0")
      checkRowNameAndValue(year, 7, "calculationResults.annualResults.unusedAnnualAllowance", "60000")

      viewModel.annualResultsData mustBe List(
        List(
          RowViewModel("periodDateRangeAA.2016-pre", "2016-pre"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "0"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "60000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2016-post", "2016-post"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "7200"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "7200"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "10000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2017", "2017"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "1200"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "1200"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "0")
        ),
        List(
          RowViewModel("periodDateRangeAA.2018", "2018"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "0"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "10000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2019", "2019"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "0"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "30000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2020", "2020"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.memberCredit", "0"),
          RowViewModel("calculationResults.annualResults.schemeCredit", "0"),
          RowViewModel("calculationResults.annualResults.debit", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "48000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2021", "2021"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.memberCredit", "0"),
          RowViewModel("calculationResults.annualResults.schemeCredit", "0"),
          RowViewModel("calculationResults.annualResults.debit", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "56000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2022", "2022"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.memberCredit", "0"),
          RowViewModel("calculationResults.annualResults.schemeCredit", "0"),
          RowViewModel("calculationResults.annualResults.debit", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "54000")
        ),
        List(
          RowViewModel("periodDateRangeAA.2023", "2023"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.memberCredit", "0"),
          RowViewModel("calculationResults.annualResults.schemeCredit", "0"),
          RowViewModel("calculationResults.annualResults.debit", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "54000")
        )
      )

    }
    "all years in out dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.outDates

      sections.foreach(year => checkYearOutDates(year))
    }

    "in dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.inDates
      sections.size mustBe 4

      val year = sections(0)

      checkRowNameAndValue(year, 0, "periodDateRangeAA.2020", "2020")
      checkRowNameAndValue(year, 1, "calculationResults.annualResults.chargePaidBySchemes", "0")
      checkRowNameAndValue(year, 2, "calculationResults.annualResults.chargePaidByMember", "0")
      checkRowNameAndValue(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0")
      checkRowNameAndValue(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0")
      checkRowNameAndValue(year, 5, "calculationResults.annualResults.memberCredit", "0")
      checkRowNameAndValue(year, 6, "calculationResults.annualResults.schemeCredit", "0")
      checkRowNameAndValue(year, 7, "calculationResults.annualResults.debit", "0")
      checkRowNameAndValue(year, 8, "calculationResults.annualResults.unusedAnnualAllowance", "48000")

    }

    "all years in in-dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        service.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.inDates

      sections.foreach(year => checkYearInDates(year))
    }

    def checkYearInDates(year: Seq[RowViewModel]) = {
      year.size mustBe 9

      year(0).value mustNot be(null)
      checkRowName(year, 1, "calculationResults.annualResults.chargePaidBySchemes")
      checkRowName(year, 2, "calculationResults.annualResults.chargePaidByMember")
      checkRowName(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate")
      checkRowName(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate")
      checkRowName(year, 5, "calculationResults.annualResults.memberCredit")
      checkRowName(year, 6, "calculationResults.annualResults.schemeCredit")
      checkRowName(year, 7, "calculationResults.annualResults.debit")
      checkRowName(year, 8, "calculationResults.annualResults.unusedAnnualAllowance")
    }

    def checkYearOutDates(year: Seq[RowViewModel]) = {
      year.size mustBe 8

      year(0).value mustNot be(null)
      checkRowName(year, 1, "calculationResults.annualResults.chargePaidBySchemes")
      checkRowName(year, 2, "calculationResults.annualResults.chargePaidByMember")
      checkRowName(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate")
      checkRowName(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate")
      checkRowName(year, 5, "calculationResults.annualResults.directCompensation")
      checkRowName(year, 6, "calculationResults.annualResults.indirectCompensation")
      checkRowName(year, 7, "calculationResults.annualResults.unusedAnnualAllowance")
    }

    def checkRowNameAndValue(rows: Seq[RowViewModel], index: Int, expectedName: String, expectedValue: String): Unit = {
      rows(index).name mustBe expectedName
      rows(index).value mustBe expectedValue
    }

    def checkRowName(rows: Seq[RowViewModel], index: Int, expectedName: String): Unit = {
      rows(index).name mustBe expectedName
      rows(index).value mustNot be(null)
    }

  }

  "submitting user answers and calculation to backend" - {

    "when a valid calculation result is sent successfully a unique id should be returned" in {

      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      when(mockCalculationResultConnector.sendRequest(any)).thenReturn(Future.successful(calculationResult))
      when(mockBackendConnector.sendSubmissionRequest(any)).thenReturn(Future.successful(Success("uniqueId")))

      val submissionResponse = service.submitUserAnswersAndCalculation(emptyUserAnswers)
      submissionResponse.futureValue.asInstanceOf[Success].uniqueId mustBe "uniqueId"
    }

    "must fail when a valid calculation result cannot be obtained" in {

      when(mockCalculationResultConnector.sendRequest(any))
        .thenReturn(Future.failed(new RuntimeException("someError")))
      when(mockBackendConnector.sendSubmissionRequest(any)).thenReturn(Future.successful(Success("uniqueId")))

      val result = service.submitUserAnswersAndCalculation(emptyUserAnswers)
      an[RuntimeException] mustBe thrownBy(result.futureValue)
    }

    "must fail when a calculation result cannot sent" in {

      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      when(mockCalculationResultConnector.sendRequest(any)).thenReturn(Future.successful(calculationResult))
      when(mockBackendConnector.sendSubmissionRequest(any))
        .thenReturn(Future.failed(new RuntimeException("someError")))

      val result = service.submitUserAnswersAndCalculation(emptyUserAnswers)
      an[RuntimeException] mustBe thrownBy(result.futureValue)
    }
  }
}
