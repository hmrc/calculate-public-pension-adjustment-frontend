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
import config.FrontendAppConfig
import models.CalculationResults.CalculationResponse
import models.submission.{Failure, Success}
import models.tasklist.SectionStatus.{Completed, NotStarted}
import models.tasklist.sections.LTASection
import models.tasklist.{SectionGroupViewModel, SectionViewModel, TaskListViewModel}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CalculationResultService, TaskListService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.io.Source

class CalculationReviewControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val hc: HeaderCarrier = HeaderCarrier()

  lazy val normalRoute = routes.CalculationReviewController.onPageLoad().url

  "CalculationReview Controller" - {

    def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
      val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
      val json: JsValue  = Json.parse(source)
      json.as[CalculationResponse]
    }

    "must show the calculation results view on a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
      }
    }

    "must redirect to outstanding tasks page on a GET if next steps section is not ready to start" in {

      val application = applicationBuilder(Some(emptyUserAnswers)).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, normalRoute)

        val result = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToOutstandingTasksPage
      }
    }

    "must redirect to outstanding tasks page on a POST if next steps section is not ready to start" in {

      val application = applicationBuilder(Some(emptyUserAnswers)).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(POST, normalRoute)

        val result = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToOutstandingTasksPage

      }
    }

    "when only out of dates AA, only show content relevant for out dates" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsOutDatesTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result).contains("Calculation results") mustBe true
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

      val mockCalculationResultService = mock[CalculationResultService]

      val userAnswers = LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)

      val mockTaskListService = mock[TaskListService]
      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )

      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
        contentAsString(result).contains("Lifetime allowance") mustBe true
      }
    }

    "when no LTA reported, should not show content for LTA" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
        contentAsString(result).contains("Lifetime allowance") mustBe false
      }

    }

    "must contain right data for totalCharge in all the valid FY" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val mockCalculationResultService = mock[CalculationResultService]
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.sendRequest(any)(any)).thenReturn(Future.successful(calculationResult))
      when(mockCalculationResultService.calculationReviewViewModel(any)).thenCallRealMethod()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK

        val document    = Jsoup.parse(contentAsString(result))
        val table       = document.select("table").first()
        val columnCells = table.select("tbody tr td:nth-child(2)")
        val columnTexts = columnCells.eachText()
        columnTexts should contain("No tax to pay")
        columnTexts should contain("Decreased by £1,200")
      }

    }

    "must redirect to submit landing page on a POST when answers / calculation are submitted to backend successfully" in {

      val mockCalculationResultService = mock[CalculationResultService]
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any, any)(any))
        .thenReturn(Future.successful(Success("123")))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
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
      val mockTaskListService          = mock[TaskListService]

      when(mockTaskListService.taskListViewModel(any())).thenReturn(
        TaskListViewModel(
          SectionGroupViewModel("", Seq(SectionViewModel("", "", Completed, "", None))),
          None,
          None,
          SectionGroupViewModel("", Seq(SectionViewModel("", "", NotStarted, "", None)))
        )
      )
      when(mockCalculationResultService.submitUserAnswersAndCalculation(any, any)(any))
        .thenReturn(Future.successful(Failure(Seq("someError"))))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CalculationResultService].toInstance(mockCalculationResultService),
            bind[TaskListService].toInstance(mockTaskListService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, normalRoute).withFormUrlEncodedBody()

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe "/public-pension-adjustment/there-is-a-problem"
      }
    }

  }
}
