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

package controllers.actions

import config.FrontendAppConfig
import controllers.routes
import models.requests.{AuthenticatedIdentifierRequest, IdentifierRequest, UnauthenticatedIdentifierRequest}
import play.api.Logging
import play.api.mvc.*
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]

class OptionalAuthIdentifierAction @Inject() (
  val authConnector: AuthConnector,
  val parser: BodyParsers.Default,
  config: FrontendAppConfig
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  private val retrievals =
    Retrievals.nino and
      Retrievals.itmpName

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] =
    invokeBlockOptionallyAuthenticated(request, block)

  private def invokeBlockOptionallyAuthenticated[A](
    request: Request[A],
    block: IdentifierRequest[A] => Future[Result]
  ): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val requiredConfidenceLevel = ConfidenceLevel.fromInt(config.requiredAuthConfidenceLevel.toInt).get

    authorised((AffinityGroup.Individual or AffinityGroup.Organisation) and requiredConfidenceLevel and User)
      .retrieve(retrievals) {
        case Some(nino) ~ Some(_) =>
          block(AuthenticatedIdentifierRequest(request, nino))
        case _                    =>
          logger.warn(s"Incomplete retrievals")
          Future.successful(Redirect(routes.UnauthorisedController.onPageLoad().url))
      } recoverWith {
      case _: NoActiveSession             =>
        hc.sessionId match {
          case Some(sessionId) =>
            block(UnauthenticatedIdentifierRequest(request, sessionId.value))
          case None            =>
            logger.error(s"User has no active sessionId: ${request.path}")
            Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
        }
      case _: InsufficientConfidenceLevel =>
        insufficientConfidence()
      case ex: UnsupportedAffinityGroup   =>
        logger.warn(s"User has UnsupportedAffinityGroup. The reason is ${ex.reason} .")
        Future.successful(Redirect(routes.CannotUseServiceNotIndividualController.onPageLoad))
      case ex: AuthorisationException     =>
        logger.warn(s"User has AuthorisationException. The reason is ${ex.reason} .")
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad()))
    }
  }

  private def insufficientConfidence() =
    Future.successful(
      Redirect(
        config.confidenceUpliftUrl,
        Map(
          "origin"          -> Seq(config.origin),
          "confidenceLevel" -> Seq(config.requiredAuthConfidenceLevel),
          "completionURL"   -> Seq(config.upliftCompletionUrl),
          "failureURL"      -> Seq(config.upliftFailureUrl)
        )
      )
    )

}
