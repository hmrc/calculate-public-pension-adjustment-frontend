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

package services

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.{SectionGroupViewModel, SectionStatus, SectionViewModel, TaskListViewModel}
import models.{Period, ReportingChange, UserAnswers}
import pages.annualallowance.preaaquestions.ScottishTaxpayerFrom2016Page
import pages.annualallowance.taxyear.MemberMoreThanOnePensionPage
import pages.behaviours.PageBehaviours
import pages.lifetimeallowance.HadBenefitCrystallisationEventPage
import pages.setupquestions.{ReportingChangePage, SavingsStatementPage}

import scala.util.Try

class TaskListSpec extends SpecBase with PageBehaviours {

  val taskListService: TaskListService = new TaskListService

  "the aa group must be defined if it is included in the changes being reported" in {
    val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
    val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

    taskListViewModel.aaGroup.isDefined must be(true)
  }

  "the aa group must not be defined if it is not included in the changes being reported" in {
    val reportingChanges: Set[ReportingChange] = Set()
    val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

    taskListViewModel.aaGroup.isDefined must be(false)
  }

  "the lta group must be defined if it is included in the changes being reported" in {
    val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
    val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

    taskListViewModel.ltaGroup.isDefined must be(true)
  }

  "the lta group must not be defined if it is not included in the changes being reported" in {
    val reportingChanges: Set[ReportingChange] = Set()
    val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

    taskListViewModel.ltaGroup.isDefined must be(false)
  }

  "given only the first page has been submitted and the view model is built" - {

    "the setupGroup must be well formed" in {
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(minimalUserAnswers)
      val setupGroup: SectionGroupViewModel    = taskListViewModel.setupGroup

      setupGroup.heading       must be("taskList.setup.groupHeading")
      setupGroup.sections.size must be(1)
      setupGroup.isComplete    must be(false)

      val setupSection = setupGroup.sections(0)
      setupSection.status   must be(SectionStatus.InProgress)
      setupSection.call.url must be("/change-previous-adjustment")
    }

    "the nextStepsGroup must be well formed" in {
      val taskListViewModel: TaskListViewModel  = taskListService.taskListViewModel(minimalUserAnswers)
      val nextStepsGroup: SectionGroupViewModel = taskListViewModel.nextStepsGroup

      nextStepsGroup.heading       must be("taskList.nextSteps.groupHeading")
      nextStepsGroup.sections.size must be(1)
      nextStepsGroup.isComplete    must be(false)

      val nextStepsSection = nextStepsGroup.sections(0)
      nextStepsSection.status must be(SectionStatus.CannotStartYet)
    }

    "the ltaGroup and aaGroup must not be defined" in {
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(minimalUserAnswers)
      taskListViewModel.aaGroup.isDefined  must be(false)
      taskListViewModel.ltaGroup.isDefined must be(false)
    }

    def minimalUserAnswers =
      emptyUserAnswers.set(SavingsStatementPage, true).get
  }

  "given aa is being reported and no aa user answers have been provided" - {

    "the aa group must be well formed" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val taskListViewModel: TaskListViewModel   = taskListService.taskListViewModel(answers)
      val aaGroup                                = taskListViewModel.aaGroup.get

      aaGroup.heading    must be("taskList.aa.groupHeading")
      aaGroup.isComplete must be(false)
    }

    "the aa background section must be well formed" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val taskListViewModel: TaskListViewModel   = taskListService.taskListViewModel(answers)
      val aaGroup                                = taskListViewModel.aaGroup.get

      val numberOfRemedyPeriods = PeriodService.allRemedyPeriods.size

      val aaBackgroundSection = aaGroup.sections(0)
      aaBackgroundSection.status   must be(SectionStatus.NotStarted)
      aaBackgroundSection.call.url must be("/annual-allowance/scottish-taxpayer")

      val periodSections = aaGroup.sections.slice(1, numberOfRemedyPeriods)
      periodSections.map { periodSection =>
        periodSection.status must be(SectionStatus.NotStarted)
        checkPeriodSectionUrl(periodSection)
      }
    }

    "the aa period sections must be well formed" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                = emptyUserAnswers.set(ReportingChangePage, reportingChanges).get
      val taskListViewModel: TaskListViewModel   = taskListService.taskListViewModel(answers)
      val aaGroup                                = taskListViewModel.aaGroup.get

      val numberOfRemedyPeriods = PeriodService.allRemedyPeriods.size

      aaGroup.sections.size must be(numberOfRemedyPeriods + 1)

      val aaBackgroundSection = aaGroup.sections(0)
      aaBackgroundSection.status   must be(SectionStatus.NotStarted)
      aaBackgroundSection.call.url must be("/annual-allowance/scottish-taxpayer")

      val periodSections = aaGroup.sections.slice(1, numberOfRemedyPeriods)
      periodSections.map { periodSection =>
        periodSection.status must be(SectionStatus.NotStarted)
        checkPeriodSectionUrl(periodSection)
      }
    }

    def checkPeriodSectionUrl(periodSection: SectionViewModel) = {
      val periodSectionUrlParts = periodSection.call.url.split("/")

      periodSectionUrlParts(0) must be("")
      periodSectionUrlParts(1) must be("annual-allowance")

      val periodIdentifier       = periodSectionUrlParts(2)
      val period: Option[Period] = Period.fromString(periodIdentifier)
      period.isDefined must be(true)

      periodSectionUrlParts(3) must be("information")
    }
  }

  "given aa is being reported and the first page in a section has been answered" - {

    "the aa background section must be in progress with the correct url" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                =
        emptyUserAnswers.set(ReportingChangePage, reportingChanges).get.set(ScottishTaxpayerFrom2016Page, true).get

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers)
      val aaBackgroundSection                  = taskListViewModel.aaGroup.get.sections(0)

      aaBackgroundSection.status   must be(SectionStatus.InProgress)
      aaBackgroundSection.call.url must be("/annual-allowance/scottish-taxpayer-years")
    }

    "an aa period section must be in progress with the correct url" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers                                = emptyUserAnswers
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(MemberMoreThanOnePensionPage(Period._2016PreAlignment), true)
        .get

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers)
      val aaPeriodSection                      = taskListViewModel.aaGroup.get.sections(1)

      aaPeriodSection.status   must be(SectionStatus.InProgress)
      // TODO the url is linking to the first page in the section - it should link to the first unanswered page
      aaPeriodSection.call.url must be("/annual-allowance/2016-pre/information")
    }
  }

  "given lta is being reported and the first page has been answered" - {

    val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
    val answers                                = emptyUserAnswers
      .set(ReportingChangePage, reportingChanges)
      .get
      .set(HadBenefitCrystallisationEventPage, true)
      .get

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers)
    val ltaGroup                             = taskListViewModel.ltaGroup.get

    "the lta group must be well formed" in {
      ltaGroup.heading       must be("taskList.lta.groupHeading")
      ltaGroup.isComplete    must be(false)
      ltaGroup.sections.size must be(1)
    }

    "the lta section must be in progress with the correct url" in {
      val ltaSection = ltaGroup.sections(0)

      ltaSection.status   must be(SectionStatus.InProgress)
      // TODO the url is linking to the first page in the section - it should link to the first unanswered page
      ltaSection.call.url must be("/lifetime-allowance")
    }
  }
}
