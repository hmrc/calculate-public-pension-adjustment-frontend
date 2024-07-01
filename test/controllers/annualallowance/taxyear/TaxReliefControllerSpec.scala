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
import forms.annualallowance.taxyear.TaxReliefFormProvider
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
import views.html.annualallowance.taxyear.TaxReliefView
import pages.annualallowance.taxyear.{AmountFlexibleRemunerationArrangementsPage, AmountSalarySacrificeArrangementsPage, HowMuchContributionPensionSchemePage, LumpSumDeathBenefitsValuePage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage}

import scala.concurrent.Future

class TaxReliefControllerSpec extends SpecBase with MockitoSugar {

  val formProvider         = new TaxReliefFormProvider()
  val form                 = formProvider()
  val startEndDate: String = "6 April 2017 to 5 April 2018"
  val period               = Period._2018

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt(0)

  lazy val taxReliefRoute =
    controllers.annualallowance.taxyear.routes.TaxReliefController.onPageLoad(NormalMode, Period._2018).url

  "TaxRelief Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, taxReliefRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaxReliefView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2018, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TaxReliefPage(Period._2018), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, taxReliefRoute)

        val view = application.injector.instanceOf[TaxReliefView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, Period._2018, startEndDate)(
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
        .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
        .success
        .value
        .set(TaxReliefPage(period), BigInt(1))
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
          FakeRequest(POST, taxReliefRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must set aboveThreshold status onSubmit" in {

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
        .set(HowMuchContributionPensionSchemePage(period), BigInt(1))
        .success
        .value
        .set(LumpSumDeathBenefitsValuePage(period), BigInt(1))
        .success
        .value
        .set(TaxReliefPage(period), BigInt(1))
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
          FakeRequest(POST, taxReliefRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(AboveThreshold(period)) mustBe Some(false)
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, taxReliefRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[TaxReliefView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2018, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }
  }
}
