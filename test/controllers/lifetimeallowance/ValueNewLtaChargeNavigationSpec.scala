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

package controllers.lifetimeallowance

import base.SpecBase
import controllers.lifetimeallowance.{routes => ltaRoutes}
import forms.setupquestions.ReportingChangeFormProvider
import models.{CheckMode, NormalMode, UserAnswers, WhoPayingExtraLtaCharge}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.lifetimeallowance.{LifetimeAllowanceChargeAmountPage, ValueNewLtaChargePage, WhoPayingExtraLtaChargePage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class ValueNewLtaChargeNavigationSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ReportingChangeFormProvider()
  val form         = formProvider()

  lazy val normalRoute = ltaRoutes.ValueNewLtaChargeController.onPageLoad(NormalMode).url
  lazy val checkRoute  = ltaRoutes.ValueNewLtaChargeController.onPageLoad(CheckMode).url

  val validAnswer = BigInt(1000)

  "ValueNewLtaCharge Controller" - {

    "must redirect to the WhoPayingExtraLtaChargeController page when valid data is submitted and " +
      "LifetimeAllowanceChargeAmountPage doesn't exist in NormalMode" in {

        val application: Application = constructApplication(Some(emptyUserAnswers))

        running(application) {
          val request = aFakePostRequestToTheNormalRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual controllers.lifetimeallowance.routes.WhoPayingExtraLtaChargeController
            .onPageLoad(NormalMode)
            .url
        }
      }

    "must redirect to the WhoPayingExtraLtaChargeController page when valid data is submitted and " +
      "LifetimeAllowanceChargeAmountPage doesn't exist in CheckMode" in {

        val application: Application = constructApplication(Some(emptyUserAnswers))

        running(application) {
          val request = aFakePostRequestToTheCheckRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual controllers.lifetimeallowance.routes.WhoPayingExtraLtaChargeController
            .onPageLoad(NormalMode)
            .url
        }
      }

    "must redirect to CheckYourAnswers when user is in NormalMode and answers are less than the old charge" in {

      val answers = emptyUserAnswers
        .set(LifetimeAllowanceChargeAmountPage, BigInt(5000))
        .success
        .value
        .set(ValueNewLtaChargePage, BigInt(1000))
        .success
        .value

      val application: Application = constructApplication(Some(answers))

      running(application) {
        val request = aFakePostRequestToTheNormalRoute

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad.url
      }
    }

    "must redirect to CheckYourAnswers when user is in NormalMode and answers are more than the old charge" in {

      val answers = emptyUserAnswers
        .set(LifetimeAllowanceChargeAmountPage, BigInt(500))
        .success
        .value
        .set(ValueNewLtaChargePage, BigInt(1000))
        .success
        .value

      val application: Application = constructApplication(Some(answers))

      running(application) {
        val request = aFakePostRequestToTheNormalRoute

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(
          result
        ).value mustEqual ltaRoutes.WhoPayingExtraLtaChargeController
          .onPageLoad(NormalMode)
          .url
      }
    }

    "must redirect to WhoPayingExtraLtaCharge when user is in CheckMode and answers are bigger than the old charge " +
      "and WhoPayingExtraLtaChargePage does not exist" in {

        val answers = emptyUserAnswers
          .set(LifetimeAllowanceChargeAmountPage, BigInt(500))
          .success
          .value
          .set(ValueNewLtaChargePage, BigInt(1000))
          .success
          .value

        val application: Application = constructApplication(Some(answers))

        running(application) {
          val request = aFakePostRequestToTheCheckRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual ltaRoutes.WhoPayingExtraLtaChargeController
            .onPageLoad(NormalMode)
            .url
        }
      }

    "must redirect to WhoPayingExtraLtaCharge when user is in CheckMode and answers are bigger than the old charge " +
      "and WhoPayingExtraLtaChargePage DOES exist" in {

        val answers = emptyUserAnswers
          .set(LifetimeAllowanceChargeAmountPage, BigInt(500))
          .success
          .value
          .set(ValueNewLtaChargePage, BigInt(1000))
          .success
          .value
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.values.head)
          .success
          .value

        val application: Application = constructApplication(Some(answers))

        running(application) {
          val request = aFakePostRequestToTheCheckRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad.url
        }
      }

    "must redirect to CheckYourLTAAnswersController when user is in CheckMode and answers are smaller than the old charge " +
      "and WhoPayingExtraLtaChargePage DOES exist" in {

        val answers = emptyUserAnswers
          .set(LifetimeAllowanceChargeAmountPage, BigInt(5000))
          .success
          .value
          .set(ValueNewLtaChargePage, BigInt(1000))
          .success
          .value
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.values.head)
          .success
          .value

        val application: Application = constructApplication(Some(answers))

        running(application) {
          val request = aFakePostRequestToTheCheckRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad.url
        }
      }

    "must redirect to CheckYourLTAAnswersController when there is no old charge" +
      "and WhoPayingExtraLtaChargePage DOES exist" in {

        val answers = emptyUserAnswers
          .set(WhoPayingExtraLtaChargePage, WhoPayingExtraLtaCharge.values.head)
          .success
          .value

        val application: Application = constructApplication(Some(answers))

        running(application) {
          val request = aFakePostRequestToTheCheckRoute

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(
            result
          ).value mustEqual ltaRoutes.CheckYourLTAAnswersController.onPageLoad.url
        }
      }
  }

  private def constructApplication(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) = {
    val mockSessionRepository = mock[SessionRepository]

    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

    applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[SessionRepository].toInstance(mockSessionRepository)
      )
      .build()
  }

  private def aFakePostRequestToTheCheckRoute = {
    val request =
      FakeRequest(POST, checkRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))
    request
  }

  private def aFakePostRequestToTheNormalRoute = {
    val request =
      FakeRequest(POST, normalRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))
    request
  }
}
