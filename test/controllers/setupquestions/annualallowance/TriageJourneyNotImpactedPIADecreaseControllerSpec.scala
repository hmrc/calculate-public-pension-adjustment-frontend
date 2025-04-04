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
import models.{LTAKickOutStatus, UserAnswers}
import config.FrontendAppConfig
import play.api.Configuration
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import views.html.setupquestions.annualallowance.TriageJourneyNotImpactedPIADecreaseView

class TriageJourneyNotImpactedPIADecreaseControllerSpec extends SpecBase with GuiceOneAppPerSuite {

  "TriageJourneyNotImpactedPIADecrease Controller" - {

    val config: Configuration = app.injector.instanceOf[Configuration]
    val exitUrl: String       = new FrontendAppConfig(config).exitSurveyUrl

    "when annual allowance status is 1 in the UserAnswers" - {

      "must return show button as true and have correct url and not show feedback message" in {
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

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            true,
            "/public-pension-adjustment/triage-journey/lifetime-allowance/benefit-crystallisation-event",
            exitUrl
          )(
            request,
            messages(application)
          ).toString
          contentAsString(result) `must` `include`("Continue")
          contentAsString(result) `must` `not` `include` "What did you think of this service?"

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

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(
            true,
            "/public-pension-adjustment/check-your-answers-setup",
            exitUrl
          )(
            request,
            messages(application)
          ).toString
          contentAsString(result) `must` `include`("Continue")
        }
      }
    }

    "when annual allowance status is any number in the UserAnswers" - {

      "must return show button as false and have correct url and show feedback message" in {
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

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(false, "/public-pension-adjustment/there-is-a-problem", exitUrl)(
            request,
            messages(application)
          ).toString
          contentAsString(result) `must` `not` `include` "Continue"
          contentAsString(result) `must` `include`("What did you think of this service?")

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

          status(result) `mustEqual` OK
          contentAsString(result) `mustEqual` view(false, "/public-pension-adjustment/there-is-a-problem", exitUrl)(
            request,
            messages(application)
          ).toString
          contentAsString(result) `must` `not` `include` "Continue"
        }
      }
    }
  }

}
