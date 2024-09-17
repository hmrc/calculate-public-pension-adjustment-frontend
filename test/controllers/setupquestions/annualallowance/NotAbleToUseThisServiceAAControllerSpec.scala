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
import controllers.setupquestions.annualallowance.{routes => triageAARoutes}
import models.{LTAKickOutStatus, ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.setupquestions.annualallowance.NotAbleToUseThisServiceAAView

class NotAbleToUseThisServiceAAControllerSpec extends SpecBase {

  val kickOutStatusFalse = 1

  "NotAbleToUseThisServiceLta Controller" - {

    "when lifetime allowance status is 1 in the UserAnswers" - {

      "must return show button as true and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(LTAKickOutStatus(), 1)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, triageAARoutes.NotAbleToUseThisServiceAAController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisServiceAAView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            true,
            "/public-pension-adjustment/triage-journey/lifetime-allowance/benefit-crystallisation-event"
          )(
            request,
            messages(application)
          ).toString
          contentAsString(result) must include("Continue")
        }
      }
    }

    "when lta status is 2 in the UserAnswers" - {

      "must return show button as true and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(LTAKickOutStatus(), 2)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, triageAARoutes.NotAbleToUseThisServiceAAController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisServiceAAView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(true, "/public-pension-adjustment/check-your-answers-setup")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must include("Continue")
        }
      }
    }

    "when LTA status is 0 in the UserAnswers" - {

      "must return show button as false and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .get
            .set(LTAKickOutStatus(), 0)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, triageAARoutes.NotAbleToUseThisServiceAAController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisServiceAAView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
        }
      }
    }

    "when lta status is not set in the UserAnswers" - {

      "must return show button as false and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(ReportingChangePage, ReportingChange.values.toSet)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, triageAARoutes.NotAbleToUseThisServiceAAController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAbleToUseThisServiceAAView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
        }
      }
    }
  }
}
