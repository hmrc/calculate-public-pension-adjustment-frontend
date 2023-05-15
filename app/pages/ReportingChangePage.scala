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

package pages

import controllers.routes
import models.{CheckMode, NormalMode, ReportingChange, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ReportingChangePage extends QuestionPage[Set[ReportingChange]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reportingChange"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(set) if set.contains(ReportingChange.AnnualAllowance)  =>
      routes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode)
    case Some(set) if !set.contains(ReportingChange.AnnualAllowance) => routes.CheckYourAnswersController.onPageLoad
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = answers.get(ReportingChangePage) match {
    case Some(set) if set.contains(ReportingChange.AnnualAllowance)  =>
      answers.get(ScottishTaxpayerFrom2016Page) match {
        case None        => routes.ScottishTaxpayerFrom2016Controller.onPageLoad(CheckMode)
        case Some(value) => routes.CheckYourAnswersController.onPageLoad
      }
    case Some(set) if !set.contains(ReportingChange.AnnualAllowance) => routes.CheckYourAnswersController.onPageLoad
  }

  override def cleanup(value: Option[Set[ReportingChange]], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { set =>
        if (set.contains(ReportingChange.AnnualAllowance)) {
          super.cleanup(value, userAnswers)
        } else { userAnswers.remove(ScottishTaxpayerFrom2016Page).flatMap(_.remove(WhichYearsScottishTaxpayerPage)) }
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
