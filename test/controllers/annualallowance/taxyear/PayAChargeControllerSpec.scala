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
import forms.annualallowance.taxyear.PayAChargeFormProvider
import models.{CheckMode, Done, NormalMode, PensionSchemeDetails, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.{PayAChargePage, PensionSchemeDetailsPage}
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.PayAChargeView

import scala.concurrent.Future

class PayAChargeControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val expectedErrorMessage = "Select yes if you paid an annual allowance tax charge for pension scheme"

  lazy val payAChargeRoute =
    controllers.annualallowance.taxyear.routes.PayAChargeController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  lazy val payACharge2019Route =
    controllers.annualallowance.taxyear.routes.PayAChargeController
      .onPageLoad(NormalMode, Period._2019, SchemeIndex(0))
      .url

  lazy val payAChargeCheckRoute =
    controllers.annualallowance.taxyear.routes.PayAChargeController
      .onPageLoad(CheckMode, Period._2018, SchemeIndex(0))
      .url

  private def formWithMockMessages = {
    val messages = mock[Messages]

    val formProvider = new PayAChargeFormProvider()
    formProvider("")(messages)
  }

  "PayACharge Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeTaxRef"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, payAChargeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PayAChargeView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          formWithMockMessages,
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          "6 April 2017 to 5 April 2018"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(PayAChargePage(Period._2019, SchemeIndex(0)), true)
        .success
        .value
        .set(PensionSchemeDetailsPage(Period._2019, SchemeIndex(0)), PensionSchemeDetails("schemeName", "schemeTaxRef"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, payACharge2019Route)

        val view = application.injector.instanceOf[PayAChargeView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          formWithMockMessages.fill(true),
          NormalMode,
          Period._2019,
          SchemeIndex(0),
          "6 April 2018 to 5 April 2019"
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
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, payAChargeRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, payAChargeRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = formWithMockMessages.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PayAChargeView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(
          boundForm,
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          startEndDate = "6 April 2017 to 5 April 2018"
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
        val request   = FakeRequest(GET, payAChargeRoute)

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
          FakeRequest(POST, payAChargeRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
