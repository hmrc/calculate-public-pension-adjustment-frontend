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
import forms.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountFormProvider
import models.{Done, NormalMode, Period, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountView

import java.time.LocalDate
import scala.concurrent.Future

class DefinedContribution2016PreFlexiAmountControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new DefinedContribution2016PreFlexiAmountFormProvider()
  val form         = formProvider(Seq("2 July 2015 and 8 July 2015"))

  val validDate = LocalDate.of(2015, 7, 1)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt("0")

  lazy val definedContribution2016PreFlexiAmountRoute =
    controllers.annualallowance.taxyear.routes.DefinedContribution2016PreFlexiAmountController
      .onPageLoad(NormalMode)
      .url

  "DefinedContribution2016PreFlexiAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreFlexiAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DefinedContribution2016PreFlexiAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form, NormalMode, "2 July 2015 and 8 July 2015")(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value
        .set(DefinedContribution2016PreFlexiAmountPage, validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreFlexiAmountRoute)

        val view = application.injector.instanceOf[DefinedContribution2016PreFlexiAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form.fill(validAnswer), NormalMode, "2 July 2015 and 8 July 2015")(
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
          FakeRequest(POST, definedContribution2016PreFlexiAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, definedContribution2016PreFlexiAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DefinedContribution2016PreFlexiAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(
          boundForm,
          NormalMode,
          "2 July 2015 and 8 July 2015"
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
        val request   = FakeRequest(GET, definedContribution2016PreFlexiAmountRoute)

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
          FakeRequest(POST, definedContribution2016PreFlexiAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }

    "must populate correctly when flexi access date = start of 2016pre sub period" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, Period.pre2016Start)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, definedContribution2016PreFlexiAmountRoute)

        val result = route(application, request).value

        val flexiForm = formProvider(Seq("7 April 2015 and 8 July 2015"))

        val view = application.injector.instanceOf[DefinedContribution2016PreFlexiAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(flexiForm, NormalMode, "7 April 2015 and 8 July 2015")(
          request,
          messages(application)
        ).toString
      }
    }
  }
}
