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
import controllers.annualallowance.taxyear.routes.FlexiAccessDefinedContributionAmountController
import forms.annualallowance.taxyear.FlexiAccessDefinedContributionAmountFormProvider
import models.{Done, NormalMode, Period, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.FlexibleAccessStartDatePage
import pages.annualallowance.taxyear.FlexiAccessDefinedContributionAmountPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.FlexiAccessDefinedContributionAmountView

import java.time.LocalDate
import scala.concurrent.Future

class FlexiAccessDefinedContributionAmountControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new FlexiAccessDefinedContributionAmountFormProvider()
  val form         = formProvider(Seq("13 December 2022 and 5 April 2023"))

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt("0")

  lazy val flexiAccessDefinedContributionAmountRoute =
    FlexiAccessDefinedContributionAmountController.onPageLoad(NormalMode, Period._2023).url

  val validDate = LocalDate.of(2022, 12, 12)

  "FlexiAccessDefinedContributionAmount Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(FlexibleAccessStartDatePage, validDate)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, flexiAccessDefinedContributionAmountRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FlexiAccessDefinedContributionAmountView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form,
          NormalMode,
          Period._2023,
          "13 December 2022 and 5 April 2023"
        )(
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
        .set(FlexiAccessDefinedContributionAmountPage(Period._2023), validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, flexiAccessDefinedContributionAmountRoute)

        val view = application.injector.instanceOf[FlexiAccessDefinedContributionAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form.fill(validAnswer),
          NormalMode,
          Period._2023,
          "13 December 2022 and 5 April 2023"
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
          FakeRequest(POST, flexiAccessDefinedContributionAmountRoute)
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
          FakeRequest(POST, flexiAccessDefinedContributionAmountRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[FlexiAccessDefinedContributionAmountView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(
          boundForm,
          NormalMode,
          Period._2023,
          "13 December 2022 and 5 April 2023"
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
        val request   = FakeRequest(GET, flexiAccessDefinedContributionAmountRoute)

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
          FakeRequest(POST, flexiAccessDefinedContributionAmountRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER

        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }

    "Flexi access and start date of period scenarios" - {

      "must populate correctly when flexi access date = start of in any other period" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(FlexibleAccessStartDatePage, LocalDate.of(2022, 4, 6))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request =
            FakeRequest(GET, FlexiAccessDefinedContributionAmountController.onPageLoad(NormalMode, Period._2023).url)

          val flexiForm = formProvider(Seq("7 April 2022 and 5 April 2023"))

          val view = application.injector.instanceOf[FlexiAccessDefinedContributionAmountView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            flexiForm,
            NormalMode,
            Period._2023,
            "7 April 2022 and 5 April 2023"
          )(
            request,
            messages(application)
          ).toString
        }
      }

      "must populate correctly when flexi access date does not equal start of in any other period" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(FlexibleAccessStartDatePage, LocalDate.of(2022, 4, 7))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request =
            FakeRequest(GET, FlexiAccessDefinedContributionAmountController.onPageLoad(NormalMode, Period._2023).url)

          val flexiForm = formProvider(Seq("8 April 2022 and 5 April 2023"))

          val view = application.injector.instanceOf[FlexiAccessDefinedContributionAmountView]

          val result = route(application, request).value

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            flexiForm,
            NormalMode,
            Period._2023,
            "8 April 2022 and 5 April 2023"
          )(
            request,
            messages(application)
          ).toString
        }
      }
    }
  }
}
