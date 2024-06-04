package controllers.annualallowance.taxyear

import base.SpecBase
import controllers.routes
import forms.annualallowance.taxyear.InterestFromSavingsFormProvider
import models.{Done, NormalMode, Period, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.taxyear.InterestFromSavingsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.annualallowance.taxyear.InterestFromSavingsView

import scala.concurrent.Future

class InterestFromSavingsControllerSpec extends SpecBase with MockitoSugar {

  val period = Period._2019
  val formProvider = new InterestFromSavingsFormProvider()
  val form = formProvider(period)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = 1

  lazy val interestFromSavingsRoute = controllers.annualallowance.taxyear.routes.InterestFromSavingsController.onPageLoad(NormalMode, period).url

  "InterestFromSavings Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, interestFromSavingsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[InterestFromSavingsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, period)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(InterestFromSavingsPage(period), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, interestFromSavingsRoute)

        val view = application.injector.instanceOf[InterestFromSavingsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, period)(request, messages(application)).toString
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
          FakeRequest(POST, interestFromSavingsRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad(None).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, interestFromSavingsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[InterestFromSavingsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, period)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, interestFromSavingsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, interestFromSavingsRoute)
            .withFormUrlEncodedBody(("value", validAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}