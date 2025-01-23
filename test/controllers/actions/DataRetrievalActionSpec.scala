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

import base.SpecBase
import models.UserAnswers
import models.requests.{AuthenticatedIdentifierRequest, IdentifierRequest, OptionalDataRequest, UnauthenticatedIdentifierRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import services.UserDataService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar {

  class Harness(userDataService: UserDataService) extends DataRetrievalActionImpl(userDataService) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" - {

    "when there is no data in the cache" - {

      "must set userAnswers to 'None' in the request" in {

        val userDataService = mock[UserDataService]
        when(userDataService.get()(any())) `thenReturn` Future(None)
        val action          = new Harness(userDataService)

        val result = action.callTransform(UnauthenticatedIdentifierRequest(FakeRequest(), "id")).futureValue

        result.userAnswers must not be defined
      }
    }

    "when there is data in the cache" - {

      "and the user is unauthenticated" - {

        "must build a userAnswers object and add it to the request" in {

          val userDataService = mock[UserDataService]
          val userAnswers     = UserAnswers("id")
          when(userDataService.get()(any())) `thenReturn` Future(Some(userAnswers))
          val action          = new Harness(userDataService)

          val result = action.callTransform(UnauthenticatedIdentifierRequest(FakeRequest(), "id")).futureValue

          result.userAnswers.value mustEqual userAnswers
          result.userAnswers.value.authenticated mustEqual false
        }
      }

      "and the user is authenticated" - {

        "must build a userAnswers object and add it to the request" in {

          val userDataService = mock[UserDataService]
          when(userDataService.get()(any())) `thenReturn` Future(Some(UserAnswers("id")))
          val action          = new Harness(userDataService)

          val result = action.callTransform(AuthenticatedIdentifierRequest(FakeRequest(), "id")).futureValue

          result.userAnswers mustBe defined
          result.userAnswers.value.authenticated mustEqual true
        }
      }
    }
  }
}
