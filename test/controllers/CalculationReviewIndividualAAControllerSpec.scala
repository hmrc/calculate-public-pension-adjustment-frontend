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
import models.submission.{Failure, Success}
import models.tasklist.sections.LTASection
import models.tasklist.sections.LTASection.cannotUseLtaServiceNoChargePage
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, status, _}
import services.CalculationResultService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.io.Source

class CalculationReviewIndividualAAControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val hc: HeaderCarrier = HeaderCarrier()

  lazy val normalRouteOutDate = routes.CalculationReviewIndividualAAController.onPageLoad(Period._2016).url
  lazy val normalRouteInDate  = routes.CalculationReviewIndividualAAController.onPageLoad(Period._2022).url
  lazy val submitRoute        = routes.CalculationReviewIndividualAAController.onSubmit().url

  "CalculationReviewIndividualAA Controller" - {

    "must show the calculation review individual AA view on a GET for out date" - {

      "when decrease in tax charge" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val mockRowViewModel                           = RowViewModel("test", "test")
        val mockCalculationResultService               = mock[CalculationResultService]
        val mockCalculationReviewIndividualAAViewModel =
          CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
        val mockIndividualAASummaryModel               =
          IndividualAASummaryModel(
            Period._2016,
            10,
            10,
            "calculationReviewIndividualAA.changeInTaxChargeString.decrease.",
            10,
            1,
            1,
            10,
            20,
            None
          )

        when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

        when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
          .thenReturn(Future.successful(mockCalculationReviewIndividualAAViewModel))

        when(mockCalculationResultService.individualAASummaryModel(calculationResult))
          .thenReturn(Seq(mockIndividualAASummaryModel))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[CalculationResultService].toInstance(mockCalculationResultService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, normalRouteOutDate)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result).contains("Calculation review for individual annual allowance") mustBe true
          contentAsString(result).contains("Your annual allowance tax charge has reduced by £10") mustBe true
        }
      }

      "when no change in tax charge" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val mockRowViewModel                           = RowViewModel("test", "test")
        val mockCalculationResultService               = mock[CalculationResultService]
        val mockCalculationReviewIndividualAAViewModel =
          CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
        val mockIndividualAASummaryModel               =
          IndividualAASummaryModel(
            Period._2016,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            10,
            1,
            1,
            10,
            20,
            Some(8)
          )

        when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

        when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
          .thenReturn(Future.successful(mockCalculationReviewIndividualAAViewModel))

        when(mockCalculationResultService.individualAASummaryModel(calculationResult))
          .thenReturn(Seq(mockIndividualAASummaryModel))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[CalculationResultService].toInstance(mockCalculationResultService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, normalRouteOutDate)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result).contains("Calculation review for individual annual allowance") mustBe true
          contentAsString(result).contains("You have no annual allowance tax charge to pay") mustBe true
          contentAsString(result).contains(
            "You do not need to pay the £8 increase in tax charge, as it is written off for this year."
          ) mustBe true
        }
      }
    }

    "must show the calculation review individual AA view on a GET for in date" - {

      "when increase in tax charge" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val mockRowViewModel                           = RowViewModel("test", "test")
        val mockCalculationResultService               = mock[CalculationResultService]
        val mockCalculationReviewIndividualAAViewModel =
          CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
        val mockIndividualAASummaryModel               =
          IndividualAASummaryModel(
            Period._2022,
            10,
            -10,
            "calculationReviewIndividualAA.changeInTaxChargeString.increase.",
            10,
            10,
            10,
            10,
            20,
            None
          )

        when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

        when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
          .thenReturn(Future.successful(mockCalculationReviewIndividualAAViewModel))

        when(mockCalculationResultService.individualAASummaryModel(calculationResult))
          .thenReturn(Seq(mockIndividualAASummaryModel))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[CalculationResultService].toInstance(mockCalculationResultService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, normalRouteInDate)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result).contains("Calculation review for individual annual allowance") mustBe true
          contentAsString(result).contains("You must pay £10") mustBe true
        }
      }

      "when decrease in tax charge" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val mockRowViewModel                           = RowViewModel("test", "test")
        val mockCalculationResultService               = mock[CalculationResultService]
        val mockCalculationReviewIndividualAAViewModel =
          CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
        val mockIndividualAASummaryModel               =
          IndividualAASummaryModel(
            Period._2022,
            10,
            10,
            "calculationReviewIndividualAA.changeInTaxChargeString.decrease.",
            10,
            10,
            10,
            10,
            20,
            None
          )

        when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

        when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
          .thenReturn(Future.successful(mockCalculationReviewIndividualAAViewModel))

        when(mockCalculationResultService.individualAASummaryModel(calculationResult))
          .thenReturn(Seq(mockIndividualAASummaryModel))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[CalculationResultService].toInstance(mockCalculationResultService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, normalRouteInDate)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result).contains("Calculation review for individual annual allowance") mustBe true
          contentAsString(result).contains("Your annual allowance tax charge has reduced by £10") mustBe true
        }
      }

      "when no change in tax charge" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val mockRowViewModel                           = RowViewModel("test", "test")
        val mockCalculationResultService               = mock[CalculationResultService]
        val mockCalculationReviewIndividualAAViewModel =
          CalculationReviewIndividualAAViewModel(Seq(Seq(mockRowViewModel)), Seq(Seq(mockRowViewModel)))
        val mockIndividualAASummaryModel               =
          IndividualAASummaryModel(
            Period._2022,
            0,
            0,
            "calculationReviewIndividualAA.changeInTaxChargeString.noChange.",
            10,
            10,
            0,
            0,
            20,
            None
          )

        when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))

        when(mockCalculationResultService.calculationReviewIndividualAAViewModel(any, any, any)(any, any))
          .thenReturn(Future.successful(mockCalculationReviewIndividualAAViewModel))

        when(mockCalculationResultService.individualAASummaryModel(calculationResult))
          .thenReturn(Seq(mockIndividualAASummaryModel))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[CalculationResultService].toInstance(mockCalculationResultService)
            )
            .build()

        running(application) {
          val request = FakeRequest(GET, normalRouteInDate)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result).contains("Calculation review for individual annual allowance") mustBe true
          contentAsString(result).contains("You have no annual allowance tax charge to pay") mustBe true
        }
      }
    }

    "must redirect to the CalculationReviewController on a POST" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, submitRoute)
            .withFormUrlEncodedBody("_" -> "")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CalculationReviewController.onPageLoad().url
      }
    }

    def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
      val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
      val json: JsValue  = Json.parse(source)
      json.as[CalculationResponse]
    }
  }
}
