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
import config.FrontendAppConfig
import models.{AAKickOutStatus, Done, EligibilityAuditEvent, KickOffAuditEvent,  LTAKickOutStatus, NormalMode, ReportingChange}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.setupquestions.ReportingChangePage
import play.api.inject
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AuditService, UserDataService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.setupquestions.UserEligibilityView

import scala.concurrent.Future

class UserEligibilityControllerSpec extends SpecBase {

  "User Eligibility Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UserEligibilityView]

        status(result) mustEqual OK
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual appConfig.redirectToStartPage
      }
    }

    "must redirect to ScottishTaxpayerFrom2016Controller if reporting page has indicated AA and Scottsih tax payer page has not been answered and AA eligible" in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val ltaKickOutStatus = 0
      val aaKickoutStatus  = 2

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head))
        .success
        .value
        .set(AAKickOutStatus(), aaKickoutStatus)
        .success
        .value
        .set(LTAKickOutStatus(), ltaKickOutStatus)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UserEligibilityView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          true,
          false,
          controllers.annualallowance.preaaquestions.routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode),
          Some(ltaKickOutStatus),
          Some(aaKickoutStatus)
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to task list controller if reporting page has indicated AA and Scottsih tax payer page has been answered and AA eligible " in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val ltaKickOutStatus = 0
      val aaKickoutStatus  = 2

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head))
        .success
        .value
        .set(AAKickOutStatus(), aaKickoutStatus)
        .success
        .value
        .set(LTAKickOutStatus(), ltaKickOutStatus)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, false)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UserEligibilityView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          true,
          false,
          controllers.routes.TaskListController.onPageLoad(),
          Some(ltaKickOutStatus),
          Some(aaKickoutStatus)
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must SHOW lta content only if lta kickout status is complete and must NOT show aa if aa kickout status is false" in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val ltaKickOutStatus = 2
      val aaKickoutStatus  = 0

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.LifetimeAllowance))
        .success
        .value
        .set(LTAKickOutStatus(), ltaKickOutStatus)
        .success
        .value
        .set(AAKickOutStatus(), aaKickoutStatus)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, false)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UserEligibilityView]

        status(result) mustEqual OK

        contentAsString(result).contains(
          "You are able to use this service to report changes to your lifetime allowance position."
        ) mustBe true
        contentAsString(result).contains(
          "You are able to use this service to calculate a change in your annual allowance position."
        ) mustBe false

        contentAsString(result) mustEqual view(
          false,
          true,
          controllers.routes.TaskListController.onPageLoad(),
          Some(ltaKickOutStatus),
          Some(aaKickoutStatus)
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must NOT show lta content only if lta kickout status is false and must show aa if aa kickout status is complete" in {
      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val ltaKickOutStatus = 0
      val aaKickoutStatus  = 2

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.AnnualAllowance))
        .success
        .value
        .set(LTAKickOutStatus(), ltaKickOutStatus)
        .success
        .value
        .set(AAKickOutStatus(), aaKickoutStatus)
        .success
        .value
        .set(ScottishTaxpayerFrom2016Page, false)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UserEligibilityView]

        status(result) mustEqual OK

        contentAsString(result).contains(
          "You are able to use this service to report changes to your lifetime allowance position."
        ) mustBe false
        contentAsString(result).contains(
          "You are able to use this service to calculate a change in your annual allowance position."
        ) mustBe true

        contentAsString(result) mustEqual view(
          true,
          false,
          controllers.routes.TaskListController.onPageLoad(),
          Some(ltaKickOutStatus),
          Some(aaKickoutStatus)
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must trigger audit event" in {

      val mockUserDataService = mock[UserDataService]
      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val mockAuditService = mock[AuditService]
      when(mockAuditService.auditEligibility(any[EligibilityAuditEvent])(any[HeaderCarrier]))
        .thenReturn(Future.successful(Done))

      val userAnswers = emptyUserAnswers
        .set(ReportingChangePage, Set[ReportingChange](ReportingChange.values.head))
        .success
        .value
        .set(AAKickOutStatus(), 2)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[AuditService].toInstance(mockAuditService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, controllers.setupquestions.routes.UserEligibility.onPageLoad.url)
        val result  = route(application, request).value
        status(result) mustEqual OK
        verify(mockAuditService).auditEligibility(any[EligibilityAuditEvent])(any[HeaderCarrier])
      }
    }
  }
}
