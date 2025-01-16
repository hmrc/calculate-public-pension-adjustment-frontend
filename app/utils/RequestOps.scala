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

package utils

import models.requests.{AuthenticatedIdentifierRequest, DataRequest, OptionalDataRequest}
import play.api.mvc.{Request, RequestHeader}

object RequestOps {

  implicit class RequestSyntax(request: RequestHeader) {

    def signedIn: Boolean = request match {
      case dr: DataRequest[_] =>
        dr.request match {
          case _: AuthenticatedIdentifierRequest[_] => true
          case _                                    => false
        }

      case odr: OptionalDataRequest[_] =>
        odr.request match {
          case _: AuthenticatedIdentifierRequest[_] => true
          case _                                    => false
        }

      case air: AuthenticatedIdentifierRequest[_] =>
        true

      case _ =>
        false
    }
  }
}
