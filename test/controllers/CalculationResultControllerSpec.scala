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
import models.CalculationResults.CalculationResponse
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

class CalculationResultControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val hc: HeaderCarrier = HeaderCarrier()

  lazy val normalRoute = routes.CalculationResultController.onPageLoad().url

  val dynamicDebit: String                     =
    "You have extra tax charges to pay, you will receive a notice by post."
  val dynamicCredit: String                    =
    "You are due a refund for tax charges, HMRC will pay this using the bank details you provide on your adjustment."
  val dynamicCompensation: String              =
    "You are due compensation, HMRC will review your information and pass it to your pension scheme. They will then:"
  val notAuthenticated: String                 =
    "If you do not sign in to your Government Gateway, your results will not be saved and you will need to complete the calculator again."
  val dynamicNoAAChargeOrLTA: String           =
    "As there is no change in your annual allowance tax position you do not need to make a submission."
  val dynamicNoAAChargeHasLTA: String          =
    "There is no change in your annual allowance tax position, but you must still submit your answers to report a change in your lifetime allowance position."
  val dynamicNextStepsAuthenticated: String    =
    "To submit the information, you will need to provide:"
  val dynamicNextStepsNotAuthenticated: String =
    "To submit the information, you will need sign in to your Government Gateway account and provide:"

  "CalculationResult Controller" - {

    "must show the calculation results view on a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationResultsViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
      }
    }

    "must redirect to submit landing page on a POST when answers / calculation are submitted to backend successfully" in {

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any, any)(any))
        .thenReturn(Future.successful(Success("123")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value.endsWith(
          "/submit-public-pension-adjustment/landing-page?submissionUniqueId=123"
        ) mustBe true
      }
    }

    "must redirect to journey recovery on a POST when answers / calculation submission fails" in {

      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any, any)(any))
        .thenReturn(Future.successful(Failure(Seq("someError"))))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe "/public-pension-adjustment/there-is-a-problem"
      }
    }

    "authenticated" - {

      "must display correct dynamic content when compensation, credit, debit are greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataAllTotals.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = true)

        status(result) mustEqual OK
        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe true
        contentAsString(result).contains(dynamicCredit) mustBe true
        contentAsString(result).contains(dynamicCompensation) mustBe true
        contentAsString(result).contains("Save and continue") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe false

      }

      "must display correct dynamic content when only credit is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataCredit.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = true)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe true
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Save and continue") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must display correct dynamic content when only debit is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataDebit.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = true)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe true
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Save and continue") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must display correct dynamic content when only compensation is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = true)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe true
        contentAsString(result).contains("Save and continue") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must not display dynamic content when no totals are greater than 0 and hide continue button and no LTA" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = true)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Save and continue") mustBe false
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must display dynamic content when no totals are greater than 0 and not hide continue button when LTA" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplicationWithLTACompleted(
          calculationResult,
          authenticatedStatus = true,
          kickedOut = false
        )

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeHasLTA) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Save and continue") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must display dynamic content when no totals are greater than 0 and hide continue button and LTA kicked out" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplicationWithLTACompleted(
          calculationResult,
          authenticatedStatus = true,
          kickedOut = true
        )

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe true
        contentAsString(result).contains(dynamicNoAAChargeHasLTA) mustBe false
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Save and continue") mustBe false
        contentAsString(result).contains(notAuthenticated) mustBe false
      }
    }

    "not authenticated" - {

      "must display correct dynamic content when compensation, credit, debit are greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataAllTotals.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = false)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe true
        contentAsString(result).contains(dynamicCredit) mustBe true
        contentAsString(result).contains(dynamicCompensation) mustBe true
        contentAsString(result).contains("Continue to sign in") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe true

      }

      "must display correct dynamic content when only credit is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataCredit.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = false)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe true
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Continue to sign in") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe true
      }

      "must display correct dynamic content when only debit is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataDebit.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = false)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe true
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Continue to sign in") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe true
      }

      "must display correct dynamic content when only compensation is greater than 0" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestData.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = false)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe true
        contentAsString(result).contains("Continue to sign in") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe true
      }

      "must not display dynamic content when no totals are greater than 0 and hide continue button and no LTA" in {
        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplication(calculationResult, authenticatedStatus = false)

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Continue to sign in") mustBe false
        contentAsString(result).contains(notAuthenticated) mustBe false
      }

      "must display dynamic content when no totals are greater than 0 and hide continue button and LTA" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplicationWithLTACompleted(
          calculationResult,
          authenticatedStatus = false,
          kickedOut = false
        )

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeHasLTA) mustBe true
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Continue to sign in") mustBe true
        contentAsString(result).contains(notAuthenticated) mustBe true
      }

      "must display dynamic content when no totals are greater than 0 and hide continue button and LTA kicked out" in {

        val calculationResult: CalculationResponse =
          readCalculationResult("test/resources/CalculationResultsTestDataNoTotals.json")

        val result = returnResultFromBuiltApplicationWithLTACompleted(
          calculationResult,
          authenticatedStatus = false,
          kickedOut = true
        )

        status(result) mustEqual OK

        contentAsString(result).contains(dynamicNextStepsNotAuthenticated) mustBe false
        contentAsString(result).contains(dynamicNoAAChargeOrLTA) mustBe true
        contentAsString(result).contains(dynamicNoAAChargeHasLTA) mustBe false
        contentAsString(result).contains(dynamicDebit) mustBe false
        contentAsString(result).contains(dynamicCredit) mustBe false
        contentAsString(result).contains(dynamicCompensation) mustBe false
        contentAsString(result).contains("Continue to sign in") mustBe false
        contentAsString(result).contains(notAuthenticated) mustBe false
      }
    }

    def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
      val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
      val json: JsValue  = Json.parse(source)
      json.as[CalculationResponse]
    }

    def returnResultFromBuiltApplication(
      calculationResult: CalculationResponse,
      authenticatedStatus: Boolean
    ): Future[Result] = {
      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationResultsViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.copy(authenticated = authenticatedStatus)))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        route(application, request).value
      }
    }

    def returnResultFromBuiltApplicationWithLTACompleted(
      calculationResult: CalculationResponse,
      authenticatedStatus: Boolean,
      kickedOut: Boolean
    ): Future[Result] = {
      val mockCalculationResultService = mock[CalculationResultService]
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationResultsViewModel(any)).thenCallRealMethod()

      val userAnswers =
        if (!kickedOut) {
          LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)
        } else {
          LTASection.saveNavigation(emptyUserAnswers, cannotUseLtaServiceNoChargePage.url)
        }

      val application =
        applicationBuilder(userAnswers = Some(userAnswers.copy(authenticated = authenticatedStatus)))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        route(application, request).value
      }
    }
  }
}
