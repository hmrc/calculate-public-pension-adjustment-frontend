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

package pages.annualallowance

import models.{NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.PeriodService

case class AddAnotherSchemePage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "addAnotherScheme"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(AddAnotherSchemePage(period, schemeIndex)) match {
      case Some(true)  =>
        val nextSchemeIndex = SchemeIndex(schemeIndex.value + 1)
        if (PeriodService.isFirstPeriod(answers, period)) {
          controllers.annualallowance.routes.PensionSchemeDetailsController
            .onPageLoad(NormalMode, period, nextSchemeIndex)
        } else controllers.annualallowance.routes.WhichSchemeController.onPageLoad(NormalMode, period, nextSchemeIndex)
      case Some(false) =>
        controllers.annualallowance.routes.CheckYourAAPeriodAnswersController
          .onPageLoad(period) // TODO until onward pages are added
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
}
