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

import models.{NormalMode, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import pages.annualallowance.preaaquestions.DefinedContributionPensionSchemePage
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.SchemeService

import scala.util.Try

case class MemberOfPublicPensionSchemePage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "memberOfPublicPensionScheme"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(MemberOfPublicPensionSchemePage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.MemberMoreThanOnePensionController.onPageLoad(NormalMode, period)
      case Some(false) =>
        navigateWhenNotMemberOfPublicPensionScheme(answers)
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateWhenNotMemberOfPublicPensionScheme(answers: UserAnswers) =
    answers.get(DefinedContributionPensionSchemePage) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.OtherDefinedBenefitOrContributionController
          .onPageLoad(NormalMode, period)
      case Some(false) =>
        navigateWhenNotMemberOfPublicSchemeAndNoDC
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateWhenNotMemberOfPublicSchemeAndNoDC =
    period match {
      case Period._2016PreAlignment  =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case Period._2016PostAlignment =>
        controllers.annualallowance.taxyear.routes.TotalIncomeController.onPageLoad(NormalMode, period)
      case Period.Year(_)            =>
        controllers.annualallowance.taxyear.routes.ThresholdIncomeController.onPageLoad(NormalMode, period)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(MemberOfPublicPensionSchemePage(period)) match {
      case Some(true)  =>
        controllers.annualallowance.taxyear.routes.MemberMoreThanOnePensionController.onPageLoad(NormalMode, period)
      case Some(false) =>
        controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)
      case None        => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }


  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val indeciesToClean = SchemeService.allSchemeIndices
    value
      .map {
        case false =>
          Try(schemeIndexCleanup(userAnswers, indeciesToClean, period))
        case true =>
          super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }

  private def schemeIndexCleanup(answers: UserAnswers, schemeIndecies: Seq[SchemeIndex], period: Period): UserAnswers =
    schemeIndecies.headOption match {
      case Some(schemeIndex: SchemeIndex) =>
        schemeIndexCleanup(
          answers
            .remove(MemberMoreThanOnePensionPage(period))
            .flatMap(_.remove(WhichSchemePage(period, schemeIndex)))
            .flatMap(_.remove(PensionSchemeDetailsPage(period, schemeIndex)))
            .flatMap(_.remove(PensionSchemeInputAmountsPage(period, schemeIndex)))
            .flatMap(_.remove(PayAChargePage(period, schemeIndex)))
            .flatMap(_.remove(WhoPaidAAChargePage(period, schemeIndex)))
            .flatMap(_.remove(HowMuchAAChargeYouPaidPage(period, schemeIndex)))
            .flatMap(_.remove(HowMuchAAChargeSchemePaidPage(period, schemeIndex)))
            .flatMap(_.remove(AddAnotherSchemePage(period, schemeIndex)))
            .get,
          schemeIndecies.tail, period
        )
      case None => answers
    }
}
