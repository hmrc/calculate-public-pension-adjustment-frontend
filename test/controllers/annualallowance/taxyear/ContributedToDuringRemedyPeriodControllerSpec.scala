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
import controllers.annualallowance.taxyear.routes.ContributedToDuringRemedyPeriodController
import forms.annualallowance.taxyear.ContributedToDuringRemedyPeriodFormProvider
import models.Period._2013
import models.{ContributedToDuringRemedyPeriod, Done, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.ContributedToDuringRemedyPeriodPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UserDataService
import views.html.annualallowance.taxyear.ContributedToDuringRemedyPeriodView

import scala.concurrent.Future

class ContributedToDuringRemedyPeriodControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val contributedToDuringRemedyPeriodRoute =
    ContributedToDuringRemedyPeriodController.onPageLoad(NormalMode, _2013).url

  val formProvider = new ContributedToDuringRemedyPeriodFormProvider()
  val form         = formProvider()

  "ContributedToDuringRemedyPeriod Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contributedToDuringRemedyPeriodRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ContributedToDuringRemedyPeriodView]

        status(result) `mustEqual` OK

        contentAsString(result) `mustEqual` view(form, NormalMode, _2013)(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ContributedToDuringRemedyPeriodPage(_2013), ContributedToDuringRemedyPeriod.values.toSet)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contributedToDuringRemedyPeriodRoute)

        val view = application.injector.instanceOf[ContributedToDuringRemedyPeriodView]

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          form.fill(ContributedToDuringRemedyPeriod.values.toSet),
          NormalMode,
          _2013
        )(request, messages(application)).toString
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
          FakeRequest(POST, contributedToDuringRemedyPeriodRoute)
            .withFormUrlEncodedBody(("value[0]", ContributedToDuringRemedyPeriod.values.head.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, contributedToDuringRemedyPeriodRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ContributedToDuringRemedyPeriodView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(boundForm, NormalMode, _2013)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, contributedToDuringRemedyPeriodRoute)

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
          FakeRequest(POST, contributedToDuringRemedyPeriodRoute)
            .withFormUrlEncodedBody(("value[0]", ContributedToDuringRemedyPeriod.values.head.toString))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }
}
