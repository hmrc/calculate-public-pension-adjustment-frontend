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

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.sections.{NextStepsSection, PreAASection, SetupSection}
import models.{Period, ReportingChange, UserAnswers}
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, PIAPreRemedyPage, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page}
import pages.behaviours.PageBehaviours
import pages.setupquestions.{ReportingChangePage, ResubmittingAdjustmentPage, SavingsStatementPage}
import play.api.libs.json.Json
import play.api.mvc.Call

class SectionTest extends SpecBase with PageBehaviours {

  "Removing user answers from a section" - {

    "should succeed when user answers are incomplete" in {

      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.data must equal(Json.obj())
    }

    "should succeed when there are multiple answers" in {

      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get.set(ResubmittingAdjustmentPage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.data must equal(Json.obj())
    }

    "should not remove answers from another section" in {

      val userAnswers =
        emptyUserAnswers.set(SavingsStatementPage, true).get.set(DefinedContributionPensionSchemePage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.get(DefinedContributionPensionSchemePage) mustBe (Some(true))
    }

    "should succeed for a section with dynamically scoped data" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(PIAPreRemedyPage(Period._2013), BigInt(1))
        .get

      val userAnswersWithRemovals = PreAASection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.get(PIAPreRemedyPage(Period._2013)) mustBe None
    }
  }

  "Identifying a page" - {

    "should locate return to" in {

      val userAnswers: UserAnswers =
        emptyUserAnswers.set(ScottishTaxpayerFrom2016Page, false).get.set(PayingPublicPensionSchemePage, true).get

      val returnTo = PreAASection.navigateTo(userAnswers)
      returnTo must be(PayingPublicPensionSchemePage)
    }
  }

  "Next steps navigation" - {

    "Must route to calculation result page when reporting a change that includes Annual Allowance details" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val nextStepsTaskUrl = NextStepsSection.navigateTo(answers).url

      checkNavigation(nextStepsTaskUrl, "/calculation-result")
    }

    "Must proceed to LTA submission when reporting a change that does not include Annual Allowance details" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val nextStepsTaskUrl = NextStepsSection.navigateTo(answers).url

      checkNavigation(nextStepsTaskUrl, "/lta-submission")
    }

    "Must route to setup page when reporting change details have not been captured" in {
      val nextStepsTaskUrl = NextStepsSection.navigateTo(emptyUserAnswers).url

      checkNavigation(nextStepsTaskUrl, "/change-previous-adjustment")
    }
  }

  "Next steps section name" - {

    "Must be 'Calculate' when reporting a change that includes Annual Allowance details" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val sectionNameOverride = NextStepsSection.sectionNameOverride(answers)

      sectionNameOverride mustBe "taskList.nextSteps.calculate"
    }

    "Must be 'Continue to sign in' when reporting a change that does not include Annual Allowance details" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val sectionNameOverride = NextStepsSection.sectionNameOverride(answers)

      sectionNameOverride mustBe "taskList.nextSteps.continueToSignIn"
    }

    "Must be 'Complete setup questions' when reporting change details have not been captured" in {
      val sectionNameOverride = NextStepsSection.sectionNameOverride(emptyUserAnswers)

      sectionNameOverride mustBe "taskList.nextSteps.setupRequired"
    }
  }

  "Next steps section status" - {

    "Must be 'Cannot start yet' if not all data capture sections are complete" in {
      val dataCaptureSections = List(
        Some(
          SectionGroupViewModel(
            1,
            "heading",
            Seq(SectionViewModel("name", Call("GET", "url"), SectionStatus.Completed, "id"))
          )
        ),
        Some(
          SectionGroupViewModel(
            1,
            "heading",
            Seq(SectionViewModel("name", Call("GET", "url"), SectionStatus.InProgress, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections)
      sectionStatus mustBe SectionStatus.CannotStartYet
    }

    "Must be 'Not started' if all specified data capture sections are complete" in {
      val dataCaptureSections = List(
        Some(
          SectionGroupViewModel(
            1,
            "heading",
            Seq(SectionViewModel("name", Call("GET", "url"), SectionStatus.Completed, "id"))
          )
        ),
        Some(
          SectionGroupViewModel(
            1,
            "heading",
            Seq(SectionViewModel("name", Call("GET", "url"), SectionStatus.Completed, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections)
      sectionStatus mustBe SectionStatus.NotStarted
    }
  }

}
