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
import connectors.{CalculationResultConnector, ReducedNetIncomeConnector, SubmissionsConnector}
import controllers.annualallowance.taxyear.AboveThresholdController
import models.CalculationResults.{AnnualAllowanceSetup, CalculationResponse, CalculationResultsViewModel, CalculationReviewIndividualAAViewModel, CalculationReviewViewModel, IndividualAASummaryModel, LifetimeAllowanceSetup, Resubmission, ReviewRowViewModel, RowViewModel, Setup}
import models.Income.{AboveThreshold, BelowThreshold}
import models.TaxYear2016To2023._
import models.submission.Success
import models.tasklist.sections.LTASection
import models.{AnnualAllowance, CalculationResults, ExcessLifetimeAllowancePaid, IncomeSubJourney, LifeTimeAllowance, LtaProtectionOrEnhancements, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, NewLifeTimeAllowanceAdditions, PensionSchemeInputAmounts, Period, ProtectionEnhancedChanged, ProtectionType, ReducedNetIncomeResponse, SchemeIndex, SchemeNameAndTaxRef, TaxYear2011To2015, TaxYear2016To2023, TaxYearScheme, ThresholdIncome, UserAnswers, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import pages.annualallowance.taxyear.{AmountClaimedOnOverseasPensionPage, DefinedBenefitAmountPage, DefinedContributionAmountPage, FlexiAccessDefinedContributionAmountPage, HowMuchContributionPensionSchemePage, HowMuchTaxReliefPensionPage, KnowAdjustedAmountPage, LumpSumDeathBenefitsValuePage, PensionSchemeInputAmountsPage, RASContributionAmountPage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage}
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDate
import scala.io.Source

class CalculationResultServiceSpec extends SpecBase with MockitoSugar {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private val mockCalculationResultConnector = mock[CalculationResultConnector]
  private val mockSubmissionsConnector       = mock[SubmissionsConnector]
  private val mockAuditService               = mock[AuditService]
  private val mockReducedNetIncomeConnector  = mock[ReducedNetIncomeConnector]
  private val aboveThresholdController       = new AboveThresholdController
  private val service                        =
    new CalculationResultService(
      mockCalculationResultConnector,
      mockSubmissionsConnector,
      mockReducedNetIncomeConnector,
      mockAuditService,
      aboveThresholdController
    )

  private def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }

  "CalculationResultService" - {

    val data1 = Json
      .parse(s"""
                |{
                |    "resubmittingAdjustment" : true,
                |    "reasonForResubmission" : "Change in amounts",
                |    "reportingChange" : [ "annualAllowance", "lifetimeAllowance" ],
                |    "kickoutStatus": {
                |      "annualAllowance": 2,
                |      "lifetimeAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true,
                |        "pensionProtectedMember": false,
                |        "hadAACharge": false,
                |        "contributionRefunds": false,
                |        "netIncomeAbove100K": false,
                |        "netIncomeAbove190K": false,
                |        "maybePIAIncrease": "no",
                |        "maybePIAUnchangedOrDecreased": "no",
                |        "pIAAboveAnnualAllowanceIn2023": false,
                |        "netIncomeAbove190KIn2023": false,
                |        "flexibleAccessDcScheme": false,
                |        "contribution4000ToDirectContributionScheme": false
                |      },
                |      "lta": {
                |        "hadBenefitCrystallisationEvent": true,
                |        "previousLTACharge": false,
                |        "changeInLifetimeAllowance": true,
                |        "increaseInLTACharge": false,
                |        "newLTACharge": false,
                |        "multipleBenefitCrystallisationEvent": false,
                |        "otherSchemeNotification": false
                |      }
                |    },
                |    "scottishTaxpayerFrom2016" : false,
                |    "payingPublicPensionScheme" : true,
                |    "definedContributionPensionScheme" : true,
                |    "flexiblyAccessedPension" : true,
                |    "flexibleAccessStartDate" : "2015-05-25",
                |    "payTaxCharge1415" : false,
                |     "2011" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 10000
                |    },
                |    "2012" : {
                |      "registeredYear" : false
                |    },
                |    "2013" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 40000
                |    },
                |    "2014" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 20000
                |    },
                |    "2015" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 60000
                |    },
                |    "aa" : {
                |      "years" : {
                |        "2016" : {
                |          "memberMoreThanOnePension" : false,
                |          "schemes" : {
                |            "0" : {
                |              "pensionSchemeDetails" : {
                |                "schemeName" : "Scheme 1",
                |                "schemeTaxRef" : "00348916RT"
                |              },
                |              "PensionSchemeInput2016preAmounts" : {
                |                "revisedPIA" : 30000
                |              },
                |              "PensionSchemeInput2016postAmounts" : {
                |                "revisedPIA" : 30000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
                |          "definedContribution2016PreAmount" : 6015,
                |          "definedContribution2016PostAmount" : 6016,
                |          "definedContribution2016PreFlexiAmount" : 10015,
                |          "definedBenefit2016PreAmount" : 30015,
                |          "definedBenefit2016PostAmount" : 30016,
                |          "totalIncome" : 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 40000
                |              },
                |              "payACharge" : true,
                |              "whoPaidAACharge" : "both",
                |              "howMuchAAChargeYouPaid" : 1000,
                |              "howMuchAAChargeSchemePaid" : 1000
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : false,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 34000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 34000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 36000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : false,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 44000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 44000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    },
                |    "lta": {
                |      "dateOfBenefitCrystallisationEvent": "2018-11-28",
                |      "increaseInLTACharge": true,
                |      "newLTACharge": true,
                |      "ltaProtectionOrEnhancements": "protection",
                |      "protectionType": "fixedProtection2014",
                |      "protectionReference": "R41AB678TR23355",
                |      "protectionTypeEnhancementChanged": true,
                |      "whatNewProtectionTypeEnhancement": "individualProtection2016",
                |      "referenceNewProtectionTypeEnhancement": "2134567801",
                |      "lifetimeAllowanceCharge": true,
                |      "excessLifetimeAllowancePaid": "annualPayment",
                |      "whoPaidLTACharge": "pensionScheme",
                |      "schemeNameAndTaxRef": {
                |        "name": "Scheme 1",
                |        "taxRef": "00348916RT"
                |      },
                |      "valueNewLtaCharge": 30000,
                |      "whoPayingExtraLtaCharge": "you"
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
                |    "payTaxCharge1415" : false,
                |    "aa" : {
                |      "years" : {
                |         "2016" : {
                |          "memberMoreThanOnePension" : false,
                |          "schemes" : {
                |            "0" : {
                |              "pensionSchemeDetails" : {
                |                "schemeName" : "Scheme 1",
                |                "schemeTaxRef" : "00348916RT"
                |              },
                |              "PensionSchemeInput2016preAmounts" : {
                |                "revisedPIA" : 30000
                |              },
                |              "PensionSchemeInput2016postAmounts" : {
                |                "revisedPIA" : 40000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
                |          "definedContribution2016PreAmount" : 6000,
                |          "definedContribution2016PreFlexiAmount" : 10000,
                |          "definedBenefit2016PreAmount" : 30000,
                |          "definedBenefit2016PostAmount" : 40000,
                |          "totalIncome" : 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "payeCodeAdjustment": "increase",
                |          "codeAdjustmentAmount": 2740,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 40000
                |              },
                |              "payACharge" : true,
                |              "whoPaidAACharge" : "both",
                |              "howMuchAAChargeYouPaid" : 1000,
                |              "howMuchAAChargeSchemePaid" : 1000
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : false,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "payeCodeAdjustment": "increase",
                |          "codeAdjustmentAmount": 2740,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "payeCodeAdjustment": "increase",
                |          "codeAdjustmentAmount": 2740,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 34000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 34000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "payeCodeAdjustment": "increase",
                |          "codeAdjustmentAmount": 2740,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 36000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : false,
                |          "thresholdIncome" : "yes",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 44000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 44000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "payeCodeAdjustment": "increase",
                |          "codeAdjustmentAmount": 2740,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |          "thresholdIncome" : "idk",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |    "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true
                |      }
                |    },
                |    "scottishTaxpayerFrom2016" : false,
                |    "payingPublicPensionScheme" : true,
                |    "definedContributionPensionScheme" : true,
                |    "flexiblyAccessedPension" : true,
                |    "flexibleAccessStartDate" : "2015-05-25",
                |    "payTaxCharge1415" : false,
                |    "2011" : {
                |      "registeredYear" : false
                |    },
                |    "2012" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 10000
                |    },
                |    "2013" : {
                |      "registeredYear" : true,
                |      "pIAPreRemedy" : 40000
                |    },
                |    "2014" : {
                |      "registeredYear" : false
                |    },
                |    "2015" : {
                |      "registeredYear" : false
                |    },
                |    "aa" : {
                |      "years" : {
                |         "2016" : {
                |          "memberMoreThanOnePension" : false,
                |          "schemes" : {
                |            "0" : {
                |              "pensionSchemeDetails" : {
                |                "schemeName" : "Scheme 1",
                |                "schemeTaxRef" : "00348916RT"
                |              },
                |              "PensionSchemeInput2016preAmounts" : {
                |                "revisedPIA" : 30000
                |              },
                |              "PensionSchemeInput2016postAmounts" : {
                |                "revisedPIA" : 40000
                |              },
                |              "payACharge" : true,
                |              "whoPaidAACharge" : "you",
                |              "howMuchAAChargeYouPaid" : 2000
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedContribution", "definedBenefit" ],
                |          "definedContribution2016PreAmount" : 6000,
                |          "definedContribution2016PreFlexiAmount" : 10000,
                |          "definedBenefit2016PreAmount" : 30000,
                |          "definedBenefit2016PostAmount" : 40000,
                |          "totalIncome" : 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 40000
                |              },
                |              "payACharge" : true,
                |              "whoPaidAACharge" : "both",
                |              "howMuchAAChargeYouPaid" : 1000,
                |              "howMuchAAChargeSchemePaid" : 1000
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : false,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
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
                |                "revisedPIA" : 35000
                |              },
                |              "payACharge" : false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution" : true,
                |          "contributedToDuringRemedyPeriod" : [ "definedBenefit" ],
                |          "definedBenefitAmount" : 35000,
                |          "thresholdIncome" : "no",
                |          "totalIncome" : 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    },
                |    "lta": {
                |      "hadBenefitCrystallisationEvent": true,
                |      "dateOfBenefitCrystallisationEvent": "2018-11-20",
                |      "changeInLifetimeAllowance": true,
                |      "increaseInLTACharge": false
                |    }
                |  }
                |""".stripMargin)
      .as[JsObject]

    val data4 = Json
      .parse("""
               |{
               |    "savingsStatement": true,
               |    "resubmittingAdjustment": false,
               |    "reportingChange": [
               |      "lifetimeAllowance"
               |    ],
               |    "lta": {
               |      "hadBenefitCrystallisationEvent": true,
               |      "dateOfBenefitCrystallisationEvent": "2018-11-28",
               |      "changeInLifetimeAllowance": true,
               |      "increaseInLTACharge": true,
               |      "newLTACharge": true,
               |      "ltaProtectionOrEnhancements": "protection",
               |      "protectionType": "fixedProtection2014",
               |      "protectionReference": "R41AB678TR23355",
               |      "protectionTypeEnhancementChanged": true,
               |      "whatNewProtectionTypeEnhancement": "individualProtection2016",
               |      "referenceNewProtectionTypeEnhancement": "2134567801",
               |      "lifetimeAllowanceCharge": true,
               |      "excessLifetimeAllowancePaid": "annualPayment",
               |      "whoPaidLTACharge": "pensionScheme",
               |      "schemeNameAndTaxRef": {
               |        "name": "Scheme 1",
               |        "taxRef": "00348916RT"
               |      },
               |      "valueNewLtaCharge": 30000,
               |      "whoPayingExtraLtaCharge": "you"
               |    }
               |  }
               |""".stripMargin)
      .as[JsObject]

    val data5 = Json
      .parse("""
               {
               |    "savingsStatement": true,
               |    "resubmittingAdjustment": false,
               |    "reportingChange": [
               |      "lifetimeAllowance"
               |    ],
               |    "lta": {
               |      "hadBenefitCrystallisationEvent": true,
               |      "dateOfBenefitCrystallisationEvent": "2018-11-20",
               |      "changeInLifetimeAllowance": true,
               |      "increaseInLTACharge": false
               |    }
               |  }
               |""".stripMargin)
      .as[JsObject]

    val data6 = Json
      .parse("""
               {
               |    "savingsStatement": true,
               |    "resubmittingAdjustment": false,
               |    "reportingChange": [
               |      "lifetimeAllowance"
               |    ],
               |    "lta": {
               |      "hadBenefitCrystallisationEvent": true,
               |      "dateOfBenefitCrystallisationEvent": "2018-11-20",
               |      "changeInLifetimeAllowance": false
               |    }
               |  }
               |""".stripMargin)
      .as[JsObject]

    val data7 = Json
      .parse("""
               {
               |    "savingsStatement": true,
               |    "resubmittingAdjustment": false,
               |    "reportingChange": [
               |      "lifetimeAllowance"
               |    ],
               |    "lta": {
               |      "hadBenefitCrystallisationEvent": false
               |    }
               |  }
               |""".stripMargin)
      .as[JsObject]

    val data8 = Json
      .parse("""
               |{
               |    "savingsStatement": true,
               |    "resubmittingAdjustment": false,
               |    "reportingChange": [
               |      "lifetimeAllowance"
               |    ],
               |    "lta": {
               |      "hadBenefitCrystallisationEvent": true,
               |      "dateOfBenefitCrystallisationEvent": "2018-11-28",
               |      "changeInLifetimeAllowance": true,
               |      "increaseInLTACharge": true,
               |      "newLTACharge": true,
               |      "ltaProtectionOrEnhancements": "protection",
               |      "protectionType": "fixedProtection2014",
               |      "protectionReference": "R41AB678TR23355",
               |      "protectionTypeEnhancementChanged": true,
               |      "whatNewProtectionTypeEnhancement": "individualProtection2016",
               |      "referenceNewProtectionTypeEnhancement": "2134567801",
               |      "lifetimeAllowanceCharge": false,
               |      "newExcessLifetimeAllowancePaid": "annualPayment",
               |      "newAnnualPaymentValue": 0
               |    }
               |  }
               |""".stripMargin)
      .as[JsObject]

    val data9 = Json
      .parse(s"""
                |{
                |    "savingsStatement": true,
                |    "navigation": {
                |      "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |      "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |      "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers",
                |      "aaSection2017": "/public-pension-adjustment/annual-allowance/2017/check-answers",
                |      "aaSection2018": "/public-pension-adjustment/annual-allowance/2018/check-answers",
                |      "aaSection2019": "/public-pension-adjustment/annual-allowance/2019/check-answers"
                |    },
                |    "resubmittingAdjustment": false,
                |    "reportingChange": [
                |      "annualAllowance"
                |    ],
                |    "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true
                |      }
                |    },
                |    "scottishTaxpayerFrom2016": true,
                |    "whichYearsScottishTaxpayer": [
                |      "2019"
                |    ],
                |    "payingPublicPensionScheme": false,
                |    "stopPayingPublicPension": "2019-04-05",
                |    "definedContributionPensionScheme": true,
                |    "flexiblyAccessedPension": false,
                |    "payTaxCharge1415": true,
                |    "aa": {
                |      "years": {
                |        "2016": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RO"
                |              },
                |              "whichScheme": "00348916RO",
                |              "PensionSchemeInput2016postAmounts": {
                |                "revisedPIA": 22000
                |              },
                |              "PensionSchemeInput2016preAmounts": {
                |                "revisedPIA": 20000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "both",
                |              "howMuchAAChargeYouPaid": 3600,
                |              "howMuchAAChargeSchemePaid": 3600
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution"
                |          ],
                |          "definedContribution2016PostAmount": 18000,
                |          "definedContribution2016PreAmount": 20000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2017": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RO"
                |              },
                |              "whichScheme": "00348916RO",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 45000
                |              },
                |              "payACharge": false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": false,
                |          "thresholdIncome": "no",
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2018": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RO"
                |              },
                |              "whichScheme": "00348916RO",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 38000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "both",
                |              "howMuchAAChargeYouPaid": 200,
                |              "howMuchAAChargeSchemePaid": 200
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": false,
                |          "thresholdIncome": "no",
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2019": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RO"
                |              },
                |              "whichScheme": "00348916RO",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 43000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "you",
                |              "howMuchAAChargeYouPaid": 3280
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": false,
                |          "thresholdIncome": "no",
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    }
                |  }
                |""".stripMargin)
      .as[JsObject]

    val data10 = Json
      .parse(s"""
                |{
                |    "savingsStatement": true,
                |    "navigation": {
                |      "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |      "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |      "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers",
                |      "aaSection2017": "/public-pension-adjustment/annual-allowance/2017/check-answers",
                |      "aaSection2018": "/public-pension-adjustment/annual-allowance/2018/check-answers",
                |      "aaSection2019": "/public-pension-adjustment/annual-allowance/2019/check-answers",
                |      "aaSection2020": "/public-pension-adjustment/annual-allowance/2020/check-answers"
                |    },
                |    "resubmittingAdjustment": false,
                |    "reportingChange": [
                |      "annualAllowance"
                |    ],
                |    "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true
                |      }
                |    },
                |    "scottishTaxpayerFrom2016": false,
                |    "payingPublicPensionScheme": false,
                |    "definedContributionPensionScheme": true,
                |    "flexiblyAccessedPension": true,
                |    "flexibleAccessStartDate": "2017-10-20",
                |    "payTaxCharge1415": true,
                |    "stopPayingPublicPension": "2020-01-01",
                |    "aa": {
                |      "years": {
                |        "2016": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RP"
                |              },
                |              "PensionSchemeInput2016preAmounts": {
                |                "revisedPIA": 28000
                |              },
                |              "PensionSchemeInput2016postAmounts": {
                |                "revisedPIA": 34000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "you",
                |              "howMuchAAChargeYouPaid": 300
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution",
                |            "definedBenefit"
                |          ],
                |          "definedContribution2016PreAmount": 1800,
                |          "definedBenefit2016PreAmount": 800,
                |          "totalIncome": 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2017": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RP"
                |              },
                |              "whichScheme": "00348916RP",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 31000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "scheme",
                |              "howMuchAAChargeSchemePaid": 8000
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedBenefit"
                |          ],
                |          "definedBenefitAmount": 2000,
                |          "thresholdIncome": "no",
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2018": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RP"
                |              },
                |              "whichScheme": "00348916RP",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 43000
                |              },
                |              "payACharge": false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": false,
                |          "thresholdIncome": "yes",
                |          "totalIncome": 200000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2019": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RP"
                |              },
                |              "whichScheme": "00348916RP",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 52000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "both",
                |              "howMuchAAChargeYouPaid": 3400,
                |              "howMuchAAChargeSchemePaid": 4700
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": false,
                |          "thresholdIncome": "no",
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		  "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2020": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RP"
                |              },
                |              "whichScheme": "00348916RP",
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 0
                |              },
                |              "payACharge": false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "thresholdIncome": "no",
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution",
                |            "definedBenefit"
                |          ],
                |          "definedContributionAmount": 700,
                |          "definedBenefitAmount": 4900,
                |          "totalIncome": 60000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    }
                |  }
                |""".stripMargin)
      .as[JsObject]

    val data11 = Json
      .parse(s"""
                |{
                |    "savingsStatement": true,
                |    "navigation": {
                |      "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |      "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |      "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers",
                |      "aaSection2017": "/public-pension-adjustment/annual-allowance/2017/check-answers",
                |      "aaSection2018": "/public-pension-adjustment/annual-allowance/2018/check-answers"
                |    },
                |    "resubmittingAdjustment": false,
                |    "reportingChange": [
                |      "annualAllowance"
                |    ],
                |    "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true
                |      }
                |    },
                |    "scottishTaxpayerFrom2016": false,
                |    "payingPublicPensionScheme": false,
                |    "stopPayingPublicPension": "2017-10-12",
                |    "definedContributionPensionScheme": true,
                |    "flexiblyAccessedPension": true,
                |    "flexibleAccessStartDate": "2015-12-20",
                |    "payTaxCharge1415": true,
                |    "aa": {
                |      "years": {
                |        "2016": {
                |          "memberMoreThanOnePension": true,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "PensionSchemeInput2016preAmounts": {
                |                "revisedPIA": 27000
                |              },
                |              "PensionSchemeInput2016postAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "both",
                |              "howMuchAAChargeYouPaid": 1200,
                |              "howMuchAAChargeSchemePaid": 1600,
                |              "addAnotherScheme": false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution",
                |            "definedBenefit"
                |          ],
                |          "definedContribution2016PreAmount": 2000,
                |          "definedContribution2016PostAmount": 1400,
                |          "definedContribution2016PostFlexiAmount": 800,
                |          "definedBenefit2016PreAmount": 1800,
                |          "definedBenefit2016PostAmount": 2100,
                |          "totalIncome": 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2017": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "whichScheme": "00348916RD",
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "you",
                |              "howMuchAAChargeYouPaid": 800
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution"
                |          ],
                |          "definedContributionAmount": 1600,
                |          "thresholdIncome": "yes",
                |          "totalIncome": 140000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2018": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "whichScheme": "00348916RD",
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "scheme",
                |              "howMuchAAChargeSchemePaid": 900
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedBenefit"
                |          ],
                |          "definedBenefitAmount": 800,
                |          "thresholdIncome": "no",
                |          "totalIncome": 80000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    }
                |  }
                |""".stripMargin)
      .as[JsObject]

    val data12 = Json
      .parse(s"""
                |{
                |  "2011": {
                |    "registeredYear": true,
                |    "pIAPreRemedy": 6000
                |  },
                |  "2012": {
                |    "registeredYear": false
                |  },
                |  "2013": {
                |    "registeredYear": true,
                |    "pIAPreRemedy": 14000
                |  },
                |  "2014": {
                |    "registeredYear": false
                |  },
                |  "2015": {
                |    "registeredYear": true,
                |    "pIAPreRemedy": 18000
                |  },
                |  "navigation": {
                |    "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |    "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |    "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers",
                |    "aaSection2017": "/public-pension-adjustment/annual-allowance/2017/check-answers",
                |    "aaSection2018": "/public-pension-adjustment/annual-allowance/2018/check-answers",
                |    "aaSection2019": "/public-pension-adjustment/annual-allowance/2019/check-answers"
                |  },
                |  "resubmittingAdjustment": true,
                |  "reasonForResubmission": "Incorrect data",
                |  "reportingChange": [
                |    "annualAllowance"
                |  ],
                |  "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |  "setup": {
                |    "aa": {
                |      "savingsStatement": true
                |    }
                |  },
                |  "scottishTaxpayerFrom2016": true,
                |  "whichYearsScottishTaxpayer": [
                |    "2018"
                |  ],
                |  "payingPublicPensionScheme": false,
                |  "stopPayingPublicPension": "2019-03-12",
                |  "definedContributionPensionScheme": true,
                |  "flexiblyAccessedPension": true,
                |  "flexibleAccessStartDate": "2017-11-09",
                |  "payTaxCharge1415": false,
                |  "aa": {
                |    "years": {
                |      "2016": {
                |        "memberMoreThanOnePension": false,
                |        "schemes": {
                |          "0": {
                |            "pensionSchemeDetails": {
                |              "schemeName": "Scheme 1",
                |              "schemeTaxRef": "00348916RU"
                |            },
                |            "PensionSchemeInput2016preAmounts": {
                |              "revisedPIA": 28000
                |            },
                |            "PensionSchemeInput2016postAmounts": {
                |              "revisedPIA": 36000
                |            },
                |            "payACharge": true,
                |            "whoPaidAACharge": "both",
                |            "howMuchAAChargeYouPaid": 900,
                |            "howMuchAAChargeSchemePaid": 1700
                |          }
                |        },
                |        "otherDefinedBenefitOrContribution": true,
                |        "contributedToDuringRemedyPeriod": [
                |          "definedContribution",
                |          "definedBenefit"
                |        ],
                |        "definedContribution2016PreAmount": 12000,
                |        "definedContribution2016PostAmount": 14000,
                |        "definedBenefit2016PreAmount": 10000,
                |        "definedBenefit2016PostAmount": 11000,
                |        "totalIncome": 60000,
                |        "claimingTaxReliefPension": true,
                |        "taxRelief": 888,
                |        "blindAllowance": true,
                |        "blindPersonsAllowanceAmount": 2291
                |      },
                |      "2017": {
                |        "memberMoreThanOnePension": false,
                |        "schemes": {
                |          "0": {
                |            "whichScheme": "00348916RU",
                |            "pensionSchemeDetails": {
                |              "schemeName": "Scheme 1",
                |              "schemeTaxRef": "00348916RU"
                |            },
                |            "pensionSchemeInputAmounts": {
                |              "revisedPIA": 33000
                |            },
                |            "payACharge": true,
                |            "whoPaidAACharge": "scheme",
                |            "howMuchAAChargeSchemePaid": 1900
                |          }
                |        },
                |        "otherDefinedBenefitOrContribution": true,
                |        "contributedToDuringRemedyPeriod": [
                |          "definedContribution"
                |        ],
                |        "definedContributionAmount": 23000,
                |        "thresholdIncome": "no",
                |        "totalIncome": 80000,
                |        "anySalarySacrificeArrangements": true,
                |        "amountSalarySacrificeArrangements": 444,
                |        "flexibleRemunerationArrangements": true,
                |        "amountFlexibleRemunerationArrangements": 666,
                |        "didYouContributeToRASScheme": true,
                |		 "rASContributionAmount": 712,
                |        "howMuchContributionPensionScheme": 1212,
                |        "anyLumpSumDeathBenefits": true,
                |        "lumpSumDeathBenefitsValue": 777,
                |        "claimingTaxReliefPension": true,
                |        "aboveThreshold": true,
                |        "taxRelief": 888,
                |        "knowAdjustedAmount": false,
                |        "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |        "howMuchTaxReliefPension": 1111,
                |        "areYouNonDom": true,
                |        "hasReliefClaimedOnOverseasPension": true,
                |        "amountClaimedOnOverseasPension": 1414,
                |        "doYouHaveGiftAid": true,
                |        "amountOfGiftAid": 842,
                |        "doYouKnowPersonalAllowance": false,
                |        "doYouHaveCodeAdjustment": true,
                |        "tradeUnionRelief": true,
                |        "unionPoliceReliefAmount": 90,
                |        "blindAllowance": true,
                |        "blindPersonsAllowanceAmount": 2291
                |      },
                |      "2018": {
                |        "memberMoreThanOnePension": false,
                |        "schemes": {
                |          "0": {
                |            "pensionSchemeDetails": {
                |              "schemeName": "Scheme 1",
                |              "schemeTaxRef": "00348916RU"
                |            },
                |            "whichScheme": "00348916RU",
                |            "pensionSchemeInputAmounts": {
                |              "revisedPIA": 54000
                |            },
                |            "payACharge": true,
                |            "whoPaidAACharge": "you",
                |            "howMuchAAChargeYouPaid": 2700
                |          }
                |        },
                |        "otherDefinedBenefitOrContribution": true,
                |        "contributedToDuringRemedyPeriod": [
                |          "definedContribution",
                |          "definedBenefit"
                |        ],
                |        "definedContributionAmount": 3200,
                |        "flexiAccessDefinedContributionAmount": 4200,
                |        "definedBenefitAmount": 8000,
                |        "thresholdIncome": "yes",
                |        "totalIncome": 140000,
                |        "anySalarySacrificeArrangements": true,
                |        "amountSalarySacrificeArrangements": 444,
                |        "flexibleRemunerationArrangements": true,
                |        "amountFlexibleRemunerationArrangements": 666,
                |        "didYouContributeToRASScheme": true,
                |		 "rASContributionAmount": 712,
                |        "howMuchContributionPensionScheme": 1212,
                |        "anyLumpSumDeathBenefits": true,
                |        "lumpSumDeathBenefitsValue": 777,
                |        "claimingTaxReliefPension": true,
                |        "aboveThreshold": true,
                |        "taxRelief": 888,
                |        "knowAdjustedAmount": false,
                |        "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |        "howMuchTaxReliefPension": 1111,
                |        "areYouNonDom": true,
                |        "hasReliefClaimedOnOverseasPension": true,
                |        "amountClaimedOnOverseasPension": 1414,
                |        "doYouHaveGiftAid": true,
                |        "amountOfGiftAid": 842,
                |        "doYouKnowPersonalAllowance": false,
                |        "doYouHaveCodeAdjustment": true,
                |        "tradeUnionRelief": true,
                |        "unionPoliceReliefAmount": 90,
                |        "blindAllowance": true,
                |        "blindPersonsAllowanceAmount": 2291
                |      },
                |      "2019": {
                |        "memberMoreThanOnePension": false,
                |        "schemes": {
                |          "0": {
                |            "pensionSchemeDetails": {
                |              "schemeName": "Scheme 1",
                |              "schemeTaxRef": "00348916RU"
                |            },
                |            "whichScheme": "00348916RU",
                |            "pensionSchemeInputAmounts": {
                |              "revisedPIA": 46000
                |            },
                |            "payACharge": true,
                |            "whoPaidAACharge": "both",
                |            "howMuchAAChargeYouPaid": 800,
                |            "howMuchAAChargeSchemePaid": 2600
                |          }
                |        },
                |        "otherDefinedBenefitOrContribution": false,
                |        "thresholdIncome": "no",
                |        "totalIncome": 90000,
                |        "anySalarySacrificeArrangements": true,
                |        "amountSalarySacrificeArrangements": 444,
                |        "flexibleRemunerationArrangements": true,
                |        "amountFlexibleRemunerationArrangements": 666,
                |        "didYouContributeToRASScheme": true,
                |		 "rASContributionAmount": 712,
                |        "howMuchContributionPensionScheme": 1212,
                |        "anyLumpSumDeathBenefits": true,
                |        "lumpSumDeathBenefitsValue": 777,
                |        "claimingTaxReliefPension": true,
                |        "aboveThreshold": true,
                |        "taxRelief": 888,
                |        "knowAdjustedAmount": false,
                |        "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |        "howMuchTaxReliefPension": 1111,
                |        "areYouNonDom": true,
                |        "hasReliefClaimedOnOverseasPension": true,
                |        "amountClaimedOnOverseasPension": 1414,
                |        "doYouHaveGiftAid": true,
                |        "amountOfGiftAid": 842,
                |        "doYouKnowPersonalAllowance": false,
                |        "doYouHaveCodeAdjustment": true,
                |        "tradeUnionRelief": true,
                |        "unionPoliceReliefAmount": 90,
                |        "blindAllowance": true,
                |        "blindPersonsAllowanceAmount": 2291
                |      }
                |    }
                |  }
                |}
                |""".stripMargin)
      .as[JsObject]

    val data13 = Json
      .parse(s"""
                |{
                |    "navigation": {
                |      "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |      "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |      "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers",
                |      "aaSection2017": "/public-pension-adjustment/annual-allowance/2017/check-answers",
                |      "aaSection2018": "/public-pension-adjustment/annual-allowance/2018/check-answers"
                |    },
                |    "resubmittingAdjustment": false,
                |    "reportingChange": [
                |      "annualAllowance"
                |    ],
                |    "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |    "setup": {
                |      "aa": {
                |        "savingsStatement": true
                |      }
                |    },
                |    "scottishTaxpayerFrom2016": false,
                |    "payingPublicPensionScheme": false,
                |    "stopPayingPublicPension": "2017-10-12",
                |    "definedContributionPensionScheme": true,
                |    "flexiblyAccessedPension": true,
                |    "flexibleAccessStartDate": "2015-05-20",
                |    "payTaxCharge1415": true,
                |    "aa": {
                |      "years": {
                |        "2016": {
                |          "memberMoreThanOnePension": true,
                |          "schemes": {
                |            "0": {
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "PensionSchemeInput2016preAmounts": {
                |                "revisedPIA": 27000
                |              },
                |              "PensionSchemeInput2016postAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "both",
                |              "howMuchAAChargeYouPaid": 1200,
                |              "howMuchAAChargeSchemePaid": 1600,
                |              "addAnotherScheme": false
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution",
                |            "definedBenefit"
                |          ],
                |          "definedContribution2016PreAmount": 2000,
                |          "definedContribution2016PreFlexiAmount": 800,
                |          "definedContribution2016PostAmount": 1400,
                |          "definedBenefit2016PreAmount": 1800,
                |          "definedBenefit2016PostAmount": 2100,
                |          "totalIncome": 60000,
                |          "claimingTaxReliefPension": true,
                |          "taxRelief": 888,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |
                |        },
                |        "2017": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "whichScheme": "00348916RD",
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "you",
                |              "howMuchAAChargeYouPaid": 800
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedContribution"
                |          ],
                |          "definedContributionAmount": 1600,
                |          "thresholdIncome": "yes",
                |          "totalIncome": 140000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        },
                |        "2018": {
                |          "memberMoreThanOnePension": false,
                |          "schemes": {
                |            "0": {
                |              "whichScheme": "00348916RD",
                |              "pensionSchemeDetails": {
                |                "schemeName": "Scheme 1",
                |                "schemeTaxRef": "00348916RD"
                |              },
                |              "pensionSchemeInputAmounts": {
                |                "revisedPIA": 36000
                |              },
                |              "payACharge": true,
                |              "whoPaidAACharge": "scheme",
                |              "howMuchAAChargeSchemePaid": 900
                |            }
                |          },
                |          "otherDefinedBenefitOrContribution": true,
                |          "contributedToDuringRemedyPeriod": [
                |            "definedBenefit"
                |          ],
                |          "definedBenefitAmount": 800,
                |          "thresholdIncome": "no",
                |          "totalIncome": 80000,
                |          "anySalarySacrificeArrangements": true,
                |          "amountSalarySacrificeArrangements": 444,
                |          "flexibleRemunerationArrangements": true,
                |          "amountFlexibleRemunerationArrangements": 666,
                |          "didYouContributeToRASScheme": true,
                |		   "rASContributionAmount": 712,
                |          "howMuchContributionPensionScheme": 1212,
                |          "anyLumpSumDeathBenefits": true,
                |          "lumpSumDeathBenefitsValue": 777,
                |          "claimingTaxReliefPension": true,
                |          "aboveThreshold": true,
                |          "taxRelief": 888,
                |          "knowAdjustedAmount": false,
                |          "ClaimingTaxReliefPensionNotAdjustedIncome": true,
                |          "howMuchTaxReliefPension": 1111,
                |          "areYouNonDom": true,
                |          "hasReliefClaimedOnOverseasPension": true,
                |          "amountClaimedOnOverseasPension": 1414,
                |          "doYouHaveGiftAid": true,
                |          "amountOfGiftAid": 842,
                |          "doYouKnowPersonalAllowance": false,
                |          "doYouHaveCodeAdjustment": true,
                |          "tradeUnionRelief": true,
                |          "unionPoliceReliefAmount": 90,
                |          "blindAllowance": true,
                |          "blindPersonsAllowanceAmount": 2291
                |        }
                |      }
                |    }
                |  }
                |""".stripMargin)
      .as[JsObject]

    val data14 = Json
      .parse(s"""
                |{
                |  "navigation": {
                |    "setupSection": "/public-pension-adjustment/check-your-answers-setup",
                |    "preAASection": "/public-pension-adjustment/annual-allowance/setup-check-answers",
                |    "aaSection2016": "/public-pension-adjustment/annual-allowance/2016/check-answers"
                |  },
                |  "resubmittingAdjustment": false,
                |  "reportingChange": [
                |    "annualAllowance"
                |  ],
                |  "kickoutStatus": {
                |      "annualAllowance": 2
                |    },
                |  "setup": {
                |    "aa": {
                |      "savingsStatement": true
                |    }
                |  },
                |  "scottishTaxpayerFrom2016": false,
                |  "payingPublicPensionScheme": false,
                |  "stopPayingPublicPension": "2015-07-01",
                |  "definedContributionPensionScheme": true,
                |  "flexiblyAccessedPension": true,
                |  "flexibleAccessStartDate": "2015-05-25",
                |  "payTaxCharge1415": true,
                |  "aa": {
                |    "years": {
                |      "2016": {
                |        "memberMoreThanOnePension": false,
                |        "schemes": {
                |          "0": {
                |            "pensionSchemeDetails": {
                |              "schemeName": "Scheme 1",
                |              "schemeTaxRef": "00348916RK"
                |            },
                |            "PensionSchemeInput2016preAmounts": {
                |              "revisedPIA": 18000
                |            },
                |            "payACharge": true,
                |            "whoPaidAACharge": "both",
                |            "howMuchAAChargeYouPaid": 800,
                |            "howMuchAAChargeSchemePaid": 1200
                |          }
                |        },
                |        "otherDefinedBenefitOrContribution": true,
                |        "contributedToDuringRemedyPeriod": [
                |          "definedContribution",
                |          "definedBenefit"
                |        ],
                |        "definedContribution2016PreAmount": 700,
                |        "definedContribution2016PreFlexiAmount": 1200,
                |        "definedBenefit2016PreAmount": 700,
                |        "totalIncome": 60000,
                |        "claimingTaxReliefPension": true,
                |        "taxRelief": 888,
                |        "blindAllowance": true,
                |        "blindPersonsAllowanceAmount": 2291
                |      }
                |    }
                |  }
                |}
                |""".stripMargin)
      .as[JsObject]

    val userAnswers1 = UserAnswers(
      id = "session-5a356f3a-c83e-4e8c-a957-226163ba285f",
      data = data1
    )

    "toTaxYear2011To2015" - {

      "should return valid TaxYear2011To2015 for a Period 2011" in {
        val result = service.toTaxYear2011To2015(userAnswers1, Period._2011)

        result mustBe Some(TaxYear2011To2015(10000, Period._2011))
      }

      "should return None for a Period 2012" in {
        val result = service.toTaxYear2011To2015(userAnswers1, Period._2012)

        result mustBe None
      }

      "should return valid TaxYear2011To2015 for a Period 2013" in {
        val result = service.toTaxYear2011To2015(userAnswers1, Period._2013)

        result mustBe Some(TaxYear2011To2015(40000, Period._2013))
      }

      "should return valid TaxYear2011To2015 for a Period 2014" in {
        val result = service.toTaxYear2011To2015(userAnswers1, Period._2014)

        result mustBe Some(TaxYear2011To2015(20000, Period._2014))
      }

      "should return valid TaxYear2011To2015 for a Period 2015" in {
        val result = service.toTaxYear2011To2015(userAnswers1, Period._2015)

        result mustBe Some(TaxYear2011To2015(60000, Period._2015))
      }

      "should return None for a missing Period 2015" in {
        val result = service.toTaxYear2011To2015(userAnswers1.copy(data = data2), Period._2015)

        result mustBe None
      }

    }

    "toTaxYear2016To2023" - {

      "should return valid TaxYear2016To2023.InitialFlexiblyAccessedTaxYear for a Period 2016" in {

        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2016).futureValue

        result mustBe Some(
          InitialFlexiblyAccessedTaxYear(
            30015,
            Some(LocalDate.parse("2015-05-25")),
            6015,
            10015,
            List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 0, Some(30000))),
            60000,
            0,
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
              Some(1),
              None,
              Some(2291),
              None,
              Some(2)
            ),
            None,
            Some(30016),
            Some(6016)
          )
        )
      }

      "should return valid TaxYear2016To2023.InitialFlexiblyAccessedTaxYear for a Period 2016 when PublicPension" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2016).futureValue

        result mustBe Some(
          InitialFlexiblyAccessedTaxYear(
            30015,
            Some(LocalDate.parse("2015-05-25")),
            6015,
            10015,
            List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 0, Some(30000))),
            60000,
            0,
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
              Some(1),
              None,
              Some(2291),
              None,
              Some(2)
            ),
            None,
            Some(30016),
            Some(6016)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2017" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2017).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            35000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
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
              Some(1),
              Some(90),
              Some(2291),
              None,
              Some(2)
            ),
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2018" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2018).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            0,
            0,
            60000,
            1000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 1000, None)),
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
              Some(1),
              Some(90),
              Some(2291),
              None,
              Some(2)
            ),
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2019" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2019).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            35000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
            Period._2019,
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
              Some(1),
              Some(90),
              Some(2291),
              None,
              Some(2)
            ),
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2020" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2020).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            34000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 34000, 0, None)),
            Period._2020,
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
              Some(1),
              Some(90),
              Some(2291),
              None,
              Some(2)
            ),
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2021" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1.copy(data = data2), Period._2021).futureValue

        result mustBe
          Some(
            PostFlexiblyAccessedTaxYear(
              0,
              0,
              60000,
              0,
              List(TaxYearScheme("Scheme 1", "00348916RT", 36000, 0, None)),
              Period._2021,
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
                Some(1),
                Some(90),
                Some(2291),
                None,
                Some(2)
              ),
              Some(AboveThreshold(96148))
            )
          )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2022" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1, Period._2022).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            44000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 44000, 0, None)),
            Period._2022,
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
              Some(1),
              Some(90),
              Some(2291),
              None,
              Some(2)
            ),
            Some(BelowThreshold)
          )
        )
      }

      "should return valid TaxYear2016To2023.PostFlexiblyAccessedTaxYear for a Period 2023" in {
        when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
          .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
        val result = service.toTaxYear2016To2023(userAnswers1.copy(data = data2), Period._2023).futureValue

        result mustBe Some(
          PostFlexiblyAccessedTaxYear(
            53000,
            0,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 53000, 4400, None)),
            Period._2023,
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
              Some(1),
              Some(90),
              Some(2291),
              Some(58733),
              Some(2)
            ),
            income = Some(AboveThreshold(166148))
          )
        )
      }

    }

    "buildInputs" - {

      when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any())(any()))
        .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))

      "should return 2016 as the InitialFlexiblyAccessedTaxYear when stopPayingPublicPension falls in 2016 " in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data14)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(),
              List(
                InitialFlexiblyAccessedTaxYear(
                  700,
                  Some(LocalDate.parse("2015-05-25")),
                  700,
                  1200,
                  List(TaxYearScheme("Scheme 1", "00348916RK", 18000, 1200, None)),
                  60000,
                  800,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  None,
                  None,
                  None
                )
              )
            )
          ),
          None
        )
      }

      "should return 2016 as the InitialFlexiblyAccessedTaxYear and 2017, 2018 as the PostFlexiblyAccessedTaxYear when FlexibleAccessStartDate falls in 2016 post period" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data11)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(),
              List(
                InitialFlexiblyAccessedTaxYear(
                  1800,
                  Some(LocalDate.parse("2015-12-20")),
                  2000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 27000, 1600, Some(36000))),
                  60000,
                  1200,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(2100),
                  Some(1400),
                  Some(800)
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  1600,
                  140000,
                  800,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 36000, 0, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(AboveThreshold(177748)),
                  None,
                  None
                ),
                PostFlexiblyAccessedTaxYear(
                  800,
                  0,
                  80000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 36000, 900, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold),
                  None,
                  None
                )
              )
            )
          ),
          None
        )
      }

      "should return 2016 as the InitialFlexiblyAccessedTaxYear and 2017, 2018 as the PostFlexiblyAccessedTaxYear when FlexibleAccessStartDate falls in 2016 pre period" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data13)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(),
              List(
                InitialFlexiblyAccessedTaxYear(
                  1800,
                  Some(LocalDate.parse("2015-05-20")),
                  2000,
                  800,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 27000, 1600, Some(36000))),
                  60000,
                  1200,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(2100),
                  Some(1400),
                  None
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  1600,
                  140000,
                  800,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 36000, 0, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(AboveThreshold(177748)),
                  None,
                  None
                ),
                PostFlexiblyAccessedTaxYear(
                  800,
                  0,
                  80000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RD", 36000, 900, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold),
                  None,
                  None
                )
              )
            )
          ),
          None
        )
      }

      "should return 2016, 2017 as the NormalTaxYear and 2018 as the InitialFlexiblyAccessedTaxYear and 2019 as the PostFlexiblyAccessedTaxYear when FlexibleAccessStartDate falls in 2018" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data12)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(true, Some("Incorrect data")),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(Period._2018),
              List(
                TaxYear2011To2015(6000, Period._2011),
                TaxYear2011To2015(14000, Period._2013),
                TaxYear2011To2015(18000, Period._2015),
                NormalTaxYear(
                  50000,
                  List(TaxYearScheme("Scheme 1", "00348916RU", 28000, 1700, Some(36000))),
                  60000,
                  900,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(61000)
                ),
                NormalTaxYear(
                  56000,
                  List(TaxYearScheme("Scheme 1", "00348916RU", 33000, 1900, None)),
                  80000,
                  0,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold),
                  None
                ),
                InitialFlexiblyAccessedTaxYear(
                  8000,
                  Some(LocalDate.parse("2017-11-09")),
                  3200,
                  4200,
                  List(TaxYearScheme("Scheme 1", "00348916RU", 54000, 0, None)),
                  140000,
                  2700,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(AboveThreshold(209548)),
                  None,
                  None,
                  None
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  0,
                  90000,
                  800,
                  List(TaxYearScheme("Scheme 1", "00348916RU", 46000, 2600, None)),
                  Period._2019,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold),
                  None,
                  None
                )
              )
            )
          ),
          None
        )

      }

      "should return valid CalculationInputs for a valid UserAnswers with all years" in {

        val result = service.buildCalculationInputs(userAnswers1).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(true, Some("Change in amounts")),
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
                Some(false)
              )
            )
          ),
          Some(
            AnnualAllowance(
              List.empty,
              List(
                TaxYear2011To2015(10000, Period._2011),
                TaxYear2011To2015(40000, Period._2013),
                TaxYear2011To2015(20000, Period._2014),
                TaxYear2011To2015(60000, Period._2015),
                InitialFlexiblyAccessedTaxYear(
                  30015,
                  Some(LocalDate.parse("2015-05-25")),
                  6015,
                  10015,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 0, Some(30000))),
                  60000,
                  0,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(30016),
                  Some(6016)
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  0,
                  60000,
                  1000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 1000, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
                  Period._2019,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  34000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 34000, 0, None)),
                  Period._2020,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 36000, 0, None)),
                  Period._2021,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  44000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 44000, 0, None)),
                  Period._2022,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  53000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 53000, 4400, None)),
                  Period._2023,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                )
              )
            )
          ),
          Some(
            LifeTimeAllowance(
              LocalDate.parse("2018-11-28"),
              LtaProtectionOrEnhancements.Protection,
              Some(ProtectionType.FixedProtection2014),
              Some("R41AB678TR23355"),
              ProtectionEnhancedChanged.Protection,
              Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
              Some("2134567801"),
              true,
              Some(ExcessLifetimeAllowancePaid.Annualpayment),
              Some(WhoPaidLTACharge.PensionScheme),
              Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
              Some(WhoPayingExtraLtaCharge.You),
              None,
              NewLifeTimeAllowanceAdditions(
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None
              )
            )
          )
        )
      }

      "should return valid CalculationInputs for a valid UserAnswers with missing years" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data3)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List.empty,
              List(
                TaxYear2011To2015(10000, Period._2012),
                TaxYear2011To2015(40000, Period._2013),
                InitialFlexiblyAccessedTaxYear(
                  30000,
                  Some(LocalDate.parse("2015-05-25")),
                  6000,
                  10000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 0, Some(40000))),
                  60000,
                  2000,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(40000),
                  None,
                  None
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  0,
                  60000,
                  1000,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 40000, 1000, None)),
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  35000,
                  0,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RT", 35000, 0, None)),
                  Period._2019,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )
      }

      "should return valid CalculationInputs with correct NormalTaxYear/InitialFlexiblyAccessedTaxYear/PostFlexiblyAccessedTaxYear for a valid UserAnswers when flexiblyAccessedPension = false" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data9)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(Period._2019),
              List(
                NormalTaxYear(
                  40000,
                  List(TaxYearScheme("Scheme 1", "00348916RO", 20000, 3600, Some(22000))),
                  0,
                  3600,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(40000)
                ),
                NormalTaxYear(
                  45000,
                  List(TaxYearScheme("Scheme 1", "00348916RO", 45000, 0, None)),
                  60000,
                  0,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                NormalTaxYear(
                  38000,
                  List(TaxYearScheme("Scheme 1", "00348916RO", 38000, 200, None)),
                  60000,
                  200,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                NormalTaxYear(
                  43000,
                  List(TaxYearScheme("Scheme 1", "00348916RO", 43000, 0, None)),
                  60000,
                  3280,
                  Period._2019,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )
      }

      "should return valid CalculationInputs with correct NormalTaxYear/InitialFlexiblyAccessedTaxYear/PostFlexiblyAccessedTaxYear for a valid UserAnswers when flexiblyAccessedPension = true" in {

        val result = service.buildCalculationInputs(userAnswers1.copy(data = data10)).futureValue

        result mustBe CalculationResults.CalculationInputs(
          Resubmission(false, None),
          Setup(
            Some(AnnualAllowanceSetup(Some(true), None, None, None, None, None, None, None, None, None, None, None)),
            None
          ),
          Some(
            AnnualAllowance(
              List(),
              List(
                NormalTaxYear(
                  30600,
                  List(TaxYearScheme("Scheme 1", "00348916RP", 28000, 0, Some(34000))),
                  60000,
                  300,
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
                    Some(1),
                    None,
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  None,
                  Some(34000)
                ),
                NormalTaxYear(
                  33000,
                  List(TaxYearScheme("Scheme 1", "00348916RP", 31000, 8000, None)),
                  60000,
                  0,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold),
                  None
                ),
                InitialFlexiblyAccessedTaxYear(
                  0,
                  Some(LocalDate.parse("2017-10-20")),
                  0,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RP", 43000, 0, None)),
                  200000,
                  0,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(AboveThreshold(243148))
                ),
                PostFlexiblyAccessedTaxYear(
                  0,
                  0,
                  60000,
                  3400,
                  List(TaxYearScheme("Scheme 1", "00348916RP", 52000, 4700, None)),
                  Period._2019,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                ),
                PostFlexiblyAccessedTaxYear(
                  4900,
                  700,
                  60000,
                  0,
                  List(TaxYearScheme("Scheme 1", "00348916RP", 0, 0, None)),
                  Period._2020,
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
                    Some(1),
                    Some(90),
                    Some(2291),
                    None,
                    Some(2)
                  ),
                  Some(BelowThreshold)
                )
              )
            )
          ),
          None
        )
      }

    }

    "buildLifeTimeAllowance" - {

      "should return Some LifeTimeAllowance data model for a valid UserAnswers with LifeTimeAllowance user inputs" in {
        val result = service.buildLifeTimeAllowance(userAnswers1.copy(data = data4))

        result mustBe Some(
          LifeTimeAllowance(
            LocalDate.parse("2018-11-28"),
            LtaProtectionOrEnhancements.Protection,
            Some(ProtectionType.FixedProtection2014),
            Some("R41AB678TR23355"),
            ProtectionEnhancedChanged.Protection,
            Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
            Some("2134567801"),
            true,
            Some(ExcessLifetimeAllowancePaid.Annualpayment),
            Some(WhoPaidLTACharge.PensionScheme),
            Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
            Some(WhoPayingExtraLtaCharge.You),
            None,
            NewLifeTimeAllowanceAdditions(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            )
          )
        )
      }

      "should return None LifeTimeAllowance data model for a valid UserAnswers with LifeTimeAllowance user input when an LTA kick out has been reached" in {

        val userAnswers    = userAnswers1.copy(data = data8)
        val answersWithNav = LTASection.saveNavigation(userAnswers, LTASection.cannotUseLtaServiceNoChargePage.url)
        val result         = service.buildLifeTimeAllowance(answersWithNav)

        result mustBe None
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
      sections.size mustBe 4

      val year = sections(0)

      checkRowNameAndValue(year, 0, "periodDateRangeAA.2016", "2016")
      checkRowNameAndValue(year, 1, "calculationResults.annualResults.chargePaidBySchemes", "0")
      checkRowNameAndValue(year, 2, "calculationResults.annualResults.chargePaidByMember", "0")
      checkRowNameAndValue(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0")
      checkRowNameAndValue(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0")
      checkRowNameAndValue(year, 5, "calculationResults.annualResults.directCompensation", "0")
      checkRowNameAndValue(year, 6, "calculationResults.annualResults.indirectCompensation", "0")
      checkRowNameAndValue(year, 7, "calculationResults.annualResults.unusedAnnualAllowance", "60000")

      viewModel.annualResultsData mustBe List(
        List(
          RowViewModel("periodDateRangeAA.2016", "2016"),
          RowViewModel("calculationResults.annualResults.chargePaidBySchemes", "0"),
          RowViewModel("calculationResults.annualResults.chargePaidByMember", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0"),
          RowViewModel("calculationResults.annualResults.directCompensation", "0"),
          RowViewModel("calculationResults.annualResults.indirectCompensation", "0"),
          RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", "60000")
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

    "Caculation Reivew Individual AA" - {
      "out dates Review AA should be well formed and should filter chosen period when given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: Future[CalculationReviewIndividualAAViewModel] =
          service.calculationReviewIndividualAAViewModel(
            calculationResult,
            Some(Period._2016.toString()),
            userAnswers1.copy(data = data2)
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.futureValue.outDates
        sections.size mustBe 1

        val year = sections(0)

        checkRowNameAndValue(year, 0, "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes", "0")
        checkRowNameAndValue(year, 1, "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember", "0")
        checkRowNameAndValue(
          year,
          2,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          year,
          3,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(year, 4, "calculationReviewIndividualAA.annualResults.outDates.directCompensation", "0")
        checkRowNameAndValue(year, 5, "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation", "0")
        checkRowNameAndValue(
          year,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "60000"
        )
      }

      "in dates Review AA should be well formed and should filter chosen period when given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: Future[CalculationReviewIndividualAAViewModel] =
          service.calculationReviewIndividualAAViewModel(
            calculationResult,
            Some(Period._2020.toString()),
            userAnswers1.copy(data = data2)
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.futureValue.inDates
        sections.size mustBe 1

        val year = sections(0)

        checkRowNameAndValue(year, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "0")
        checkRowNameAndValue(year, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "0")
        checkRowNameAndValue(
          year,
          2,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          year,
          3,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(year, 4, "calculationReviewIndividualAA.annualResults.inDates.debit", "0")
        checkRowNameAndValue(year, 5, "calculationReviewIndividualAA.annualResults.inDates.memberCredit", "0")
        checkRowNameAndValue(year, 6, "calculationReviewIndividualAA.annualResults.inDates.schemeCredit", "0")
        checkRowNameAndValue(
          year,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "48000"
        )

      }

      "out dates Review AA should be well formed and should return all period when NOT given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: Future[CalculationReviewIndividualAAViewModel] =
          service.calculationReviewIndividualAAViewModel(
            calculationResult,
            None,
            userAnswers1.copy(data = data2)
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.futureValue.outDates
        sections.size mustBe 4

        val _2016Period = sections(0)
        val _2017Period = sections(1)

        println(_2017Period)

        checkRowNameAndValue(
          _2016Period,
          0,
          "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          1,
          "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          2,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          3,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          4,
          "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          5,
          "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
          "0"
        )
        checkRowNameAndValue(
          _2016Period,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "60000"
        )

        checkRowNameAndValue(
          _2017Period,
          0,
          "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes",
          "0"
        )
        checkRowNameAndValue(
          _2017Period,
          1,
          "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember",
          "1200"
        )
        checkRowNameAndValue(
          _2017Period,
          2,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2017Period,
          3,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2017Period,
          4,
          "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
          "1200"
        )
        checkRowNameAndValue(
          _2017Period,
          5,
          "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
          "0"
        )
        checkRowNameAndValue(
          _2017Period,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "0"
        )

      }

      "in dates Review AA should be well formed and should return all period when NOT given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: Future[CalculationReviewIndividualAAViewModel] =
          service.calculationReviewIndividualAAViewModel(
            calculationResult,
            None,
            userAnswers1.copy(data = data2)
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.futureValue.inDates
        sections.size mustBe 4

        val _2020 = sections(0)
        val _2021 = sections(1)

        println(_2021)

        checkRowNameAndValue(_2020, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "0")
        checkRowNameAndValue(_2020, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "0")
        checkRowNameAndValue(
          _2020,
          2,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2020,
          3,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(_2020, 4, "calculationReviewIndividualAA.annualResults.inDates.debit", "0")
        checkRowNameAndValue(_2020, 5, "calculationReviewIndividualAA.annualResults.inDates.memberCredit", "0")
        checkRowNameAndValue(_2020, 6, "calculationReviewIndividualAA.annualResults.inDates.schemeCredit", "0")
        checkRowNameAndValue(
          _2020,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "48000"
        )

        checkRowNameAndValue(_2021, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "0")
        checkRowNameAndValue(_2021, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "0")
        checkRowNameAndValue(
          _2021,
          2,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "0"
        )
        checkRowNameAndValue(
          _2021,
          3,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "0"
        )
        checkRowNameAndValue(_2021, 4, "calculationReviewIndividualAA.annualResults.inDates.debit", "0")
        checkRowNameAndValue(_2021, 5, "calculationReviewIndividualAA.annualResults.inDates.memberCredit", "0")
        checkRowNameAndValue(_2021, 6, "calculationReviewIndividualAA.annualResults.inDates.schemeCredit", "0")
        checkRowNameAndValue(
          _2021,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "56000"
        )

      }
    }

    "individualAASummaryModel" - {
      "Should form a sequence including indates and outdates" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val summaryModel: Seq[IndividualAASummaryModel] =
          service.individualAASummaryModel(
            calculationResult
          )

        summaryModel.size mustBe 8

        summaryModel mustBe List(
          IndividualAASummaryModel(
            Period._2016,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2017,
            1200,
            1200,
            "calculationReviewIndividualAA.changeInTaxChargeString.decrease.",
            0,
            1200,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2018,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2019,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2020,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2021,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2022,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          ),
          IndividualAASummaryModel(
            Period._2023,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            0,
            0,
            0,
            0
          )
        )

      }

      "An out dates year should be well formed" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val summaryModel: Seq[IndividualAASummaryModel] =
          service.individualAASummaryModel(
            calculationResult
          )

        summaryModel.size mustBe 8

        val year2016 = summaryModel(0)

        year2016.period mustBe Period._2016
        year2016.changeInTaxCharge mustBe 0
        year2016.changeInTaxChargeNonAbs mustBe 0
        year2016.changeInTaxChargeString mustBe "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
        year2016.revisedChargeableAmountBeforeTaxRate mustBe 0
        year2016.chargePaidByMember mustBe 0
        year2016.chargePaidBySchemes mustBe 0
        year2016.revisedChargeableAmountAfterTaxRate mustBe 0
      }

      "An in date year should be well formed" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val summaryModel: Seq[IndividualAASummaryModel] =
          service.individualAASummaryModel(
            calculationResult
          )

        summaryModel.size mustBe 8

        val year2021 = summaryModel(5)

        year2021.period mustBe Period._2021
        year2021.changeInTaxCharge mustBe 0
        year2021.changeInTaxChargeNonAbs mustBe 0
        year2021.changeInTaxChargeString mustBe "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
        year2021.revisedChargeableAmountBeforeTaxRate mustBe 0
        year2021.chargePaidByMember mustBe 0
        year2021.chargePaidBySchemes mustBe 0
        year2021.revisedChargeableAmountAfterTaxRate mustBe 0

      }
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

    "Calculation review" - {

      def checkRowNameAndValueReviewRow(
        rows: Seq[ReviewRowViewModel],
        index: Int,
        expectedTitle: String,
        expectedString: Option[String],
        expectedLink: String,
        expectedTotalCharge: Option[Int]
      ): Unit = {
        rows(index).title mustBe expectedTitle
        rows(index).changeString mustBe expectedString
        rows(index).link mustBe expectedLink
        rows(index).totalCharge mustBe expectedTotalCharge
      }

      def checkRowNameReviewRowLTA(row: ReviewRowViewModel, expectedTitle: String, expectedLink: String): Unit = {
        row.title mustBe expectedTitle
        row.changeString mustBe None
        row.link mustBe expectedLink
        row.totalCharge mustBe None
      }

      val index = 0

      "out dates must be well formed" in {

        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: CalculationReviewViewModel =
          service.calculationReviewViewModel(calculationResult)

        val sections: Seq[Seq[ReviewRowViewModel]] = viewModel.outDates
        sections.size mustBe 4

        checkRowNameAndValueReviewRow(
          sections(0),
          index,
          "calculationReview.period.2016",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2016",
          Some(0)
        )
        checkRowNameAndValueReviewRow(
          sections(1),
          index,
          "calculationReview.period.2017",
          Some("calculationReview.taxChargeDecreasedBy"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2017",
          Some(1200)
        )
        checkRowNameAndValueReviewRow(
          sections(2),
          index,
          "calculationReview.period.2018",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2018",
          Some(0)
        )
        checkRowNameAndValueReviewRow(
          sections(3),
          index,
          "calculationReview.period.2019",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2019",
          Some(0)
        )
      }

      "in dates must be well formed" in {

        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: CalculationReviewViewModel =
          service.calculationReviewViewModel(calculationResult)

        val sections: Seq[Seq[ReviewRowViewModel]] = viewModel.inDates
        sections.size mustBe 4

        checkRowNameAndValueReviewRow(
          sections(0),
          index,
          "calculationReview.period.2020",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2020",
          Some(0)
        )
        checkRowNameAndValueReviewRow(
          sections(1),
          index,
          "calculationReview.period.2021",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2021",
          Some(0)
        )
        checkRowNameAndValueReviewRow(
          sections(2),
          index,
          "calculationReview.period.2022",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2022",
          Some(0)
        )
        checkRowNameAndValueReviewRow(
          sections(3),
          index,
          "calculationReview.period.2023",
          Some("calculationReview.taxChargeNotChanged"),
          "/public-pension-adjustment/CalculationReviewIndividualAA/2023",
          Some(0)
        )
      }

      "lta must be well formed" in {

        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val viewModel: CalculationReviewViewModel =
          service.calculationReviewViewModel(calculationResult)

        val sections: Seq[ReviewRowViewModel] = viewModel.lifetimeAllowance
        sections.size mustBe 1

        checkRowNameReviewRowLTA(sections(0), "calculationReview.lta", "lifetime-allowance/view-answers")
      }
    }

  }

  "submitting user answers and calculation to backend" - {

    "when a valid calculation result is sent successfully a unique id should be returned" in {

      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any)(any))
        .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
      when(mockCalculationResultConnector.sendRequest(any)).thenReturn(Future.successful(calculationResult))
      when(mockSubmissionsConnector.sendSubmissionRequest(any)(any)).thenReturn(Future.successful(Success("uniqueId")))

      val submissionResponse = service.submitUserAnswersAndCalculation(emptyUserAnswers, "sessionId")
      submissionResponse.futureValue.asInstanceOf[Success].uniqueId mustBe "uniqueId"
    }

    "must fail when a valid calculation result cannot be obtained" in {

      when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any)(any))
        .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
      when(mockCalculationResultConnector.sendRequest(any))
        .thenReturn(Future.failed(new RuntimeException("someError")))

      val result = service.submitUserAnswersAndCalculation(emptyUserAnswers, "sessionId")
      an[RuntimeException] mustBe thrownBy(result.futureValue)
    }

    "must fail when a calculation result cannot sent" in {

      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      when(mockReducedNetIncomeConnector.sendReducedNetIncomeRequest(any)(any))
        .thenReturn(Future.successful(ReducedNetIncomeResponse(1, 2)))
      when(mockCalculationResultConnector.sendRequest(any)).thenReturn(Future.successful(calculationResult))
      when(mockSubmissionsConnector.sendSubmissionRequest(any)(any))
        .thenReturn(Future.failed(new RuntimeException("someError")))

      val result = service.submitUserAnswersAndCalculation(emptyUserAnswers, "sessionId")
      an[RuntimeException] mustBe thrownBy(result.futureValue)
    }
  }

  "adjustedIncomeCalculation" - {

    "(Using test thread data, scenario 1 2017/18, test thread v0.5) if user does not know their adjusted income, must calculate it" in {

      val period: Period = Period._2022

      val userAnswers = emptyUserAnswers
        .set(PensionSchemeInputAmountsPage(period, SchemeIndex(0)), PensionSchemeInputAmounts(29997))
        .success
        .value
        .set(PensionSchemeInputAmountsPage(period, SchemeIndex(1)), PensionSchemeInputAmounts(45000))
        .success
        .value
        .set(DefinedContributionAmountPage(period), BigInt(1))
        .success
        .value
        .set(FlexiAccessDefinedContributionAmountPage(period), BigInt(1))
        .success
        .value
        .set(DefinedBenefitAmountPage(period), BigInt(1))
        .success
        .value
        .set(ThresholdIncomePage(period), ThresholdIncome.Yes)
        .success
        .value
        .set(TotalIncomePage(period), BigInt(160000))
        .success
        .value
        .set(TaxReliefPage(period), BigInt(5000))
        .success
        .value
        .set(KnowAdjustedAmountPage(period), false)
        .success
        .value
        .set(RASContributionAmountPage(period), BigInt(10000))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(period), BigInt(40000))
        .success
        .value
        .set(HowMuchTaxReliefPensionPage(period), BigInt(0))
        .success
        .value
        .set(HowMuchContributionPensionSchemePage(period), BigInt(30000))
        .success
        .value
        .set(AmountClaimedOnOverseasPensionPage(period), BigInt(0))
        .success
        .value

      service.adjustedIncomeCalculation(userAnswers, period) mustBe 180000

    }
  }
}
