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

package controllers.setupquestions.lifetimeallowance

import base.SpecBase
import config.FrontendAppConfig
import controllers.setupquestions.lifetimeallowance.{routes => ltaRoutes}
import models.{AAKickOutStatus, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.Configuration
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.Helpers.baseApplicationBuilder.injector
import views.html.setupquestions.lifetimeallowance.NotAbleToUseThisTriageLtaView

class NotAbleToUseThisTriageLtaControllerSpec extends SpecBase {

  val kickOutStatusFalse    = 1
  val config: Configuration = injector.instanceOf[Configuration]
  val exitUrl: String       = new FrontendAppConfig(config).exitSurveyUrl

  "NotAbleToUseThisTriageLta Controller" - {

    "when annual allowance status is 1 in the UserAnswers" - {

      "must return show button as true and have correct url and must not show feedback messaage" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(AAKickOutStatus(), 1)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, ltaRoutes.NotAbleToUseThisTriageLtaController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisTriageLtaView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            true,
            "/public-pension-adjustment/triage-journey/received-letter",
            exitUrl
          )(
            request,
            messages(application)
          ).toString
          contentAsString(result) must include("Continue")
          contentAsString(result) must not include "What did you think of this service?"
        }
      }
    }

    "when annual allowance status is 2 in the UserAnswers" - {

      "must return show button as true and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(AAKickOutStatus(), 2)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, ltaRoutes.NotAbleToUseThisTriageLtaController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisTriageLtaView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(true, "/public-pension-adjustment/check-your-answers-setup", exitUrl)(
            request,
            messages(application)
          ).toString
          contentAsString(result) must include("Continue")
        }
      }
    }

    "when annual allowance status is any number in the UserAnswers" - {

      "must return show button as false and have correct url and must show feedback url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(AAKickOutStatus(), 0)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, ltaRoutes.NotAbleToUseThisTriageLtaController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisTriageLtaView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem", exitUrl)(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
          contentAsString(result) must include("What did you think of this service?")
        }
      }
    }

    "when annual allowance status is not set in the UserAnswers" - {

      "must return show button as false and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, ltaRoutes.NotAbleToUseThisTriageLtaController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisTriageLtaView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem", exitUrl)(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
        }
      }
    }
  }
}
