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

package models.tasklist

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.sections.{LTASection, NextStepsSection}
import models.{ReportingChange, UserAnswers}
import pages.behaviours.PageBehaviours
import pages.setupquestions.ReportingChangePage

class NextStepsSectionSpec extends SpecBase with PageBehaviours {

  "Next steps navigation" - {

    "Must route to calculation result page when reporting a change that includes Annual Allowance details" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val nextStepsTaskUrl = NextStepsSection.navigateTo(answers)

      checkNavigation(nextStepsTaskUrl, "/calculation-result")
    }

    "Must route to LTA submission when reporting a change for LTA only" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val nextStepsTaskUrl = NextStepsSection.navigateTo(answers)

      checkNavigation(nextStepsTaskUrl, "/lta-submission")
    }

    "Must route to a page in the Setup Section when reporting change details have not been captured" in {
      val nextStepsTaskUrl = NextStepsSection.navigateTo(emptyUserAnswers)

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

    "Must be 'Continue to sign in' when reporting a change that does not include Annual Allowance details & no kickout reached" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

      val sectionNameOverride = NextStepsSection.sectionNameOverride(answers)

      sectionNameOverride mustBe "taskList.nextSteps.continueToSignIn"
    }

    "Must be 'No further action required' when reporting a change that does not include Annual Allowance details & kickout was reached" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers
        .set(ReportingChangePage, reportingChanges)
        .get

      val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

      val sectionNameOverride = NextStepsSection.sectionNameOverride(answersWithNav)

      sectionNameOverride mustBe "taskList.nextSteps.noFurtherAction"
    }

    "Must be 'Complete setup questions' when reporting change details have not been captured" in {
      val sectionNameOverride = NextStepsSection.sectionNameOverride(emptyUserAnswers)

      sectionNameOverride mustBe "taskList.nextSteps.setupRequired"
    }
  }

  "Next steps section status" - {

    "Must be 'Cannot start yet' if not all data capture sections are complete" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val dataCaptureSections                    = List(
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
          )
        ),
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.InProgress, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections, answers)
      sectionStatus mustBe SectionStatus.CannotStartYet
    }

    "Must be 'Not started' if all specified data capture sections are complete and only LTA is selected" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val dataCaptureSections                    = List(
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections, answers)
      sectionStatus mustBe SectionStatus.NotStarted
    }

    "Must be 'CannotStartYet' if all specified data capture sections are complete, only LTA is selected & a kickout page has been entered" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers
        .set(ReportingChangePage, reportingChanges)
        .get

      val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

      val dataCaptureSections = List(
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections, answersWithNav)
      sectionStatus mustBe SectionStatus.CannotStartYet
    }

    "Must be 'Not started' if all specified data capture sections are complete and annual allowance is not selected and no kickout has been reached" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
      val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val dataCaptureSections                    = List(
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
          )
        ),
        Some(
          SectionGroupViewModel(
            "heading",
            Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
          )
        ),
        None
      )

      val sectionStatus = NextStepsSection.sectionStatus(dataCaptureSections, answers)
      sectionStatus mustBe SectionStatus.NotStarted
    }
  }

}
