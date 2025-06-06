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
import forms.annualallowance.taxyear.PensionSchemeDetailsFormProvider
import models.{Done, NormalMode, PSTR, PensionSchemeDetails, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.{MemberMoreThanOnePensionPage, PensionSchemeDetailsPage, WhichSchemePage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.PensionSchemeDetailsView

import scala.concurrent.Future

class PensionSchemeDetailsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PensionSchemeDetailsFormProvider()
  val form         = formProvider()

  lazy val pensionSchemeDetailsRoute =
    controllers.annualallowance.taxyear.routes.PensionSchemeDetailsController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      PensionSchemeDetailsPage.toString -> Json.obj(
        "schemeName"   -> "value 1",
        "schemeTaxRef" -> "value 2"
      )
    )
  )

  "PensionSchemeDetails Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeDetailsRoute)

        val view = application.injector.instanceOf[PensionSchemeDetailsView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form, NormalMode, Period._2018, SchemeIndex(0))(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET" - {

      "when the user is entering their first period" in {
        val previouslyAnswered = UserAnswers(userAnswersId)
          .set(WhichSchemePage(Period._2018, SchemeIndex(0)), "12345678RL")
          .success
          .value

        val userAnswers = previouslyAnswered
          .set(
            PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")
          )
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, pensionSchemeDetailsRoute)

          val view = application.injector.instanceOf[PensionSchemeDetailsView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            form.fill(PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")),
            NormalMode,
            Period._2018,
            SchemeIndex(0)
          )(
            request,
            messages(application)
          ).toString
        }
      }

      "when the user is entering their second period" in {
        val previouslyAnswered = UserAnswers(userAnswersId)
          .set(MemberMoreThanOnePensionPage(Period._2016), false)
          .success
          .value
          .set(WhichSchemePage(Period._2016, SchemeIndex(0)), "11111111RL")
          .success
          .value
          .set(WhichSchemePage(Period._2018, SchemeIndex(0)), "12345678RL")
          .success
          .value

        val userAnswers = previouslyAnswered
          .set(
            PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")
          )
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, pensionSchemeDetailsRoute)

          val view = application.injector.instanceOf[PensionSchemeDetailsView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            form.fill(PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")),
            NormalMode,
            Period._2018,
            SchemeIndex(0)
          )(
            request,
            messages(application)
          ).toString
        }
      }

      "must populate the view correctly on a GET when previous scheme exists" in {
        val previouslyAnswered = UserAnswers(userAnswersId)
          .set(WhichSchemePage(Period._2018, SchemeIndex(0)), PSTR.New)
          .success
          .value

        val userAnswers = previouslyAnswered
          .set(
            PensionSchemeDetailsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")
          )
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, pensionSchemeDetailsRoute)

          val view = application.injector.instanceOf[PensionSchemeDetailsView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            form.fill(PensionSchemeDetails("someSchemeName", "someSchemeTaxRef")),
            NormalMode,
            Period._2018,
            SchemeIndex(0)
          )(
            request,
            messages(application)
          ).toString
        }
      }

      "must not populate the view on a GET when isNewScheme and no pension scheme details entered" in {
        val previouslyAnswered = UserAnswers(userAnswersId)
          .set(WhichSchemePage(Period._2018, SchemeIndex(0)), PSTR.New)
          .success
          .value

        val userAnswers = previouslyAnswered

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, pensionSchemeDetailsRoute)

          val view = application.injector.instanceOf[PensionSchemeDetailsView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            form,
            NormalMode,
            Period._2018,
            SchemeIndex(0)
          )(
            request,
            messages(application)
          ).toString
        }
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
          FakeRequest(POST, pensionSchemeDetailsRoute)
            .withFormUrlEncodedBody(("schemeName", "value 1"), ("schemeTaxRef", "12345678RL"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, pensionSchemeDetailsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PensionSchemeDetailsView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(boundForm, NormalMode, Period._2018, SchemeIndex(0))(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, pensionSchemeDetailsRoute)

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
          FakeRequest(POST, pensionSchemeDetailsRoute)
            .withFormUrlEncodedBody(("schemeName", "value 1"), ("schemeTaxRef", "value 2"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
