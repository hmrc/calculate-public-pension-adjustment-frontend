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
import config.FrontendAppConfig
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.sections.{LTASection, NextStepsSection}
import models.{AAKickOutStatus, LTAKickOutStatus, PostTriageFlag, ReportingChange, UserAnswers}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.behaviours.PageBehaviours
import pages.setupquestions.ReportingChangePage

class NextStepsSectionSpec extends SpecBase with PageBehaviours with MockitoSugar {

  val mockFrontEndAppConfig = mock[FrontendAppConfig]
  val nextStepsSection      = new NextStepsSection(mockFrontEndAppConfig)

  "Next steps navigation" - {

    "pre triage" - {

      "Must route to calculation review page when reporting a change that includes Annual Allowance details when" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

        val nextStepsTaskUrl = nextStepsSection.navigateTo(answers)

        checkNavigation(nextStepsTaskUrl, "/calculation-results")
      }

      "Must route to LTA submission when reporting a change for LTA only" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

        val nextStepsTaskUrl = nextStepsSection.navigateTo(answers)

        checkNavigation(nextStepsTaskUrl, "/lta-submission")
      }

      "Must route to a page in the Setup Section when reporting change details have not been captured" in {
        val nextStepsTaskUrl = nextStepsSection.navigateTo(emptyUserAnswers)

        checkNavigation(nextStepsTaskUrl, "/change-previous-adjustment")
      }
    }

    "post triage" - {

      "Must route to calculation review page when reporting a change that includes Annual Allowance details" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 2)
          .get

        val nextStepsTaskUrl = nextStepsSection.navigateTo(answers)

        checkNavigation(nextStepsTaskUrl, "/calculation-results")
      }

      "Must route to LTA submission when reporting a change for LTA only" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get

        val nextStepsTaskUrl = nextStepsSection.navigateTo(answers)

        checkNavigation(nextStepsTaskUrl, "/lta-submission")
      }

      "Must route to LTA submission when reporting a change for AA and LTA but only LTA eligible" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 0)
          .get
          .set(LTAKickOutStatus(), 1)
          .get

        val nextStepsTaskUrl = nextStepsSection.navigateTo(answers)

        checkNavigation(nextStepsTaskUrl, "/lta-submission")
      }

      "Must route to a page in the Setup Section when reporting change details have not been captured" in {
        val nextStepsTaskUrl = nextStepsSection.navigateTo(
          emptyUserAnswers
            .set(PostTriageFlag, true)
            .get
        )

        checkNavigation(nextStepsTaskUrl, "/change-previous-adjustment")
      }
    }
  }

  "Next steps section name" - {

    "pre triage" - {

      "Must be 'Calculate' when reporting a change that includes Annual Allowance details" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.calculate"
      }

      "Must be 'Continue to sign in' when reporting a change that does not include Annual Allowance details & no kickout reached and not authenticated" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continueToSignIn"
      }

      "Must be 'Continue' when reporting a change that does not include Annual Allowance details & no kickout reached and authenticated" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .copy(authenticated = true)
          .set(ReportingChangePage, reportingChanges)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continue"
      }

      "Must be 'No further action required' when reporting a change that does not include Annual Allowance details & kickout was reached" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(ReportingChangePage, reportingChanges)
          .get

        val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answersWithNav)
        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'Complete setup questions' when reporting change details have not been captured" in {
        val sectionNameOverride = nextStepsSection.sectionNameOverride(emptyUserAnswers)

        sectionNameOverride `mustBe` "taskList.nextSteps.setupRequired"
      }
    }

    "post triage" - {

      "Must be 'Calculate' when AA eligible for AA" in {
        val answers: UserAnswers = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(AAKickOutStatus(), 2)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.calculate"
      }

      "Must be 'Continue' when not eligible for AA, eligible for LTA and authenticated" in {
        val answers: UserAnswers = emptyUserAnswers
          .copy(authenticated = true)
          .set(PostTriageFlag, true)
          .get
          .set(AAKickOutStatus(), 0)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continue"
      }

      "Must be 'Continue' when AA not reported, eligible for LTA and authenticated" in {
        val answers: UserAnswers = emptyUserAnswers
          .copy(authenticated = true)
          .set(PostTriageFlag, true)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continue"
      }

      "Must be 'Continue to sign in' when not eligible for AA, eligible for LTA and not authenticated" in {
        val answers: UserAnswers = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(AAKickOutStatus(), 0)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continueToSignIn"
      }

      "Must be 'Continue to sign in' AA not reported, eligible for LTA and not authenticated" in {
        val answers: UserAnswers = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.continueToSignIn"
      }

      "Must be 'No further action required' when not eligible for LTA or AA" in {
        val answers: UserAnswers = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(AAKickOutStatus(), 0)
          .get
          .set(LTAKickOutStatus(), 0)
          .get

        val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answersWithNav)

        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'No further action required' when when not eligible for AA and only AA indicated" in {
        val reportingChanges: Set[ReportingChange] = Set(ReportingChange.AnnualAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 0)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'No further action required' when when not eligible for LTA and only LTA indicated" in {
        val reportingChanges: Set[ReportingChange] = Set(ReportingChange.LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(LTAKickOutStatus(), 0)
          .get

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'No further action required' when not eligible for AA and LTA kickout has been reached " in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 0)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answersWithNav)

        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'No further action required' when only LTA and LTA kickout has been reached " in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val answersWithNav = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)

        val sectionNameOverride = nextStepsSection.sectionNameOverride(answersWithNav)

        sectionNameOverride `mustBe` "taskList.nextSteps.noFurtherAction"
      }

      "Must be 'Complete setup questions' when reporting change details have not been captured" in {
        val sectionNameOverride = nextStepsSection.sectionNameOverride(
          emptyUserAnswers
            .set(PostTriageFlag, true)
            .get
        )

        sectionNameOverride `mustBe` "taskList.nextSteps.setupRequired"
      }

      "Must be 'Complete setup questions' when AA or LTA Triage in progress" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 1)
          .get
          .set(LTAKickOutStatus(), 1)
          .get
        val sectionNameOverride                    = nextStepsSection.sectionNameOverride(answers)

        sectionNameOverride `mustBe` "taskList.nextSteps.setupRequired"
      }
    }
  }

  "Next steps section status" - {

    "pre triage" - {

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
        val sectionStatus                          = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.CannotStartYet
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
        val sectionStatus                          = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.NotStarted
      }

      "Must be 'CannotStartYet' if all specified data capture sections are complete, only LTA is selected & a kickout page has been entered" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(ReportingChangePage, reportingChanges)
          .get
        val answersWithNav                         = LTASection.saveNavigation(answers, LTASection.cannotUseLtaServiceNoChargePage.url)
        val dataCaptureSections                    = List(
          Some(
            SectionGroupViewModel(
              "heading",
              Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
            )
          ),
          None
        )
        val sectionStatus                          = nextStepsSection.sectionStatus(dataCaptureSections, answersWithNav)
        sectionStatus `mustBe` SectionStatus.CannotStartYet
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
        val sectionStatus                          = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.NotStarted
      }
    }

    "post triage" - {

      "Must be 'Cannot start yet' if not all data capture sections are complete" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
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

        val sectionStatus = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.CannotStartYet
      }

      "Must be 'Not started' if all specified data capture sections are complete and only LTA is selected" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(LTAKickOutStatus(), 2)
          .get
        val dataCaptureSections                    = List(
          Some(
            SectionGroupViewModel(
              "heading",
              Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
            )
          ),
          None
        )

        val sectionStatus = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.NotStarted
      }

      "Must be 'CannotStartYet' if all specified data capture sections are complete, only LTA is selected & a LTA task kickout page has been entered" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
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

        val sectionStatus = nextStepsSection.sectionStatus(dataCaptureSections, answersWithNav)
        sectionStatus `mustBe` SectionStatus.CannotStartYet
      }

      "Must be 'CannotStartYet' if all specified data capture sections are complete, only LTA is selected & LTA triage kickout page has been entered" in {
        val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(LTAKickOutStatus(), 0)
          .get

        val dataCaptureSections = List(
          Some(
            SectionGroupViewModel(
              "heading",
              Seq(SectionViewModel("name", "url", SectionStatus.Completed, "id"))
            )
          ),
          None
        )

        val sectionStatus = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.CannotStartYet
      }

      "Must be 'Not started' if all specified data capture sections are complete and annual allowance is not selected and no kickout has been reached" in {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance, LifetimeAllowance)
        val answers: UserAnswers                   = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(AAKickOutStatus(), 2)
          .get
          .set(LTAKickOutStatus(), 2)
          .get

        val dataCaptureSections = List(
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

        val sectionStatus = nextStepsSection.sectionStatus(dataCaptureSections, answers)
        sectionStatus `mustBe` SectionStatus.NotStarted
      }
    }
  }
}
