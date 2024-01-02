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

package pages.lifetimeallowance

import models.ExcessLifetimeAllowancePaid.{Both, Lumpsum}
import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object LumpSumValuePage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "lumpSumValue"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ExcessLifetimeAllowancePaidPage) match {
      case Some(Lumpsum) => controllers.lifetimeallowance.routes.WhoPaidLTAChargeController.onPageLoad(NormalMode)
      case Some(Both)    => controllers.lifetimeallowance.routes.AnnualPaymentValueController.onPageLoad(NormalMode)
      case _             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ExcessLifetimeAllowancePaidPage) match {
      case Some(Lumpsum) =>
        controllers.lifetimeallowance.routes.NewExcessLifetimeAllowancePaidController.onPageLoad(NormalMode)
      case Some(Both)    => controllers.lifetimeallowance.routes.AnnualPaymentValueController.onPageLoad(CheckMode)
      case _             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[BigInt], answers: UserAnswers): Try[UserAnswers] =
    value
      .map { case _ =>
        Try(cleanUp(answers, models.LTAPageGroups.newExcessLifetimeAllowancePaidPageGroup()))
      }
      .getOrElse(super.cleanup(value, answers))

  def cleanUp(answers: UserAnswers, pages: Seq[String]): UserAnswers =
    pages.headOption match {
      case Some(page) => cleanUp(answers.remove(models.UserAnswerLTAPageGroup(page)).get, pages.tail)
      case None       => answers
    }
}
