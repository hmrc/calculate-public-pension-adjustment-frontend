/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.actions

import auth.Retrievals.Ops
import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import models.requests.{AuthenticatedIdentifierRequest, UnauthenticatedIdentifierRequest}
import play.api.Application
import play.api.mvc.BodyParsers
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{ItmpName, Retrieval}
import uk.gov.hmrc.auth.core.{AuthConnector, InsufficientConfidenceLevel, InternalError, MissingBearerToken, UnsupportedAffinityGroup}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class OptionalAuthIdentifierActionSpec extends SpecBase {

  def buildApplication(optionalAuthEnabled: Boolean): Application = applicationBuilder(userAnswers = None)
    .configure(
      "feature-flags.optionalAuth" -> optionalAuthEnabled
    )
    .build()

  def bodyParsers(application: Application) = application.injector.instanceOf[BodyParsers.Default]

  def config(application: Application) = application.injector.instanceOf[FrontendAppConfig]

  val sessionId = "sessionId"
  val userId    = "userId"

  "when optional auth is enabled" - {

    val application = buildApplication(optionalAuthEnabled = true)

    "when the user is authenticated" - {

      "with an unsupported affinity group" - {

        "they must be redirected to not an individual error page" in {

          val authAction = new OptionalAuthIdentifierAction(
            new FakeFailingAuthConnector(UnsupportedAffinityGroup()),
            bodyParsers(application),
            config(application)
          )
          val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

          val result = authAction(a => Ok(a.userId))(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.CannotUseServiceNotIndividualController.onPageLoad.url
        }
      }

      "as an individual" - {

        "with a non specific authorisation error" - {

          "they must redirect to the unauthorised page" in {

            val authAction = new OptionalAuthIdentifierAction(
              new FakeFailingAuthConnector(InternalError()),
              bodyParsers(application),
              config(application)
            )
            val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

            val result = authAction(_ => Ok)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(
              result
            ).value mustEqual routes.UnauthorisedController.onPageLoad().url
          }
        }

        "with insufficient confidence" - {

          "they must be redirected to uplift their confidence level" in {

            val authAction = new OptionalAuthIdentifierAction(
              new FakeFailingAuthConnector(InsufficientConfidenceLevel()),
              bodyParsers(application),
              config(application)
            )
            val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

            val result = authAction(a => Ok(a.userId))(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(
              result
            ).value mustEqual "http://localhost:9948/iv-stub/uplift?origin=PPA&confidenceLevel=250&completionURL=http%3A%2F%2Flocalhost%3A12804%2Fpublic-pension-adjustment%2Fchange-previous-adjustment&failureURL=http%3A%2F%2Flocalhost%3A12804%2Fpublic-pension-adjustment%2Fuplift-failure"
          }
        }

        "and a user id is retrieved" - {

          "they must succeed with an AuthenticatedIdentifierRequest" in {

            val authAction = new OptionalAuthIdentifierAction(
              new FakeAuthConnector(
                Some(userId) ~ Some("nino") ~ Some(
                  ItmpName(Some("firstN"), Some("middleN"), Some("last"))
                )
              ),
              bodyParsers(application),
              config(application)
            )
            val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

            val result = authAction(a =>
              a match {
                case x: AuthenticatedIdentifierRequest[_]   => Ok(s"${x.userId}")
                case y: UnauthenticatedIdentifierRequest[_] => Ok(y.userId)
              }
            )(request)

            status(result) mustEqual OK
            contentAsString(result) mustEqual s"$userId"
          }
        }

        "but no user id is retrieved" - {

          "they must redirect to the unauthorised page" in {

            val authAction = new OptionalAuthIdentifierAction(
              new FakeAuthConnector(None ~ None ~ None),
              bodyParsers(application),
              config(application)
            )
            val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

            val result = authAction(_ => Ok)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(
              result
            ).value mustEqual routes.UnauthorisedController.onPageLoad().url
          }
        }
      }
    }

    "when the user is unauthenticated" - {

      "must use an UnauthenticatedIdentifierRequest" in {

        val authAction =
          new OptionalAuthIdentifierAction(
            new FakeFailingAuthConnector(MissingBearerToken()),
            bodyParsers(application),
            config(application)
          )
        val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)

        val result = authAction(a =>
          a match {
            case x: AuthenticatedIdentifierRequest[_]   => Ok(s"${x.userId}")
            case y: UnauthenticatedIdentifierRequest[_] => Ok(y.userId)
          }
        )(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual sessionId
      }

      "must redirect to the session expired page when the user is unauthenticated and has no session identifier" in {

        val authAction =
          new OptionalAuthIdentifierAction(
            new FakeFailingAuthConnector(new MissingBearerToken),
            bodyParsers(application),
            config(application)
          )
        val request    = FakeRequest()

        val result = authAction(a => Ok(a.userId))(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(controllers.routes.JourneyRecoveryController.onPageLoad().url)
      }
    }
  }

  "when optional auth is disabled" - {

    val application = buildApplication(optionalAuthEnabled = false)

    "when there is no active session" - {
      "must redirect to the session expired page" in {

        val authAction = new OptionalAuthIdentifierAction(
          new FakeAuthConnector(None),
          bodyParsers(application),
          config(application)
        )
        val request    = FakeRequest()
        val result     = authAction(a => Ok(a.userId))(request)
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(controllers.routes.JourneyRecoveryController.onPageLoad().url)
      }
    }

    "when there is an active session" - {
      "must perform the action" in {
        val authAction =
          new OptionalAuthIdentifierAction(
            new FakeFailingAuthConnector(MissingBearerToken()),
            bodyParsers(application),
            config(application)
          )
        val request    = FakeRequest().withSession(SessionKeys.sessionId -> sessionId)
        val result     = authAction(a =>
          a match {
            case x: AuthenticatedIdentifierRequest[_]   => Ok(s"${x.userId}")
            case y: UnauthenticatedIdentifierRequest[_] => Ok(y.userId)
          }
        )(request)
        status(result) mustEqual OK
        contentAsString(result) mustEqual sessionId
      }
    }
  }

  class FakeAuthConnector[T](value: T) extends AuthConnector {
    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext
    ): Future[A] =
      Future.fromTry(Try(value.asInstanceOf[A]))
  }

  class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {
    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext
    ): Future[A] =
      Future.failed(exceptionToReturn)
  }
}
