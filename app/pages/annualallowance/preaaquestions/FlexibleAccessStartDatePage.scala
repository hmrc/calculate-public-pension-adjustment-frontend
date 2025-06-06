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

package pages.annualallowance.preaaquestions

import controllers.routes
import models.tasklist.sections.AASection
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate
import scala.util.Try

case object FlexibleAccessStartDatePage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "flexibleAccessStartDate"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(FlexibleAccessStartDatePage) match {
      case Some(_) =>
        controllers.annualallowance.preaaquestions.routes.PayTaxCharge1415Controller.onPageLoad(NormalMode)
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(FlexibleAccessStartDatePage) match {
      case Some(_) => controllers.annualallowance.preaaquestions.routes.CheckYourAASetupAnswersController.onPageLoad()
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[LocalDate], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) => Try(AASection.removeAllAAPeriodAnswersAndNavigation(userAnswers))
      case None    => super.cleanup(value, userAnswers)
    }
}
