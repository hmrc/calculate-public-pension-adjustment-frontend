/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.routes
import forms.annualallowance.taxyear.PensionSchemeInputAmountsFormProvider
import models.{CheckMode, NormalMode, PensionSchemeInputAmounts, Period, SchemeIndex, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.PensionSchemeInputAmountsPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualallowance.taxyear.PensionSchemeInputAmountsView

import scala.concurrent.Future

class PensionSchemeInputAmountsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PensionSchemeInputAmountsFormProvider()
  val form         = formProvider()

  lazy val pensionSchemeInputAmountsRoute =
    controllers.annualallowance.taxyear.routes.PensionSchemeInputAmountsController
      .onPageLoad(NormalMode, Period._2018, SchemeIndex(0))
      .url

  lazy val pensionSchemeInputAmountsCheckRoute =
    controllers.annualallowance.taxyear.routes.PensionSchemeInputAmountsController
      .onPageLoad(CheckMode, Period._2018, SchemeIndex(0))
      .url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      PensionSchemeInputAmountsPage.toString -> Json.obj(
        "originalPIA" -> "value 1",
        "revisedPIA"  -> "value 2"
      )
    )
  )

  val schemeName: String = "Some scheme"

  "PensionSchemeInputAmounts Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeInputAmountsRoute)

        //val view = application.injector.instanceOf[PensionSchemeInputAmountsView]

        val result = route(application, request).value

        status(result) mustEqual OK
//        contentAsString(result) mustEqual view(form, NormalMode, Period._2018, SchemeIndex(0), "Some Scheme")(
//          request,
//          messages(application)
//        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(PensionSchemeInputAmountsPage(Period._2018, SchemeIndex(0)), PensionSchemeInputAmounts(1, 2))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeInputAmountsRoute)

       // val view = application.injector.instanceOf[PensionSchemeInputAmountsView]

        val result = route(application, request).value

        status(result) mustEqual OK
//        contentAsString(result) mustEqual view(
//          form.fill(PensionSchemeInputAmounts(1, 2)),
//          NormalMode,
//          Period._2018,
//          SchemeIndex(0),
//          "Some Scheme"
//        )(
//          request,
//          messages(application)
//        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted in Normal Mode" in {

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
          FakeRequest(POST, pensionSchemeInputAmountsRoute)
            .withFormUrlEncodedBody(("originalPIA", "1"), ("revisedPIA", "2"))

        val userAnswers =
          emptyUserAnswers.set(
            PensionSchemeInputAmountsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeInputAmounts(1, 2)
          )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual PensionSchemeInputAmountsPage(Period._2018, SchemeIndex(0))
          .navigate(NormalMode, userAnswers.get)
          .url
      }
    }

    "must redirect to the next page when valid data is submitted in Check Mode" in {

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
          FakeRequest(POST, pensionSchemeInputAmountsCheckRoute)
            .withFormUrlEncodedBody(("originalPIA", "1"), ("revisedPIA", "2"))

        val userAnswers =
          emptyUserAnswers.set(
            PensionSchemeInputAmountsPage(Period._2018, SchemeIndex(0)),
            PensionSchemeInputAmounts(1, 2)
          )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(Period._2018)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, pensionSchemeInputAmountsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        //val boundForm = form.bind(Map("value" -> "invalid value"))

        //val view = application.injector.instanceOf[PensionSchemeInputAmountsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
//        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2018, SchemeIndex(0), "Some Scheme")(
//          request,
//          messages(application)
//        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, pensionSchemeInputAmountsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, pensionSchemeInputAmountsRoute)
            .withFormUrlEncodedBody(("originalPIA", "value 1"), ("revisedPIA", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
