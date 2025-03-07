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

import models.requests.{AuthenticatedIdentifierRequest, IdentifierRequest, OptionalDataRequest, UnauthenticatedIdentifierRequest}
import play.api.mvc.ActionTransformer
import services.UserDataService
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject() (
  val userDataService: UserDataService
)(implicit val executionContext: ExecutionContext)
    extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {

    val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val authenticated = request match {
      case AuthenticatedIdentifierRequest(_, _)   => true
      case UnauthenticatedIdentifierRequest(_, _) => false
    }

    for {
      maybeUserAnswers <- userDataService.get()(hc)
    } yield OptionalDataRequest(
      request,
      request.userId,
      maybeUserAnswers.map(
        _.copy(
          authenticated = authenticated
        )
      )
    )
  }
}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, OptionalDataRequest]
