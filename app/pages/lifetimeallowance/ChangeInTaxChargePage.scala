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
import models.ChangeInTaxCharge.{DecreasedCharge, IncreasedCharge, NewCharge}
import models.{ChangeInTaxCharge, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ChangeInTaxChargePage extends QuestionPage[ChangeInTaxCharge] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "changeInTaxCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.DecreasedCharge) | Some(
            ChangeInTaxCharge.IncreasedCharge
          ) =>
        ltaRoutes.MultipleBenefitCrystallisationEventController.onPageLoad(NormalMode)
      case _ =>
        ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(ChangeInTaxChargePage) match {
      case Some(ChangeInTaxCharge.NewCharge) | Some(ChangeInTaxCharge.DecreasedCharge) | Some(
            ChangeInTaxCharge.IncreasedCharge
          ) =>
        ltaRoutes.MultipleBenefitCrystallisationEventController.onPageLoad(NormalMode)
      case _ =>
        ltaRoutes.NotAbleToUseThisServiceLtaController.onPageLoad()
    }

  override def cleanup(value: Option[ChangeInTaxCharge], answers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case ChangeInTaxCharge.None                        =>
          val cleanedUserAnswers = Try(cleanUp(answers, models.LTAPageGroups.changeInTaxChargePageGroup()))
          cleanedUserAnswers.get.set(ChangeInTaxChargePage, value = ChangeInTaxCharge.None, cleanUp = false)
        case NewCharge | DecreasedCharge | IncreasedCharge =>
          super.cleanup(value, answers)
      }
      .getOrElse(super.cleanup(value, answers))

  def cleanUp(answers: UserAnswers, pages: Seq[String]): UserAnswers =
    pages.headOption match {
      case Some(page) => cleanUp(answers.remove(models.UserAnswerLTAPageGroup(page)).get, pages.tail)
      case None       => answers
    }
}
