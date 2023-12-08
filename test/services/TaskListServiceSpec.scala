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
import models.tasklist.sections.{LTASection, PreAASection, SetupSection}
import models.tasklist.{SectionGroupViewModel, SectionStatus, TaskListViewModel}
import models.{NormalMode, ReportingChange, UserAnswers}
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.behaviours.PageBehaviours
import pages.lifetimeallowance.HadBenefitCrystallisationEventPage
import pages.setupquestions.{ReportingChangePage, SavingsStatementPage}

import java.time.LocalDate
import scala.util.Try

class TaskListServiceSpec extends SpecBase with PageBehaviours {

  val taskListService: TaskListService = new TaskListService

  def urlWithNoContext(url: String): String = url.replace("/public-pension-adjustment", "")

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
      val taskListViewModel: TaskListViewModel =
        taskListService.taskListViewModel(userAnswersAfterSavingsStatementPageSubmitted())
      val setupGroup: SectionGroupViewModel    = taskListViewModel.setupGroup

      setupGroup.heading       must be("taskList.setup.groupHeading")
      setupGroup.sections.size must be(1)
      setupGroup.isComplete    must be(false)

      val setupSection = setupGroup.sections(0)
      setupSection.status                must be(SectionStatus.InProgress)
      urlWithNoContext(setupSection.url) must be("/sign-in-government-gateway")
    }

    "the nextStepsGroup must be well formed" in {
      val taskListViewModel: TaskListViewModel  =
        taskListService.taskListViewModel(userAnswersAfterSavingsStatementPageSubmitted())
      val nextStepsGroup: SectionGroupViewModel = taskListViewModel.nextStepsGroup

      nextStepsGroup.heading       must be("taskList.nextSteps.groupHeading")
      nextStepsGroup.sections.size must be(1)
      nextStepsGroup.isComplete    must be(false)

      val nextStepsSection = nextStepsGroup.sections(0)
      nextStepsSection.status must be(SectionStatus.CannotStartYet)
    }

    "the ltaGroup and aaGroup must not be defined" in {
      val taskListViewModel: TaskListViewModel =
        taskListService.taskListViewModel(userAnswersAfterSavingsStatementPageSubmitted())
      taskListViewModel.aaGroup.isDefined  must be(false)
      taskListViewModel.ltaGroup.isDefined must be(false)
    }

    def userAnswersAfterSavingsStatementPageSubmitted(): UserAnswers = {
      val answersWithPageData = emptyUserAnswers
        .set(SavingsStatementPage(true), true)
        .get

      val redirectedUrl = SavingsStatementPage(true).navigate(NormalMode, answersWithPageData).url
      SetupSection.saveNavigation(answersWithPageData, redirectedUrl)
    }
  }

  "given aa is being reported and aa setup is complete" - {

    "the aa group must be well formed" in {
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
      val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

      aaGroup.sections.size must be(2)
      aaGroup.heading       must be("taskList.aa.groupHeading")
      aaGroup.isComplete    must be(false)
    }

    "the aa background section must be well formed" in {
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
      val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

      val aaBackgroundSection = aaGroup.sections(0)
      aaBackgroundSection.status                must be(SectionStatus.Completed)
      urlWithNoContext(aaBackgroundSection.url) must be("/annual-allowance/setup-check-answers")
    }

    "the aa period section must be well formed" in {
      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
      val aaGroup                              = taskListViewModel.aaGroup.get

      val periodSection = aaGroup.sections(1)
      periodSection.status                must be(SectionStatus.NotStarted)
      urlWithNoContext(periodSection.url) must be("/annual-allowance/2016-pre/information")
    }

    def userAnswersWhenAAPreReqsSatisfied(): UserAnswers = {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answersWithPageData                    = emptyUserAnswers
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
        .get

      val answersWithSetupNav   =
        SetupSection.saveNavigation(answersWithPageData, SetupSection.checkYourSetupAnswersPage.url)
      val answersWithAASetupNav =
        PreAASection.saveNavigation(answersWithSetupNav, PreAASection.checkYourAASetupAnswersPage.url)

      answersWithAASetupNav
    }
  }

  "given lta is being reported the user has navigated to another page" - {

    val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
    val answers                                = emptyUserAnswers
      .set(ReportingChangePage, reportingChanges)
      .get

    val answersWithNav = LTASection.saveNavigation(answers, "some-url")

    val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answersWithNav)
    val ltaGroup                             = taskListViewModel.ltaGroup.get

    "the lta group must be well formed" in {
      ltaGroup.heading       must be("taskList.lta.groupHeading")
      ltaGroup.isComplete    must be(false)
      ltaGroup.sections.size must be(1)
    }

    "the lta section must be in progress with the correct url" in {
      val ltaSection = ltaGroup.sections(0)

      ltaSection.status                must be(SectionStatus.InProgress)
      urlWithNoContext(ltaSection.url) must be("some-url")
    }
  }
}
