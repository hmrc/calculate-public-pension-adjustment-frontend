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

package controllers.lifetimeallowance

import base.SpecBase
import config.FrontendAppConfig
import models.{ReportingChange, UserAnswers}
import pages.setupquestions.ReportingChangePage
import play.api.Configuration
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import views.html.lifetimeallowance.CannotUseLtaServiceNoChargeView

class CannotUseLtaServiceNoChargeControllerSpec extends SpecBase with GuiceOneAppPerSuite {

  "CannotUseLtaServiceNoCharge Controller" - {

    val config: Configuration = app.injector.instanceOf[Configuration]
    val exitUrl: String       = new FrontendAppConfig(config).exitSurveyUrl

    "when AnnualAllowance is included in the UserAnswers" - {

      "must return OK and the correct view with annualAllowanceIncluded as true for a GET and must not show feedback message" in {
        val userAnswers =
          UserAnswers(userAnswersId).set(ReportingChangePage, ReportingChange.values.toSet).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.lifetimeallowance.routes.CannotUseLtaServiceNoChargeController.onPageLoad().url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[CannotUseLtaServiceNoChargeView]

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(true, exitUrl)(request, messages(application)).toString
          contentAsString(result) `must` `include`("Continue")
          contentAsString(result) `must` `not` `include` "What did you think of this service?"
        }
      }
    }

    "when AnnualAllowance is NOT included in the UserAnswers must not show continue and show feedback message" - {

      "must return OK and the correct view with annualAllowanceIncluded as false for a GET" in {
        val userAnswers = UserAnswers(userAnswersId)
          .set(ReportingChangePage, Set[ReportingChange](ReportingChange.LifetimeAllowance))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(
            GET,
            controllers.lifetimeallowance.routes.CannotUseLtaServiceNoChargeController.onPageLoad().url
          )

          val result = route(application, request).value

          val view = application.injector.instanceOf[CannotUseLtaServiceNoChargeView]

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(false, exitUrl)(request, messages(application)).toString
          contentAsString(result) `must` `not` `include` "Continue"
          contentAsString(result) `must` `include`("What did you think of this service?")
        }
      }
    }
  }
}
