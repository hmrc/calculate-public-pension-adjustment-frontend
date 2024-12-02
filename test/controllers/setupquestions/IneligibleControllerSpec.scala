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
import models.{Done, KickOffAuditEvent}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuditService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.setupquestions.IneligibleView

import scala.concurrent.Future

class IneligibleControllerSpec extends SpecBase {

  "Ineligible Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, setupRoutes.IneligibleController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IneligibleView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
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
        val request = FakeRequest(GET, setupRoutes.IneligibleController.onPageLoad.url)
        val result  = route(application, request).value
        status(result) mustEqual OK
        verify(mockAuditService).auditKickOff(any[String], any[KickOffAuditEvent])(any[HeaderCarrier])
      }

    }
  }
}
