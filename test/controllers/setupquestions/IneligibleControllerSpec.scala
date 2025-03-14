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
import controllers.setupquestions.{routes => setupRoutes}
import play.api.Configuration
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.Helpers.baseApplicationBuilder.injector
import views.html.setupquestions.IneligibleView

class IneligibleControllerSpec extends SpecBase {

  val config: Configuration = injector.instanceOf[Configuration]
  val exitUrl: String       = new FrontendAppConfig(config).exitSurveyUrl

  "Ineligible Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, setupRoutes.IneligibleController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IneligibleView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(exitUrl)(request, messages(application)).toString
        contentAsString(result) must include("What did you think of this service?")
      }
    }
  }
}
