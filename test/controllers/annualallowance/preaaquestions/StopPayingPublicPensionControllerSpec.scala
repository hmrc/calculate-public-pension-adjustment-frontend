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

package controllers.annualallowance.preaaquestions

import java.time.LocalDate

import base.SpecBase
import controllers.routes
import controllers.annualallowance.preaaquestions.{routes => preAARoutes}
import forms.annualallowance.preaaquestions.StopPayingPublicPensionFormProvider
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualallowance.preaaquestions.StopPayingPublicPensionView

import scala.concurrent.Future

class StopPayingPublicPensionControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new StopPayingPublicPensionFormProvider()
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: LocalDate = LocalDate.of(2015, 4, 6)

  lazy val NormalRoute = preAARoutes.StopPayingPublicPensionController.onPageLoad(NormalMode).url
  lazy val CheckRoute  = preAARoutes.StopPayingPublicPensionController.onPageLoad(CheckMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  def getRequest(mode: Mode): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, preAARoutes.StopPayingPublicPensionController.onPageLoad(mode).url)

  def postRequest(mode: Mode): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, preAARoutes.StopPayingPublicPensionController.onPageLoad(mode).url)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "StopPayingPublicPension Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, getRequest(NormalMode)).value

        val view = application.injector.instanceOf[StopPayingPublicPensionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(getRequest(NormalMode), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(StopPayingPublicPensionPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val view = application.injector.instanceOf[StopPayingPublicPensionView]

        val result = route(application, getRequest(NormalMode)).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(
          getRequest(NormalMode),
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted in normal mode" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val result = route(application, postRequest(NormalMode)).value

        val expectedAnswers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 4, 6)).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual StopPayingPublicPensionPage
          .navigate(NormalMode, expectedAnswers)
          .url // Change to appropriate page upon implementation
      }
    }

    "must redirect to the next page when valid data is submitted in check mode" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val result = route(application, postRequest(CheckMode)).value

        val expectedAnswers = emptyUserAnswers.set(StopPayingPublicPensionPage, LocalDate.of(2015, 4, 6)).success.value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual StopPayingPublicPensionPage
          .navigate(CheckMode, expectedAnswers)
          .url // Change to appropriate page upon implementation
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, NormalRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[StopPayingPublicPensionView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, getRequest(NormalMode)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, postRequest(NormalMode)).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
