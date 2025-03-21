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

package pages.setupquestions.annualallowance

import models.tasklist.sections.{AASection, PreAASection}
import models.{NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object PensionProtectedMemberPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "aa" \ toString

  override def toString: String = "pensionProtectedMember"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(PensionProtectedMemberPage), answers.get(SavingsStatementPage)) match {
      case (Some(false), Some(_))    =>
        controllers.setupquestions.annualallowance.routes.HadAAChargeController.onPageLoad(NormalMode)
      case (Some(true), Some(false)) =>
        controllers.setupquestions.annualallowance.routes.NotAbleToUseThisServiceAAController.onPageLoad()
      case (Some(true), Some(true))  =>
        controllers.setupquestions.annualallowance.routes.PIAAboveAnnualAllowanceIn2023Controller.onPageLoad(NormalMode)
      case _                         => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    (answers.get(PensionProtectedMemberPage), answers.get(SavingsStatementPage)) match {
      case (Some(false), Some(_))    =>
        controllers.setupquestions.annualallowance.routes.HadAAChargeController.onPageLoad(NormalMode)
      case (Some(true), Some(false)) =>
        controllers.setupquestions.annualallowance.routes.NotAbleToUseThisServiceAAController.onPageLoad()
      case (Some(true), Some(true))  =>
        controllers.setupquestions.annualallowance.routes.PIAAboveAnnualAllowanceIn2023Controller.onPageLoad(NormalMode)
      case _                         => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case true  =>
          userAnswers
            .get(SavingsStatementPage)
            .map {
              case false =>
                for {
                  answersNoAASetup <- Try(PreAASection.removeAllUserAnswersAndNavigation(userAnswers))
                  answersNoAATask  <- Try(AASection.removeAllAAPeriodAnswersAndNavigation(answersNoAASetup))
                } yield triageAAPages(answersNoAATask).get
              case _     => triageAAPages(userAnswers)
            }
            .getOrElse(super.cleanup(value, userAnswers))
        case false => triageAAPages(userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def triageAAPages(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers
      .remove(HadAAChargePage)
      .flatMap(_.remove(ContributionRefundsPage))
      .flatMap(_.remove(NetIncomeAbove100KPage))
      .flatMap(_.remove(NetIncomeAbove190KPage))
      .flatMap(_.remove(MaybePIAIncreasePage))
      .flatMap(_.remove(MaybePIAUnchangedOrDecreasedPage))
      .flatMap(_.remove(PIAAboveAnnualAllowanceIn2023Page))
      .flatMap(_.remove(NetIncomeAbove190KIn2023Page))
      .flatMap(_.remove(FlexibleAccessDcSchemePage))
      .flatMap(_.remove(Contribution4000ToDirectContributionSchemePage))
}
