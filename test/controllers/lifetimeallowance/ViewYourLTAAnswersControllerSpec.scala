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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import viewmodels.govuk.SummaryListFluency
import views.html.lifetimeallowance.ViewYourAnswersView

class ViewYourLTAAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "View Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(GET, controllers.lifetimeallowance.routes.ViewYourLTAAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ViewYourAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          controllers.routes.CalculationReviewController.onPageLoad(),
          list
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to start of the service for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   =
          FakeRequest(GET, controllers.lifetimeallowance.routes.ViewYourLTAAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` appConfig.redirectToStartPage
      }
    }
  }

}
