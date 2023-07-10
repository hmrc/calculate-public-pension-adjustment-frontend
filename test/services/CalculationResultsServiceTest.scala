package services

import base.SpecBase
import models.CalculationResults.{CalculationResponse, RowViewModel}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

class CalculationResultsServiceTest extends SpecBase {

  val calculationResultsService: CalculationResultsService = new CalculationResultsService
  val source: String = Source.fromFile("test/resources/CalculationResultsTestData.json").getLines().mkString
  val json: JsValue = Json.parse(source)
  val calculationResult: CalculationResponse = json.as[CalculationResponse]

  "Something" - {
    val viewModel = calculationResultsService.calculationResultsViewModel(calculationResult)
    val something = viewModel.




  }

}
