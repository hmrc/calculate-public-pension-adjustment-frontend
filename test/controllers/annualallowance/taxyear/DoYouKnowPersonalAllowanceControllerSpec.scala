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

package controllers.annualallowance.taxyear

import base.SpecBase
import config.FrontendAppConfig
import forms.annualallowance.taxyear.DoYouKnowPersonalAllowanceFormProvider
import pages.annualallowance.taxyear.{DoYouKnowPersonalAllowancePage, TotalIncomePage}
import models.{Done, NormalMode, Period, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CalculateBackendService, UserDataService}
import views.html.annualallowance.taxyear.DoYouKnowPersonalAllowanceView

import scala.concurrent.Future

class DoYouKnowPersonalAllowanceControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DoYouKnowPersonalAllowanceFormProvider()
  val form         = formProvider()

  lazy val doYouKnowPersonalAllowanceRoute =
    controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController
      .onPageLoad(NormalMode, Period._2018)
      .url

  "DoYouKnowPersonalAllowance Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, doYouKnowPersonalAllowanceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DoYouKnowPersonalAllowanceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2018)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(DoYouKnowPersonalAllowancePage(Period._2018), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, doYouKnowPersonalAllowanceRoute)

        val view = application.injector.instanceOf[DoYouKnowPersonalAllowanceView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, Period._2018)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, doYouKnowPersonalAllowanceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.annualallowance.taxyear.routes.PersonalAllowanceController
          .onPageLoad(NormalMode, Period._2018)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, doYouKnowPersonalAllowanceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DoYouKnowPersonalAllowanceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2018)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, doYouKnowPersonalAllowanceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to to start of the service for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, doYouKnowPersonalAllowanceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to MarriageAllowanceController when basic rate is Charged" in {

      val mockCalculateBackendService = mock[CalculateBackendService]

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      when(mockCalculateBackendService.findTaxRateStatus(any(), any())(any())) thenReturn
        Future.successful(true)

      val userAnswers = emptyUserAnswers
        .set(DoYouKnowPersonalAllowancePage(Period._2018), false)
        .success
        .value
        .set(TotalIncomePage(Period._2018), BigInt("30000"))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[CalculateBackendService].toInstance(mockCalculateBackendService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(
            GET,
            controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController
              .checkBasicRate(Period._2018)
              .url
          )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be(
          s"/public-pension-adjustment/annual-allowance/total-income/marriage-allowance/2018"
        )
      }
    }

    "must redirect to BlindAllowanceController when basic rate is Not Charged" in {

      val mockCalculateBackendService = mock[CalculateBackendService]

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      when(mockCalculateBackendService.findTaxRateStatus(any(), any())(any())) thenReturn
        Future.successful(false)

      val userAnswers = emptyUserAnswers
        .set(DoYouKnowPersonalAllowancePage(Period._2018), false)
        .success
        .value
        .set(TotalIncomePage(Period._2018), BigInt("70000"))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[CalculateBackendService].toInstance(mockCalculateBackendService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(
            GET,
            controllers.annualallowance.taxyear.routes.DoYouKnowPersonalAllowanceController
              .checkBasicRate(Period._2018)
              .url
          )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get must be(
          s"/public-pension-adjustment/annual-allowance/total-income/blind-person-allowance/2018"
        )
      }
    }
  }
}
