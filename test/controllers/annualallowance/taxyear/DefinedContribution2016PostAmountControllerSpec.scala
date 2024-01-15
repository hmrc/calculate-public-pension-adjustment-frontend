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
import controllers.routes
import forms.annualallowance.taxyear.DefinedContribution2016PostAmountFormProvider
import models.{NormalMode, Period, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.{DefinedContribution2016PostAmountPage, DefinedContribution2016PostFlexiAmountPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.annualallowance.taxyear.DefinedContribution2016PostAmountView

import java.time.LocalDate
import scala.concurrent.Future

class DefinedContribution2016PostAmountControllerSpec extends SpecBase with MockitoSugar {

  val formProvider           = new DefinedContribution2016PostAmountFormProvider()
  val form                   = formProvider(Seq("9 July 2015 and 5 April 2016"))
  val flexiForm              = formProvider(Seq("9 July 2015 and 1 December 2015"))
  val flexiFormStartOfPeriod = formProvider(Seq("9 July 2015 and 9 July 2015"))

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt("0")

  lazy val definedContribution2016PostAmountRoute =
    controllers.annualallowance.taxyear.routes.DefinedContribution2016PostAmountController.onPageLoad(NormalMode).url

  "DefinedContribution2016PostAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PostAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PostAmountView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, "9 July 2015 and 5 April 2016")(
          request,
          messages(application)
        ).toString
      }
    }

    "must return OK and the correct view when user indicated flexi year for a GET" in {

      val validDate = LocalDate.of(2015, 12, 1)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PostAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PostAmountView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          flexiForm,
          NormalMode,
          "9 July 2015 and 1 December 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must return OK and the correct view when user indicated flexi year for a GET when flexi date first day of sub-period" in {

      val validDate = Period.post2016Start

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PostAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PostAmountView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          flexiForm,
          NormalMode,
          "9 July 2015 and 9 July 2015"
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate flexi year amount answer for period with 0 when user has entered flexi date as last date of sub-period" in {
      val validDate = LocalDate.of(2016, 4, 5)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PostAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(DefinedContribution2016PostFlexiAmountPage) mustBe Some(BigInt(0))
      }
    }

    "must not populate flexi year amount answer for period with 0 when user has entered flexi date as any other date than last day of period" in {
      val validDate = LocalDate.of(2016, 1, 1)

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PostAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(DefinedContribution2016PostFlexiAmountPage) mustBe None
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(DefinedContribution2016PostAmountPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PostAmountRoute)

        val view = application.injector.instanceOf[DefinedContribution2016PostAmountView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, "9 July 2015 and 5 April 2016")(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PostAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PostAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DefinedContribution2016PostAmountView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          boundForm,
          NormalMode,
          "9 July 2015 and 5 April 2016"
        )(request, messages(application)).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, definedContribution2016PostAmountRoute)

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
          FakeRequest(POST, definedContribution2016PostAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
