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
import controllers.routes
import forms.ContributionRefundsFormProvider
import models.{AAKickOutStatus, Done, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.DefinedContribution2016PreFlexiAmountPage
import pages.setupquestions.SavingsStatementPage
import pages.setupquestions.annualallowance.ContributionRefundsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.setupquestions.annualallowance.ContributionRefundsView

import scala.concurrent.Future

class ContributionRefundsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ContributionRefundsFormProvider()
  val form         = formProvider()

  lazy val contributionRefundsRoute =
    controllers.setupquestions.annualallowance.routes.ContributionRefundsController.onPageLoad(NormalMode).url

  "ContributionRefunds Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contributionRefundsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ContributionRefundsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ContributionRefundsPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contributionRefundsRoute)

        val view = application.injector.instanceOf[ContributionRefundsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(SavingsStatementPage, false).get))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, contributionRefundsRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, contributionRefundsRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ContributionRefundsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, contributionRefundsRoute)

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
          FakeRequest(POST, contributionRefundsRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "aaKickoutStatus" - {
      "must set aaKickoutStatus to 0 if no and RPSS no" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(SavingsStatementPage, false)
          .success
          .value

        val mockUserDataService = mock[UserDataService]

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, contributionRefundsRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          val capturedUserAnswers = userAnswersCaptor.getValue
          capturedUserAnswers.get(AAKickOutStatus()) mustBe Some(0)

        }
      }

      "must set aaKickoutStatus to 1 if anything else" in {

        val userAnswers = UserAnswers(userAnswersId)
          .set(SavingsStatementPage, true)
          .success
          .value

        val mockUserDataService = mock[UserDataService]

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, contributionRefundsRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          val capturedUserAnswers = userAnswersCaptor.getValue
          capturedUserAnswers.get(AAKickOutStatus()) mustBe Some(1)

        }
      }
    }
  }
}
