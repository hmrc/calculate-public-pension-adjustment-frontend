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
import forms.annualallowance.taxyear.DefinedContribution2016PreAmountFormProvider
import models.{Done, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContribution2016PreAmountPage, DefinedContribution2016PreFlexiAmountPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.DefinedContribution2016PreAmountView

import java.time.LocalDate
import scala.concurrent.Future

class DefinedContribution2016PreAmountControllerSpec extends SpecBase with MockitoSugar {

  val formProvider           = new DefinedContribution2016PreAmountFormProvider()
  val form                   = formProvider(Seq("6 April 2015 and 8 July 2015"))
  val flexiForm              = formProvider(Seq("6 April 2015 and 1 July 2015"))
  val flexiFormStartOfPeriod = formProvider(Seq("6 April 2015 and 6 April 2015"))

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt("0")

  lazy val definedContribution2016PreAmountRoute =
    controllers.annualallowance.taxyear.routes.DefinedContribution2016PreAmountController.onPageLoad(NormalMode).url

  "DefinedContribution2016PreAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PreAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form,
          NormalMode,
          "6 April 2015 and 8 July 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return OK and the correct view when user indicated flexi year for a GET" in {

      val validDate = LocalDate.of(2015, 7, 1)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PreAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          flexiForm,
          NormalMode,
          "6 April 2015 and 1 July 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return OK and the correct view when user indicated flexi year for a GET when flexi date first day of sub-period" in {

      val validDate = LocalDate.of(2015, 4, 6)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PreAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          flexiForm,
          NormalMode,
          "6 April 2015 and 6 April 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate flexi year amount answer for period with 0 when user has entered flexi date as last date of sub-period" in {
      val validDate = LocalDate.of(2015, 7, 8)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val mockUserDataService = mock[UserDataService]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PreAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(DefinedContribution2016PreFlexiAmountPage) mustBe Some(BigInt(0))
      }
    }

    "must not populate flexi year amount answer for period with 0 when user has entered flexi date as any other date than last day of period" in {
      val validDate = LocalDate.of(2015, 7, 7)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val mockUserDataService = mock[UserDataService]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PreAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(DefinedContribution2016PreFlexiAmountPage) mustBe None
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(DefinedContribution2016PreAmountPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreAmountRoute)

        val view = application.injector.instanceOf[DefinedContribution2016PreAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form.fill(validAnswer),
          NormalMode,
          "6 April 2015 and 8 July 2015"
        )(
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
          FakeRequest(POST, definedContribution2016PreAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PreAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DefinedContribution2016PreAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(
          boundForm,
          NormalMode,
          "6 April 2015 and 8 July 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, definedContribution2016PreAmountRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }

    "must redirect to start of the service for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, definedContribution2016PreAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
