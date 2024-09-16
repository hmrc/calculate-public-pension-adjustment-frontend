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
import models.{LTAKickOutStatus, NormalMode, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object NetIncomeAbove190KPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "setup" \ "aa" \ toString

  override def toString: String = "netIncomeAbove190K"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    (answers.get(NetIncomeAbove190KPage), answers.get(SavingsStatementPage)) match {
      case (Some(true), Some(true))  =>
        answers.get(LTAKickOutStatus()).getOrElse(None) match {
          case 0    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case 1    =>
            controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
              .onPageLoad(NormalMode)
          case 2    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case _    => controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      case (Some(false), Some(true)) =>
        controllers.setupquestions.annualallowance.routes.MaybePIAIncreaseController.onPageLoad(NormalMode)
      case (Some(_), Some(_))        =>
        controllers.setupquestions.annualallowance.routes.NotAbleToUseThisServiceAAController.onPageLoad()
      case _                         => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    (answers.get(NetIncomeAbove190KPage), answers.get(SavingsStatementPage)) match {
      case (Some(true), Some(true))  =>
        answers.get(LTAKickOutStatus()).getOrElse(None) match {
          case 0    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case 1    =>
            controllers.setupquestions.lifetimeallowance.routes.HadBenefitCrystallisationEventController
              .onPageLoad(NormalMode)
          case 2    => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case None => controllers.setupquestions.routes.CheckYourSetupAnswersController.onPageLoad()
          case _    => controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      case (Some(false), Some(true)) =>
        controllers.setupquestions.annualallowance.routes.MaybePIAIncreaseController.onPageLoad(NormalMode)
      case (Some(_), Some(_))        =>
        controllers.setupquestions.annualallowance.routes.NotAbleToUseThisServiceAAController.onPageLoad()
      case _                         => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { _ =>
        userAnswers
          .get(SavingsStatementPage)
          .map {
            case true  =>
              triageAAPages(userAnswers)
            case false =>
              removeAAData(triageAAPages(userAnswers).get)
            case _     =>
              super.cleanup(value, userAnswers)
          }
          .getOrElse(super.cleanup(value, userAnswers))
      }
      .getOrElse(super.cleanup(value, userAnswers))

  private def removeAAData(userAnswers: UserAnswers) =
    for {
      answersNoAASetup <- Try(PreAASection.removeAllUserAnswersAndNavigation(userAnswers))
      answersNoAATask  <- Try(AASection.removeAllAAPeriodAnswersAndNavigation(answersNoAASetup))
    } yield answersNoAATask

  private def triageAAPages(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers
      .remove(MaybePIAIncreasePage)
      .flatMap(_.remove(MaybePIAUnchangedOrDecreasedPage))
      .flatMap(_.remove(PIAAboveAnnualAllowanceIn2023Page))
      .flatMap(_.remove(NetIncomeAbove190KIn2023Page))
      .flatMap(_.remove(FlexibleAccessDcSchemePage))
      .flatMap(_.remove(Contribution4000ToDirectContributionSchemePage))
}
