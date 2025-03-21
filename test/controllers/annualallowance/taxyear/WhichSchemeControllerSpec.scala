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
import forms.annualallowance.taxyear.WhichSchemeFormProvider
import models.{Done, NormalMode, PSTR, PensionSchemeDetails, Period, SchemeIndex, UserAnswers, WhichScheme}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, WhichSchemePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.WhichSchemeView

import scala.concurrent.Future

class WhichSchemeControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichSchemeRoute =
    controllers.annualallowance.taxyear.routes.WhichSchemeController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  val formProvider = new WhichSchemeFormProvider()
  val form         = formProvider()
  val whichScheme  = WhichScheme(Seq("12345678RL", PSTR.New))

  "WhichScheme Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers =
        emptyUserAnswers
          .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "12345678RL"))
          .success
          .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichSchemeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichSchemeView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form, NormalMode, Period._2018, SchemeIndex(0), whichScheme)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val previouslyAnswered = UserAnswers(userAnswersId)
        .set(WhichSchemePage(Period._2018, SchemeIndex(0)), whichScheme.values.head)
        .success
        .value

      val userAnswers =
        previouslyAnswered
          .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "12345678RL"))
          .success
          .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichSchemeRoute)

        val view: WhichSchemeView = application.injector.instanceOf[WhichSchemeView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form.fill(whichScheme.values.head),
          NormalMode,
          Period._2018,
          SchemeIndex(0),
          whichScheme
        )(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)

      val userAnswers: UserAnswers =
        emptyUserAnswers

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whichSchemeRoute)
            .withFormUrlEncodedBody(("value", whichScheme.values.head))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers =
        emptyUserAnswers
          .set(PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)), PensionSchemeDetails("schemeName", "12345678RL"))
          .success
          .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichSchemeRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhichSchemeView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(boundForm, NormalMode, Period._2018, SchemeIndex(0), whichScheme)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, whichSchemeRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }

    "redirect to start of the service for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, whichSchemeRoute)
            .withFormUrlEncodedBody(("value", whichScheme.values.head.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
