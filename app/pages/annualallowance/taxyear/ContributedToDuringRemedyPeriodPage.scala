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

package pages.annualallowance.taxyear

import controllers.routes
import models.ContributedToDuringRemedyPeriod.Definedcontribution
import models.{ContributedToDuringRemedyPeriod, NormalMode, Period, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class ContributedToDuringRemedyPeriodPage(period: Period)
    extends QuestionPage[Set[ContributedToDuringRemedyPeriod]] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "contributedToDuringRemedyPeriod"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ContributedToDuringRemedyPeriodPage(period)) match {
      case Some(contributions) if contributions.contains(Definedcontribution) =>
        controllers.annualallowance.taxyear.routes.DefinedContributionAmountController
          .onPageLoad(NormalMode, period)
      case Some(_)                                                            =>
        controllers.annualallowance.taxyear.routes.DefinedBenefitAmountController
          .onPageLoad(NormalMode, period)
      case None                                                               =>
        routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
}
