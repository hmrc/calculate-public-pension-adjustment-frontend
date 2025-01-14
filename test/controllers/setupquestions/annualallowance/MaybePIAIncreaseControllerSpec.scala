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

package controllers.setupquestions.annualallowance

import base.SpecBase
import config.FrontendAppConfig
import forms.setupquestions.annualallowance.MaybePIAIncreaseFormProvider
import models.{AAKickOutStatus, Done, MaybePIAIncrease, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.setupquestions.annualallowance.MaybePIAIncreasePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.setupquestions.annualallowance.MaybePIAIncreaseView

import scala.concurrent.Future

class MaybePIAIncreaseControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val maybePIAIncreaseRoute = routes.MaybePIAIncreaseController.onPageLoad(NormalMode).url

  val formProvider = new MaybePIAIncreaseFormProvider()
  val form         = formProvider()

  "MaybePIAIncrease Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, maybePIAIncreaseRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MaybePIAIncreaseView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(MaybePIAIncreasePage, MaybePIAIncrease.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, maybePIAIncreaseRoute)

        val view = application.injector.instanceOf[MaybePIAIncreaseView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(MaybePIAIncrease.values.head), NormalMode)(
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
          FakeRequest(POST, maybePIAIncreaseRoute)
            .withFormUrlEncodedBody(("value", MaybePIAIncrease.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, maybePIAIncreaseRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[MaybePIAIncreaseView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, maybePIAIncreaseRoute)

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
          FakeRequest(POST, maybePIAIncreaseRoute)
            .withFormUrlEncodedBody(("value", MaybePIAIncrease.Yes.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "aaKickOutStatus" - {

      "must set aaKickOutStatus to 2 if yes" in {

        val mockUserDataService = mock[UserDataService]

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, maybePIAIncreaseRoute)
            .withFormUrlEncodedBody(("value", MaybePIAIncrease.Yes.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          val capturedUserAnswers = userAnswersCaptor.getValue
          capturedUserAnswers.get(AAKickOutStatus()) mustBe Some(2)
        }
      }

      "must set aaKickOutStatus to 1 if anything else" in {

        val mockUserDataService = mock[UserDataService]

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, maybePIAIncreaseRoute)
            .withFormUrlEncodedBody(("value", MaybePIAIncrease.No.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          val capturedUserAnswers = userAnswersCaptor.getValue
          capturedUserAnswers.get(AAKickOutStatus()) mustBe Some(1)
        }
      }
    }
  }
}
