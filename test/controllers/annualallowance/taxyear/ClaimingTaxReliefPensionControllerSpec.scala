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
import forms.annualallowance.taxyear.ClaimingTaxReliefPensionFormProvider
import models.{ContributedToDuringRemedyPeriod, Done, NormalMode, Period, ThresholdIncome, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.{AmountFlexibleRemunerationArrangementsPage, AmountSalarySacrificeArrangementsPage, ClaimingTaxReliefPensionPage, DefinedContribution2016PreFlexiAmountPage, HowMuchContributionPensionSchemePage, LumpSumDeathBenefitsValuePage, TaxReliefPage, ThresholdIncomePage, TotalIncomePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.annualallowance.taxyear.ClaimingTaxReliefPensionView

import scala.concurrent.Future

class ClaimingTaxReliefPensionControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val startEndDate = "6 April 2019 to 5 April 2020"

  val period       = Period._2020
  val formProvider = new ClaimingTaxReliefPensionFormProvider()
  val form         = formProvider(Seq(startEndDate))

  lazy val claimingTaxReliefPensionRoute =
    controllers.annualallowance.taxyear.routes.ClaimingTaxReliefPensionController.onPageLoad(NormalMode, period).url

  "ClaimingTaxReliefPension Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, claimingTaxReliefPensionRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ClaimingTaxReliefPensionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, period, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ClaimingTaxReliefPensionPage(period), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, claimingTaxReliefPensionRoute)

        val view = application.injector.instanceOf[ClaimingTaxReliefPensionView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, period, startEndDate)(
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
          FakeRequest(POST, claimingTaxReliefPensionRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual controllers.annualallowance.taxyear.routes.DidYouContributeToRASSchemeController
          .onPageLoad(NormalMode, period)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, claimingTaxReliefPensionRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ClaimingTaxReliefPensionView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, period, startEndDate)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, claimingTaxReliefPensionRoute)

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
          FakeRequest(POST, claimingTaxReliefPensionRoute)
            .withFormUrlEncodedBody(("value[0]", ContributedToDuringRemedyPeriod.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }
  }
}
