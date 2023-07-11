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
import connectors.CalculationResultConnector
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023._
import models.{AnnualAllowance, CalculationUserAnswers, Period, Resubmission, TaxYear2013To2015, TaxYearScheme, UserAnswers}
import org.mockito.MockitoSugar
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global

class CalculationResultServiceTest extends SpecBase with MockitoSugar {

  private val mockCalculationResultConnector = mock[CalculationResultConnector]

  private val service = new CalculationResultService(mockCalculationResultConnector)

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

  }

}
