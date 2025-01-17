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

import models.{NormalMode, PSTR, Period, SchemeIndex, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class WhichSchemePage(period: Period, schemeIndex: SchemeIndex) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ "schemes" \ schemeIndex.toString \ toString

  override def toString: String = "whichScheme"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val selectedScheme: Option[String] = answers.get(WhichSchemePage(period, schemeIndex))

    selectedScheme match {
      case Some(PSTR.New | PSTR.NewInWelsh) =>
        controllers.annualallowance.taxyear.routes.PensionSchemeDetailsController
          .onPageLoad(NormalMode, period, schemeIndex)
      case Some(_)                          =>
        period match {
          case Period._2016 =>
            controllers.annualallowance.taxyear.routes.PensionSchemeInput2016preAmountsController
              .onPageLoad(NormalMode, period, schemeIndex)
          case _            =>
            controllers.annualallowance.taxyear.routes.PensionSchemeInputAmountsController
              .onPageLoad(NormalMode, period, schemeIndex)
        }
      case None                             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    controllers.annualallowance.taxyear.routes.CheckYourAAPeriodAnswersController.onPageLoad(period)

  override def cleanup(value: Option[String], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case value if value.contains(PSTR.New) || value.contains(PSTR.NewInWelsh) =>
          userAnswers.remove(PensionSchemeDetailsPage(period, schemeIndex))
        case _                                                                    =>
          super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
