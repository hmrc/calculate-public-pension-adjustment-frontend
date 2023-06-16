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

package models.tasklist

import models.{Period, UserAnswers}
import pages.Page
import pages.annualallowance.preaaquestions._

case object PreAASection extends Section {
  override def pages(): Seq[Page] = Seq(
    ScottishTaxpayerFrom2016Page,
    WhichYearsScottishTaxpayerPage,
    PayingPublicPensionSchemePage,
    StopPayingPublicPensionPage,
    DefinedContributionPensionSchemePage,
    FlexiblyAccessedPensionPage,
    FlexibleAccessStartDatePage,
    PayTaxCharge1516Page,
    PIAPreRemedyPage(Period._2013),
    PIAPreRemedyPage(Period._2014),
    PIAPreRemedyPage(Period._2015)
  )

  override def status(answers: UserAnswers): SectionStatus =
    if (answers.get(ScottishTaxpayerFrom2016Page).isDefined) {
      answers.get(PayTaxCharge1516Page) match {
        case Some(true)                                                           => SectionStatus.Completed
        case Some(false) if answers.get(PIAPreRemedyPage(Period._2015)).isDefined =>
          SectionStatus.Completed
        case _                                                                    => SectionStatus.InProgress
      }
    } else SectionStatus.NotStarted

  override def checkYourAnswersPage: Page = CheckYourAASetupAnswersPage

}
