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

package controllers.lifetimeallowance

import base.SpecBase
import config.FrontendAppConfig
import controllers.lifetimeallowance.routes as ltaRoutes
import forms.lifetimeallowance.WhoPaidLTAChargeFormProvider
import models.{Done, NormalMode, UserAnswers, WhoPaidLTACharge}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.lifetimeallowance.WhoPaidLTAChargePage
import play.api
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, contentAsString, defaultAwaitTimeout, redirectLocation, route, running, status, writeableOf_AnyContentAsEmpty, writeableOf_AnyContentAsFormUrlEncoded}
import services.UserDataService
import views.html.lifetimeallowance.WhoPaidLTAChargeView

import scala.concurrent.Future

class WhoPaidLTAChargeControllerSpec extends SpecBase with MockitoSugar {
  def onwardRoute = Call("GET", "/foo")

  lazy val normalRoute = ltaRoutes.WhoPaidLTAChargeController.onPageLoad(NormalMode).url

  val formProvider = new WhoPaidLTAChargeFormProvider()
  val form         = formProvider()

  "WhoPaidExtraLtaCharge Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhoPaidLTAChargeView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form, NormalMode)(request, messages(application)).toString
      }
    }
  }

  "must redirect to the next page when valid data is submitted" in {

    val mockUserDataService = mock[UserDataService]

    when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

    val application =
      applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          api.inject.bind[UserDataService].toInstance(mockUserDataService)
        )
        .build()

    running(application) {
      val request =
        FakeRequest(POST, normalRoute)
          .withFormUrlEncodedBody(("value", WhoPaidLTACharge.values.head.toString))

      val result = route(application, request).value

      status(result) `mustEqual` SEE_OTHER
    }
  }

  "must populate the view correctly on a GET when the question has previously been answered" in {

    val userAnswers =
      UserAnswers(userAnswersId).set(WhoPaidLTAChargePage, WhoPaidLTACharge.values.head).success.value

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    running(application) {
      val request = FakeRequest(GET, normalRoute)

      val view = application.injector.instanceOf[WhoPaidLTAChargeView]

      val result = route(application, request).value

      status(result) `mustEqual` OK
      contentAsString(result) `mustEqual` view(form.fill(WhoPaidLTACharge.values.head), NormalMode)(
        request,
        messages(application)
      ).toString
    }
  }

  "must return a Bad Request and errors when invalid data is submitted" in {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    running(application) {
      val request =
        FakeRequest(POST, normalRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhoPaidLTAChargeView]

      val result = route(application, request).value

      status(result) `mustEqual` BAD_REQUEST
      contentAsString(result) `mustEqual` view(boundForm, NormalMode)(request, messages(application)).toString
    }
  }

  "must redirect to start of service for a GET if no existing data is found" in {

    val application = applicationBuilder(userAnswers = None).build()

    running(application) {
      val appConfig = application.injector.instanceOf[FrontendAppConfig]
      val request   = FakeRequest(GET, normalRoute)

      val result = route(application, request).value

      status(result) `mustEqual` SEE_OTHER
      redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
    }
  }

  "redirect to start of service for a POST if no existing data is found" in {

    val application = applicationBuilder(userAnswers = None).build()

    running(application) {
      val appConfig = application.injector.instanceOf[FrontendAppConfig]
      val request   =
        FakeRequest(POST, normalRoute)
          .withFormUrlEncodedBody(("value", WhoPaidLTACharge.values.head.toString))

      val result = route(application, request).value

      status(result) `mustEqual` SEE_OTHER
      redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
    }
  }
}
