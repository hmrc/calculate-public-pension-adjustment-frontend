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
import forms.annualallowance.taxyear.DidYouContributeToRASSchemeFormProvider
import pages.annualallowance.taxyear.{AmountFlexibleRemunerationArrangementsPage, AmountSalarySacrificeArrangementsPage, DidYouContributeToRASSchemePage, LumpSumDeathBenefitsValuePage, RASContributionAmountPage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage}
import models.{AboveThreshold, Done, NormalMode, Period, ThresholdIncome, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.annualallowance.taxyear.DidYouContributeToRASSchemeView

import scala.concurrent.Future

class DidYouContributeToRASSchemeControllerSpec extends SpecBase with MockitoSugar {

  val startEndDate = "6 April 2017 to 5 April 2018"

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DidYouContributeToRASSchemeFormProvider()
  val form         = formProvider()
  val period       = Period._2018

  lazy val didYouContributeToRASSchemeRoute =
    controllers.annualallowance.taxyear.routes.DidYouContributeToRASSchemeController
      .onPageLoad(NormalMode, Period._2018)
      .url

  "DidYouContributeToRASScheme Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didYouContributeToRASSchemeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidYouContributeToRASSchemeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2018, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(DidYouContributeToRASSchemePage(Period._2018), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didYouContributeToRASSchemeRoute)

        val view = application.injector.instanceOf[DidYouContributeToRASSchemeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, Period._2018, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
        .success
        .value
        .set(TotalIncomePage(period), BigInt(1))
        .success
        .value
        .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
        .success
        .value
        .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
        .success
        .value
        .set(TaxReliefPage(period), BigInt(1))
        .success
        .value
        .set(RASContributionAmountPage(period), BigInt(1))
        .success
        .value

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, didYouContributeToRASSchemeRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual controllers.annualallowance.taxyear.routes.DoYouHaveGiftAidController
          .onPageLoad(NormalMode, period)
          .url
      }
    }

    "must set aboveThreshold status onSubmit when ThresholdPage == I Do Not Know" in {

      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(period), ThresholdIncome.IDoNotKnow)
        .success
        .value
        .set(TotalIncomePage(period), BigInt(1))
        .success
        .value
        .set(AmountSalarySacrificeArrangementsPage(period), BigInt(1))
        .success
        .value
        .set(AmountFlexibleRemunerationArrangementsPage(period), BigInt(1))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
        .success
        .value
        .set(TaxReliefPage(period), BigInt(1))
        .success
        .value
        .set(RASContributionAmountPage(period), BigInt(1))
        .success
        .value

      val mockUserDataService = mock[UserDataService]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, didYouContributeToRASSchemeRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(AboveThreshold(period)) mustBe Some(false)
      }
    }

    "must not set aboveThreshold status onSubmit when ThresholdPage is not I Do Not Know" in {

      val ua = emptyUserAnswers
        .set(ThresholdIncomePage(Period._2020), ThresholdIncome.Yes)
        .success
        .value
        .set(TotalIncomePage(Period._2020), BigInt(1))
        .success
        .value
        .set(AmountSalarySacrificeArrangementsPage(Period._2020), BigInt(1))
        .success
        .value
        .set(AmountFlexibleRemunerationArrangementsPage(Period._2020), BigInt(1))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(Period._2020), BigInt(1))
        .success
        .value
        .set(TaxReliefPage(Period._2020), BigInt(1))
        .success
        .value

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, didYouContributeToRASSchemeRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        ua.get(AboveThreshold(Period._2020)) mustBe None
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, didYouContributeToRASSchemeRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidYouContributeToRASSchemeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2018, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, didYouContributeToRASSchemeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(POST, didYouContributeToRASSchemeRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
