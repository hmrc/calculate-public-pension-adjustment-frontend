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

package pages.annualallowance.taxyear

import models.{NormalMode, PensionSchemeInput2016preAmounts, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class PensionSchemeInput2016preAmountsPage(period: Period, schemeIndex: SchemeIndex)
    extends QuestionPage[PensionSchemeInput2016preAmounts] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "PensionSchemeInput2016preAmounts"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    if (maybeStopPayingInFirstSubPeriod(answers)) {
      controllers.annualallowance.taxyear.routes.PayAChargeController.onPageLoad(NormalMode, period, schemeIndex)
    } else {
      controllers.annualallowance.taxyear.routes.PensionSchemeInput2016postAmountsController
        .onPageLoad(NormalMode, period, schemeIndex)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)

  private def maybeStopPayingInFirstSubPeriod(answers: UserAnswers) = answers.get(StopPayingPublicPensionPage) match {
    case Some(date) => Period.pre2016Start.minusDays(1).isBefore(date) && Period.pre2016End.plusDays(1).isAfter(date)
    case None       => false
  }
}
