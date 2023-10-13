package controllers.lifetimeallowance

import base.SpecBase
import controllers.routes
import play.api.test.FakeRequest
import play.api.test.Helpers._

class CannotUseLtaServiceNoChargeControllerSpec extends SpecBase {

  "CannotUseLtaServiceNoCharge Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CannotUseLtaServiceNoChargeController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CannotUseLtaServiceNoChargeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
