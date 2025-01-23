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
import forms.annualallowance.taxyear.PensionSchemeInput2016preAmountsFormProvider
import models.{Done, NormalMode, PensionSchemeInput2016preAmounts, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PensionSchemeInput2016preAmountsPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.PensionSchemeInput2016preAmountsView

import scala.concurrent.Future

class PensionSchemeInput2016preAmountsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PensionSchemeInput2016preAmountsFormProvider()
  val form         = formProvider("6 April 2015 and 8 July 2015")

  lazy val pensionSchemeInput2016preAmountsRoute =
    controllers.annualallowance.taxyear.routes.PensionSchemeInput2016preAmountsController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      PensionSchemeInput2016preAmountsPage.toString -> Json.obj(
        "revisedPIA" -> "value 2"
      ),
      PensionSchemeDetailsPage.toString             -> Json.obj(
        "schemeName"   -> "Some Scheme",
        "schemeTaxRef" -> "12345678KL"
      )
    )
  )

  val schemeName: String = "Some scheme"

  "PensionSchemeInput2016preAmounts Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeInput2016preAmountsRoute)

        val view = application.injector.instanceOf[PensionSchemeInput2016preAmountsView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form, NormalMode, Period._2018, SchemeIndex(0), "")(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(PensionSchemeInput2016preAmountsPage(Period._2018, SchemeIndex(0)), PensionSchemeInput2016preAmounts(2))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeInput2016preAmountsRoute)

        val view = application.injector.instanceOf[PensionSchemeInput2016preAmountsView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form.fill(PensionSchemeInput2016preAmounts(2)),
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          ""
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, pensionSchemeInput2016preAmountsRoute)
            .withFormUrlEncodedBody(("revisedPIA", "2"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, pensionSchemeInput2016preAmountsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PensionSchemeInput2016preAmountsView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(boundForm, NormalMode, Period._2018, SchemeIndex(0), "")(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, pensionSchemeInput2016preAmountsRoute)

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
          FakeRequest(POST, pensionSchemeInput2016preAmountsRoute)
            .withFormUrlEncodedBody(("revisedPIA", "value 2"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
