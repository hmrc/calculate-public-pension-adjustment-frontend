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
import forms.lifetimeallowance.LtaPensionSchemeDetailsFormProvider
import controllers.lifetimeallowance.{routes => ltaRoutes}
import models.{LtaPensionSchemeDetails, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.lifetimeallowance.LtaPensionSchemeDetailsPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.lifetimeallowance.LtaPensionSchemeDetailsView

import scala.concurrent.Future

class LtaPensionSchemeDetailsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val normalRoute = ltaRoutes.LtaPensionSchemeDetailsController.onPageLoad(NormalMode).url

  val formProvider = new LtaPensionSchemeDetailsFormProvider()
  val form         = formProvider()

  lazy val ltaPensionSchemeDetailsRoute = routes.LtaPensionSchemeDetailsController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      LtaPensionSchemeDetailsPage.toString -> Json.obj(
        "name"   -> "value1",
        "taxRef" -> "00348916RL"
      )
    )
  )

  "LtaPensionSchemeDetails Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[LtaPensionSchemeDetailsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(
          LtaPensionSchemeDetailsPage,
          LtaPensionSchemeDetails("someSchemeName", "00348916RK")
        )
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, normalRoute)

        val view = application.injector.instanceOf[LtaPensionSchemeDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(LtaPensionSchemeDetails("someSchemeName", "00348916RK")),
          NormalMode
        )(request, messages(application)).toString
      }
    }

    "must redirect to the next page when submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, normalRoute)
            .withFormUrlEncodedBody(("name", "scheme name"), ("taxRef", "00348916RK"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad().url
      }
    }

    "must return a Bad Request and errors when invalid data 00348916RT is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, normalRoute)
            .withFormUrlEncodedBody(("value", "00348916RT"))

        val boundForm = form.bind(Map("value" -> "00348916RT"))

        val view = application.injector.instanceOf[LtaPensionSchemeDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, normalRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[LtaPensionSchemeDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, normalRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to start of the service for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, normalRoute)
            .withFormUrlEncodedBody(("Name", "value 1"), ("Reference", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
