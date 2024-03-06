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

package controllers.setupquestions

import base.SpecBase
import controllers.setupquestions.{routes => setupRoutes}
import forms.ResubmittingAdjustmentFormProvider
import models.{Done, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.setupquestions.{ResubmittingAdjustmentPage, SavingsStatementPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import views.html.setupquestions.ResubmittingAdjustmentView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class ResubmittingAdjustmentControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ResubmittingAdjustmentFormProvider()
  val form         = formProvider()

  lazy val resubmittingNormalRoute = setupRoutes.ResubmittingAdjustmentController.onPageLoad(NormalMode).url

  "ResubmittingAdjustment Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, resubmittingNormalRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ResubmittingAdjustmentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ResubmittingAdjustmentPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, resubmittingNormalRoute)

        val view = application.injector.instanceOf[ResubmittingAdjustmentView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      val mockUserDataService = mock[UserDataService]
      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val fakeAuthConnector = new FakeAuthConnector(Some("userId"))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[AuthConnector].toInstance(fakeAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, resubmittingNormalRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

//        val capturedUserAnswers = userAnswersCaptor.getValue
//        capturedUserAnswers.get(ResubmittingAdjustmentPage).get mustBe true
//        capturedUserAnswers.get(SavingsStatementPage(true)).get mustBe true
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, resubmittingNormalRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ResubmittingAdjustmentView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "when user answers can't be found and the user is signed in" in {

      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = true).build()

      running(application) {
        val request = FakeRequest(GET, resubmittingNormalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
      }
    }

    "when user answers can't be found and the user is not signed in" in {

      val application = applicationBuilder(userAnswers = None, userIsAuthenticated = false).build()

      running(application) {
        val request = FakeRequest(GET, resubmittingNormalRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
      }
    }
  }

  class FakeAuthConnector[T](value: T) extends AuthConnector {
    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext
    ): Future[A] =
      Future.fromTry(Try(value.asInstanceOf[A]))
  }
}
