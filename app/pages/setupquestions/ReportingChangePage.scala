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

package pages.setupquestions

import controllers.routes
import models.tasklist.sections.LTASection
import models.{Period, ReportingChange, UserAnswers, UserAnswersPeriod}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, FlexibleAccessStartDatePage, FlexiblyAccessedPensionPage, PIAPreRemedyPage, PayTaxCharge1415Page, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page, StopPayingPublicPensionPage, WhichYearsScottishTaxpayerPage}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.PeriodService

import scala.util.Try

case object ReportingChangePage extends QuestionPage[Set[ReportingChange]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reportingChange"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(_) => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
    case _       => routes.JourneyRecoveryController.onPageLoad(None)
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(_) => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
    case _       => routes.JourneyRecoveryController.onPageLoad(None)
  }
  override def cleanup(
    value: Option[Set[ReportingChange]],
    userAnswers: UserAnswers
  ): Try[UserAnswers] =
    value
      .map { set =>
        if (!set.contains(ReportingChange.LifetimeAllowance) && set.contains(ReportingChange.AnnualAllowance)) {
          LTASection.deleteAllAnswers(userAnswers)
        } else if (!set.contains(ReportingChange.AnnualAllowance) && set.contains(ReportingChange.LifetimeAllowance)) {
          val periodsToCleanup   = PeriodService.allRemedyPeriods
          val cleanedUserAnswers = periodAACleanup(userAnswers, periodsToCleanup)
          cleanedUserAnswers
            .remove(ScottishTaxpayerFrom2016Page)
            .flatMap(_.remove(WhichYearsScottishTaxpayerPage))
            .flatMap(_.remove(PayingPublicPensionSchemePage))
            .flatMap(_.remove(StopPayingPublicPensionPage))
            .flatMap(_.remove(DefinedContributionPensionSchemePage))
            .flatMap(_.remove(FlexiblyAccessedPensionPage))
            .flatMap(_.remove(FlexibleAccessStartDatePage))
            .flatMap(_.remove(PayTaxCharge1415Page))
            .flatMap(_.remove(PIAPreRemedyPage(Period._2013)))
            .flatMap(_.remove(PIAPreRemedyPage(Period._2014)))
            .flatMap(_.remove(PIAPreRemedyPage(Period._2015)))
        } else {
          super.cleanup(value, userAnswers)
        }
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def periodAACleanup(answers: UserAnswers, periods: Seq[Period]): UserAnswers =
    periods.headOption match {
      case Some(period) => periodAACleanup(answers.remove(UserAnswersPeriod(period)).get, periods.tail)
      case None         => answers
    }
}
