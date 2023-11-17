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

package models.tasklist.sections

import controllers.annualallowance.preaaquestions.{routes => preAARoutes}
import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.{Section, SectionStatus}
import models.{NormalMode, Period, SectionNavigation, UserAnswers}
import pages.Page
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, FlexibleAccessStartDatePage, FlexiblyAccessedPensionPage, PIAPreRemedyPage, PayTaxCharge1415Page, PayingPublicPensionSchemePage, RegisteredYearPage, ScottishTaxpayerFrom2016Page, StopPayingPublicPensionPage, WhichYearsScottishTaxpayerPage}
import play.api.mvc.Call

case object PreAASection extends Section {

  def pages(): Seq[Page] = Seq(
    ScottishTaxpayerFrom2016Page,
    WhichYearsScottishTaxpayerPage,
    PayingPublicPensionSchemePage,
    StopPayingPublicPensionPage,
    DefinedContributionPensionSchemePage,
    FlexiblyAccessedPensionPage,
    FlexibleAccessStartDatePage,
    PayTaxCharge1415Page,
    RegisteredYearPage(Period._2011),
    PIAPreRemedyPage(Period._2011),
    RegisteredYearPage(Period._2012),
    PIAPreRemedyPage(Period._2012),
    RegisteredYearPage(Period._2013),
    RegisteredYearPage(Period._2011),
    PIAPreRemedyPage(Period._2011),
    RegisteredYearPage(Period._2012),
    PIAPreRemedyPage(Period._2012),
    RegisteredYearPage(Period._2013),
    PIAPreRemedyPage(Period._2013),
    RegisteredYearPage(Period._2014),
    PIAPreRemedyPage(Period._2014),
    RegisteredYearPage(Period._2015),
    PIAPreRemedyPage(Period._2015)
  )

  val initialPage: Call                 = preAARoutes.ScottishTaxpayerFrom2016Controller.onPageLoad(NormalMode)
  val checkYourAASetupAnswersPage: Call = preAARoutes.CheckYourAASetupAnswersController.onPageLoad()

  def status(answers: UserAnswers): SectionStatus =
    navigateTo(answers) match {
      case initialPage.url                 => NotStarted
      case checkYourAASetupAnswersPage.url => Completed
      case _                               => InProgress
    }

  private val sectionNavigation: SectionNavigation = SectionNavigation("preAASection")

  def navigateTo(answers: UserAnswers): String =
    answers.get(sectionNavigation).getOrElse(initialPage.url)

  def saveNavigation(answers: UserAnswers, urlFragment: String): UserAnswers =
    answers.set(sectionNavigation, urlFragment).get

  def removeAllUserAnswersAndNavigation(answers: UserAnswers) = remove(answers, pages()).remove(sectionNavigation).get
}
