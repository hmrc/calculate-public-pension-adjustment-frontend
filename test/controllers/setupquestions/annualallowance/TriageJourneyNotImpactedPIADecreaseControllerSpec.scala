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

package controllers

import base.SpecBase
import models.{Done, KickOffAuditEvent, LTAKickOutStatus, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuditService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.setupquestions.annualallowance.TriageJourneyNotImpactedPIADecreaseView

import scala.concurrent.Future

class TriageJourneyNotImpactedPIADecreaseControllerSpec extends SpecBase {

  "TriageJourneyNotImpactedPIADecrease Controller" - {

    "when annual allowance status is 1 in the UserAnswers" - {

      "must return show button as true and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(LTAKickOutStatus(), 1)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController
              .onPageLoad()
              .url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[TriageJourneyNotImpactedPIADecreaseView]

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

    "when annual allowance status is 2 in the UserAnswers" - {

      "must return show button as true and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(LTAKickOutStatus(), 2)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController
              .onPageLoad()
              .url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[TriageJourneyNotImpactedPIADecreaseView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(true, "/public-pension-adjustment/check-your-answers-setup")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must include("Continue")
        }
      }
    }

    "when annual allowance status is any number in the UserAnswers" - {

      "must return show button as false and have correct url" in {
        val userAnswers =
          UserAnswers(userAnswersId)
            .set(LTAKickOutStatus(), 0)
            .success
            .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController
              .onPageLoad()
              .url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[TriageJourneyNotImpactedPIADecreaseView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
        }
      }
    }

    "when annual allowance status is not set in the UserAnswers" - {

      "must return show button as false and have correct url" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController
              .onPageLoad()
              .url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[TriageJourneyNotImpactedPIADecreaseView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(false, "/public-pension-adjustment/there-is-a-problem")(
            request,
            messages(application)
          ).toString
          contentAsString(result) must not include "Continue"
        }
      }
    }
    "must trigger audit event" in {

      val mockAuditService = mock[AuditService]
      when(mockAuditService.auditKickOff(any[String], any[KickOffAuditEvent])(any[HeaderCarrier]))
        .thenReturn(Future.successful(Done))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          inject.bind[AuditService].toInstance(mockAuditService)
        )
        .build()

      running(application) {
        val request = FakeRequest(
          GET,
          controllers.setupquestions.annualallowance.routes.TriageJourneyNotImpactedPIADecreaseController
            .onPageLoad()
            .url
        )
        val result  = route(application, request).value
        status(result) mustEqual OK
        verify(mockAuditService).auditKickOff(any[String], any[KickOffAuditEvent])(any[HeaderCarrier])
      }
    }
  }

}
