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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.*
import models.Done
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT, OK}
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

class SubmitBackendConnectorSpec extends SpecBase with ScalaFutures with WireMockHelper {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    applicationBuilder()
      .configure(
        "microservice.services.submit-public-pension-adjustment.port" -> server.port
      )
      .build()

  "SubmitBackendConnector" - {

    "clearUserAnswers" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearUserAnswers().futureValue

          result `mustBe` Done
        }
      }

      "must return a failed future when the server responds with an error status" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearUserAnswers().failed.futureValue

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }

    }

    "clearCalcUserAnswers" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/calc-user-answers"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearCalcUserAnswers().futureValue

          result `mustBe` Done
        }
      }

      "must return a failed future when the server responds with an error status" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/calc-user-answers"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearCalcUserAnswers().failed.futureValue

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }

    }

    "clearSubmissions" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/submissions"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearSubmissions().futureValue

          result `mustBe` Done
        }
      }

      "must return a failed future when the server responds with an error status" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/submissions"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]
          val result    = connector.clearSubmissions().failed.futureValue

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }

    }

    "userAnswersPresentInSubmissionService" - {
      "must return a true when server returns future true" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/user-answers-present/id"
        val app = application

        val responseBody = "true"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.userAnswersPresentInSubmissionService("id")(hc).futureValue

          result `mustBe` true
        }
      }

      "must return a false when server returns future false" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/user-answers-present/id"
        val app = application

        val responseBody = "false"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.userAnswersPresentInSubmissionService("id")(hc).futureValue

          result `mustBe` false
        }
      }

      "must return a failed future when the server responds with BAD_REQUEST" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/user-answers-present/id"
        val app = application

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          whenReady(connector.userAnswersPresentInSubmissionService("id")(hc).failed) { exc =>
            exc `mustBe` a[java.lang.IllegalArgumentException]
          }
        }
      }
    }

    "submissionsPresentInSubmissionService" - {
      "must return a true when server returns future true" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present/uniqueId"
        val app = application

        val responseBody = "true"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.submissionsPresentInSubmissionService("uniqueId")(hc).futureValue

          result `mustBe` true
        }
      }

      "must return a false when server returns future false" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present/uniqueId"
        val app = application

        val responseBody = "false"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.submissionsPresentInSubmissionService("uniqueId")(hc).futureValue

          result `mustBe` false
        }
      }

      "must return a failed future when the server responds with BAD_REQUEST" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present/uniqueId"
        val app = application

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          whenReady(connector.userAnswersPresentInSubmissionService("uniqueId")(hc).failed) { exc =>
            exc `mustBe` a[java.lang.IllegalArgumentException]
          }
        }
      }
    }

    "submissionsPresentInSubmissionServiceWithId" - {
      "must return a true when server returns future true" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present-with-id/id"
        val app = application

        val responseBody = "true"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.submissionsPresentInSubmissionServiceWithId("id")(hc).futureValue

          result `mustBe` true
        }
      }

      "must return a false when server returns future false" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present-with-id/id"
        val app = application

        val responseBody = "false"

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          val result = connector.submissionsPresentInSubmissionServiceWithId("id")(hc).futureValue

          result `mustBe` false
        }
      }

      "must return a failed future when the server responds with BAD_REQUEST" in {
        implicit val hc = HeaderCarrier()

        val url = s"/submit-public-pension-adjustment/submissions-present-with-id/uniqueId"
        val app = application

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(BAD_REQUEST)))

        running(app) {
          val connector = app.injector.instanceOf[SubmitBackendConnector]

          whenReady(connector.userAnswersPresentInSubmissionService("id")(hc).failed) { exc =>
            exc `mustBe` a[java.lang.IllegalArgumentException]
          }
        }
      }
    }

  }
}
