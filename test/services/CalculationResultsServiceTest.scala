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

/*package services

import base.SpecBase
import models.CalculationResults.{CalculationResponse, CalculationResultsViewModel, RowViewModel}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

class CalculationResultsServiceTest extends SpecBase {

  val calculationResultsService: CalculationResultsService = new CalculationResultsService

  private def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }

  "resubmission details should be well formed" in {
    val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

    val viewModel: CalculationResultsViewModel =
      calculationResultsService.calculationResultsViewModel(calculationResult)

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
      calculationResultsService.calculationResultsViewModel(calculationResult)

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
      calculationResultsService.calculationResultsViewModel(calculationResult)

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
      calculationResultsService.calculationResultsViewModel(calculationResult)

    val sections: Seq[Seq[RowViewModel]] = viewModel.outDates

    sections.foreach(year => checkYearOutDates(year))
  }

  "in dates should be well formed" in {
    val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

    val viewModel: CalculationResultsViewModel =
      calculationResultsService.calculationResultsViewModel(calculationResult)

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
      calculationResultsService.calculationResultsViewModel(calculationResult)

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
}*/
