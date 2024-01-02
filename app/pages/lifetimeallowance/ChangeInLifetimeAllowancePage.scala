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

import controllers.lifetimeallowance.{routes => ltaRoutes}
import controllers.routes
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ChangeInLifetimeAllowancePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "changeInLifetimeAllowance"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ChangeInLifetimeAllowancePage) match {
      case Some(true)  => ltaRoutes.ChangeInTaxChargeController.onPageLoad(NormalMode)
      case Some(false) => ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ChangeInLifetimeAllowancePage) match {
      case Some(true)  => ltaRoutes.ChangeInTaxChargeController.onPageLoad(NormalMode)
      case Some(false) => ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], answers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false =>
          val cleanedUserAnswers = Try(cleanUp(answers, models.LTAPageGroups.changeInLifetimeAllowancePageGroup()))
          cleanedUserAnswers.get.set(ChangeInLifetimeAllowancePage, value = false, cleanUp = false)
        case true  => super.cleanup(value, answers)
      }
      .getOrElse(super.cleanup(value, answers))

  def cleanUp(answers: UserAnswers, pages: Seq[String]): UserAnswers =
    pages.headOption match {
      case Some(page) => cleanUp(answers.remove(models.UserAnswerLTAPageGroup(page)).get, pages.tail)
      case None       => answers
    }
}
