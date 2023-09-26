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

package pages.lifetimeallowance

import models.{CheckMode, Mode, NewExcessLifetimeAllowancePaid, NormalMode, UserAnswers}
import models.NewExcessLifetimeAllowancePaid.{Annualpayment, Both, Lumpsum}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object NewExcessLifetimeAllowancePaidPage extends QuestionPage[NewExcessLifetimeAllowancePaid] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "newExcessLifetimeAllowancePaid"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = navigateInEitherMode(answers, NormalMode)

  override protected def navigateInCheckMode(answers: UserAnswers): Call = navigateInEitherMode(answers, CheckMode)

  private def navigateInEitherMode(answers: UserAnswers, mode: Mode): Call =
    answers.get(NewExcessLifetimeAllowancePaidPage) match {
      case Some(Lumpsum)       => controllers.lifetimeallowance.routes.NewLumpSumValueController.onPageLoad(mode)
      case Some(Both)          => controllers.lifetimeallowance.routes.NewLumpSumValueController.onPageLoad(mode)
      case Some(Annualpayment) => controllers.lifetimeallowance.routes.NewAnnualPaymentValueController.onPageLoad(mode)
      case None                => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[NewExcessLifetimeAllowancePaid], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { case _ =>
        for {
          updated1 <- userAnswers.remove(NewLumpSumValuePage)
          updated2 <- updated1.remove(NewAnnualPaymentValuePage)
        } yield updated2
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
