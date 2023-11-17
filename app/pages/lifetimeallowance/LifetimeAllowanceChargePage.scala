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

import models.{CheckMode, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object LifetimeAllowanceChargePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "lta" \ toString

  override def toString: String = "lifetimeAllowanceCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(LifetimeAllowanceChargePage) match {
      case Some(true)  =>
        controllers.lifetimeallowance.routes.ExcessLifetimeAllowancePaidController.onPageLoad(NormalMode)
      case Some(false) =>
        controllers.lifetimeallowance.routes.NewExcessLifetimeAllowancePaidController.onPageLoad(NormalMode)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(LifetimeAllowanceChargePage) match {
      case Some(true)  =>
        controllers.lifetimeallowance.routes.ExcessLifetimeAllowancePaidController.onPageLoad(NormalMode)
      case Some(false) =>
        controllers.lifetimeallowance.routes.NewExcessLifetimeAllowancePaidController.onPageLoad(CheckMode)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  => super.cleanup(value, userAnswers)
        case false =>
          userAnswers
            .remove(ExcessLifetimeAllowancePaidPage)
            .flatMap(_.remove(LumpSumValuePage))
            .flatMap(_.remove(AnnualPaymentValuePage))
            .flatMap(_.remove(UserSchemeDetailsPage))
            .flatMap(_.remove(WhoPaidLTAChargePage))
            .flatMap(_.remove(SchemeNameAndTaxRefPage))
            .flatMap(_.remove(YearChargePaidPage))
            .flatMap(_.remove(QuarterChargePaidPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
