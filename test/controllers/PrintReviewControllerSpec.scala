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

package controllers

import base.SpecBase
import models.CalculationResults.{CalculationResponse, CalculationReviewIndividualAAViewModel, IndividualAASummaryModel, RowViewModel}
import models.Period
import models.tasklist.sections.LTASection
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CalculationResultService
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.govuk.all.SummaryListViewModel

import scala.concurrent.Future
import scala.io.Source

class PrintReviewControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val hc: HeaderCarrier = HeaderCarrier()

  lazy val normalRoute = routes.PrintReviewController.onPageLoad().url

  "PrintReviewController" - {

    "must show the print review view on a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockRowViewModel             = RowViewModel("test", "test")
      val mockCalculationResultService = mock[CalculationResultService]
      val mockPrintReviewViewModel     =
        CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
      val list                         = SummaryListViewModel(Seq.empty)
      val mockOutDatesSummary          =
        IndividualAASummaryModel(Period._2017, -10, 10, "Reduced", 10, 10, 10, 10, 20)
      val mockInDatesSummary           =
        IndividualAASummaryModel(Period._2022, -10, 10, "Reduced", 10, 10, 10, 10, 20)

      val userAnswers = LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)

      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

      when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
        .thenReturn(Future.successful(mockPrintReviewViewModel))

      when(mockCalculationResultService.outDatesSummary(any))
        .thenReturn(Seq(mockOutDatesSummary))

      when(mockCalculationResultService.inDatesSummary(any))
        .thenReturn(Seq(mockInDatesSummary))

      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            inject.bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Print calculation results") mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) mustBe true
      }
    }

    "when only out of dates AA, only show content relevant for out dates" in {
      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsOutDatesTestData.json")

      val mockRowViewModel             = RowViewModel("test", "test")
      val mockCalculationResultService = mock[CalculationResultService]
      val mockPrintReviewViewModel     =
        CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
      val list                         = SummaryListViewModel(Seq.empty)
      val mockOutDatesSummary          =
        IndividualAASummaryModel(Period._2017, -10, 10, "Reduced", 10, 10, 10, 10, 20)

      val userAnswers = LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)

      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

      when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
        .thenReturn(Future.successful(mockPrintReviewViewModel))

      when(mockCalculationResultService.outDatesSummary(any))
        .thenReturn(Seq(mockOutDatesSummary))

      when(mockCalculationResultService.inDatesSummary(any))
        .thenReturn(Seq())

      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            inject.bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Print calculation results") mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) mustBe false
      }
    }

    "when LTA reported, should so content relevant for LTA" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockRowViewModel             = RowViewModel("test", "test")
      val mockCalculationResultService = mock[CalculationResultService]
      val mockPrintReviewViewModel     =
        CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
      val list                         = SummaryListViewModel(Seq.empty)
      val mockOutDatesSummary          =
        IndividualAASummaryModel(Period._2017, -10, 10, "Reduced", 10, 10, 10, 10, 20)
      val mockInDatesSummary           =
        IndividualAASummaryModel(Period._2022, -10, 10, "Reduced", 10, 10, 10, 10, 20)

      val userAnswers = LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)

      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

      when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
        .thenReturn(Future.successful(mockPrintReviewViewModel))

      when(mockCalculationResultService.outDatesSummary(any))
        .thenReturn(Seq(mockOutDatesSummary))

      when(mockCalculationResultService.inDatesSummary(any))
        .thenReturn(Seq(mockInDatesSummary))

      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            inject.bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Print calculation results") mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) mustBe true
        contentAsString(result).contains("Lifetime allowance answers") mustBe true
      }
    }

    "when no LTA reported, should not show content for LTA" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockRowViewModel             = RowViewModel("test", "test")
      val mockCalculationResultService = mock[CalculationResultService]
      val mockPrintReviewViewModel     =
        CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
      val list                         = SummaryListViewModel(Seq.empty)
      val mockOutDatesSummary          =
        IndividualAASummaryModel(Period._2017, -10, 10, "Reduced", 10, 10, 10, 10, 20)
      val mockInDatesSummary           =
        IndividualAASummaryModel(Period._2022, -10, 10, "Reduced", 10, 10, 10, 10, 20)

      val userAnswers = emptyUserAnswers

      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

      when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
        .thenReturn(Future.successful(mockPrintReviewViewModel))

      when(mockCalculationResultService.outDatesSummary(any))
        .thenReturn(Seq(mockOutDatesSummary))

      when(mockCalculationResultService.inDatesSummary(any))
        .thenReturn(Seq(mockInDatesSummary))

      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            inject.bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Print calculation results") mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) mustBe true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) mustBe true
        contentAsString(result).contains("Lifetime allowance answers") mustBe false
      }
    }

    def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
      val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
      val json: JsValue  = Json.parse(source)
      json.as[CalculationResponse]
    }
  }
}
