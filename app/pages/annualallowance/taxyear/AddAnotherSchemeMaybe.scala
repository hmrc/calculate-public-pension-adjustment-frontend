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
import models.{NormalMode, Period, SchemeIndex, UserAnswers}
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import play.api.mvc.Call

object AddAnotherSchemeMaybe {

  def navigate(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex): Call =
    answers.get(MemberMoreThanOnePensionPage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.AddAnotherSchemeController.onPageLoad(period, schemeIndex)
      case Some(false) => exitSchemeLoopNavigation(answers, period, schemeIndex)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  def exitSchemeLoopNavigation(answers: UserAnswers, period: Period, schemeIndex: SchemeIndex) =
    answers.get(DefinedContributionPensionSchemePage) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.OtherDefinedBenefitOrContributionController
          .onPageLoad(NormalMode, period, schemeIndex)
      case Some(false) => noDCNavigation(period, schemeIndex)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def noDCNavigation(period: Period, schemeIndex: SchemeIndex): Call =
    period match {
      case Period._2016PreAlignment  =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Period._2016PostAlignment =>
        controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period, schemeIndex)
      case Period.Year(_)            =>
        controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period, schemeIndex)
    }
}
