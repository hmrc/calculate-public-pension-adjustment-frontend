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

package services

import base.SpecBase
import models.ReportingChange.{AnnualAllowance, LifetimeAllowance}
import models.tasklist.sections.{LTASection, NextStepsSection, PreAASection, SetupSection}
import models.tasklist.{SectionGroupViewModel, SectionStatus, TaskListViewModel}
import models.{AAKickOutStatus, LTAKickOutStatus, NormalMode, PostTriageFlag, ReportingChange, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.annualallowance.preaaquestions.StopPayingPublicPensionPage
import pages.behaviours.PageBehaviours
import pages.setupquestions.{ReportingChangePage, ResubmittingAdjustmentPage}

import java.time.LocalDate
import scala.util.Try

class TaskListServiceSpec extends SpecBase with PageBehaviours with MockitoSugar {

  val mockNextStepsSection = mock[NextStepsSection]
  val taskListService      = new TaskListService(mockNextStepsSection)
  when(mockNextStepsSection.sectionStatus(any(), any())).`thenReturn`(SectionStatus.CannotStartYet)

  def urlWithNoContext(url: String): String = url.replace("/public-pension-adjustment", "")

  "pre triage" - {

    "the aa group must be defined if it is included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.aaGroup.isDefined `must` `be`(true)
    }

    "the aa group must not be defined if it is not included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set()
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)
      val taskListViewModel: TaskListViewModel   = taskListService.taskListViewModel(answers.get)
      taskListViewModel.aaGroup.isDefined `must` `be`(false)
    }

    "the lta group must be defined if it is included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.ltaGroup.isDefined `must` `be`(true)
    }

    "the lta group must not be defined if it is not included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set()
      val answers: Try[UserAnswers]              = emptyUserAnswers.set(ReportingChangePage, reportingChanges)
      val taskListViewModel: TaskListViewModel   = taskListService.taskListViewModel(answers.get)
      taskListViewModel.ltaGroup.isDefined `must` `be`(false)
    }
  }

  "post triage" - {

    "the aa group must be defined if it is included in the changes being reported and AAKickoutStatus = 2" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(AAKickOutStatus(), 2)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.aaGroup.isDefined `must` `be`(true)
    }

    "the aa group must not be defined if it is included in the changes being reported and AAKickoutStatus = 1 or 0" in {
      val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(AAKickOutStatus(), Gen.oneOf(Seq(0, 1)).sample.get)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.aaGroup.isDefined `must` `be`(false)
    }

    "the aa group must not be defined if it is not included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set()
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.aaGroup.isDefined `must` `be`(false)
    }

    "the lta group must be defined if it is included in the changes being reported and LTAKickoutStatus = 2 " in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(LTAKickOutStatus(), 2)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.ltaGroup.isDefined `must` `be`(true)
    }

    "the lta group must not be defined if it is included in the changes being reported and LTAKickoutStatus = 1 or 2 " in {
      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(LTAKickOutStatus(), Gen.oneOf(Seq(0, 1)).sample.get)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.ltaGroup.isDefined `must` `be`(false)
    }

    "the lta group must not be defined if it is not included in the changes being reported" in {
      val reportingChanges: Set[ReportingChange] = Set()
      val answers: Try[UserAnswers]              = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answers.get)

      taskListViewModel.ltaGroup.isDefined `must` `be`(false)
    }
  }

  "given only the first page has been submitted and the view model is built" - {

    "pre triage" - {

      "the setupGroup must be well formed" in {

        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())

        val setupGroup: SectionGroupViewModel = taskListViewModel.setupGroup

        setupGroup.heading       `must` `be`("taskList.setup.groupHeading")
        setupGroup.sections.size `must` `be`(1)
        setupGroup.isComplete    `must` `be`(false)

        val setupSection = setupGroup.sections(0)
        setupSection.status                `must` `be`(SectionStatus.InProgress)
        urlWithNoContext(setupSection.url) `must` `be`("/change-reason")
      }

      "the nextStepsGroup must be well formed" in {

        val taskListViewModel: TaskListViewModel  =
          taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())
        val nextStepsGroup: SectionGroupViewModel = taskListViewModel.nextStepsGroup

        nextStepsGroup.heading       `must` `be`("taskList.nextSteps.groupHeading")
        nextStepsGroup.sections.size `must` `be`(1)
        nextStepsGroup.isComplete    `must` `be`(false)

        val nextStepsSection = nextStepsGroup.sections(0)
        nextStepsSection.status `must` `be`(SectionStatus.CannotStartYet)
      }

      "the ltaGroup and aaGroup must not be defined" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())
        taskListViewModel.aaGroup.isDefined  `must` `be`(false)
        taskListViewModel.ltaGroup.isDefined `must` `be`(false)
      }

      def userAnswersAferResubmittingAdjustmentSubmitted(): UserAnswers = {

        val answersWithPageData = emptyUserAnswers
          .set(ResubmittingAdjustmentPage, true)
          .get

        val redirectedUrl = ResubmittingAdjustmentPage.navigate(NormalMode, answersWithPageData).url
        SetupSection.saveNavigation(answersWithPageData, redirectedUrl)
      }
    }
  }

  "post triage" - {

    "the setupGroup must be well formed" in {
      val taskListViewModel: TaskListViewModel =
        taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())
      val setupGroup: SectionGroupViewModel    = taskListViewModel.setupGroup

      setupGroup.heading       `must` `be`("taskList.setup.groupHeading")
      setupGroup.sections.size `must` `be`(1)
      setupGroup.isComplete    `must` `be`(false)

      val setupSection = setupGroup.sections(0)
      setupSection.status               `must` `be`(SectionStatus.InProgress)
      urlWithNoContext(setupSection.url) `must` `be`("/change-reason")
    }

    "the nextStepsGroup must be well formed" in {
      val taskListViewModel: TaskListViewModel  =
        taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())
      val nextStepsGroup: SectionGroupViewModel = taskListViewModel.nextStepsGroup

      nextStepsGroup.heading       `must` `be`("taskList.nextSteps.groupHeading")
      nextStepsGroup.sections.size `must` `be`(1)
      nextStepsGroup.isComplete    `must` `be`(false)

      val nextStepsSection = nextStepsGroup.sections(0)
      nextStepsSection.status `must` `be`(SectionStatus.CannotStartYet)
    }

    "the ltaGroup and aaGroup must not be defined" in {
      val taskListViewModel: TaskListViewModel =
        taskListService.taskListViewModel(userAnswersAferResubmittingAdjustmentSubmitted())
      taskListViewModel.aaGroup.isDefined  `must` `be`(false)
      taskListViewModel.ltaGroup.isDefined `must` `be`(false)
    }

    def userAnswersAferResubmittingAdjustmentSubmitted(): UserAnswers = {
      val answersWithPageData = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ResubmittingAdjustmentPage, true)
        .get

      val redirectedUrl = ResubmittingAdjustmentPage.navigate(NormalMode, answersWithPageData).url
      SetupSection.saveNavigation(answersWithPageData, redirectedUrl)
    }
  }

  "given aa is being reported and aa setup is complete" - {

    "pre triage" - {

      "the aa group must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

        aaGroup.sections.size `must` `be`(2)
        aaGroup.heading       `must` `be`("taskList.aa.groupHeading")
        aaGroup.isComplete   `must` `be`(false)
      }

      "the aa background section must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

        val aaBackgroundSection = aaGroup.sections(0)
        aaBackgroundSection.status                `must` `be`(SectionStatus.Completed)
        urlWithNoContext(aaBackgroundSection.url) `must` `be`("/annual-allowance/setup-check-answers")
      }

      "the aa period section must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup                              = taskListViewModel.aaGroup.get

        val periodSection = aaGroup.sections(1)
        periodSection.status                `must` `be`(SectionStatus.NotStarted)
        urlWithNoContext(periodSection.url) `must` `be`("/annual-allowance/2016/information")
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

    "post triage" - {

      "the aa group must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

        aaGroup.sections.size `must` `be`(2)
        aaGroup.heading       `must` `be`("taskList.aa.groupHeading")
        aaGroup.isComplete    `must` `be`(false)
      }

      "the aa background section must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup: SectionGroupViewModel       = taskListViewModel.aaGroup.get

        val aaBackgroundSection = aaGroup.sections(0)
        aaBackgroundSection.status                `must` `be`(SectionStatus.Completed)
        urlWithNoContext(aaBackgroundSection.url) `must` `be`("/annual-allowance/setup-check-answers")
      }

      "the aa period section must be well formed" in {
        val taskListViewModel: TaskListViewModel =
          taskListService.taskListViewModel(userAnswersWhenAAPreReqsSatisfied())
        val aaGroup                              = taskListViewModel.aaGroup.get

        val periodSection = aaGroup.sections(1)
        periodSection.status                `must` `be`(SectionStatus.NotStarted)
        urlWithNoContext(periodSection.url) `must` `be`("/annual-allowance/2016/information")
      }

      def userAnswersWhenAAPreReqsSatisfied(): UserAnswers = {
        val reportingChanges: Set[ReportingChange] = Set(AnnualAllowance)
        val answersWithPageData                    = emptyUserAnswers
          .set(PostTriageFlag, true)
          .get
          .set(ReportingChangePage, reportingChanges)
          .get
          .set(StopPayingPublicPensionPage, LocalDate.of(2015, 7, 1))
          .get
          .set(AAKickOutStatus(), 2)
          .get

        val answersWithSetupNav   =
          SetupSection.saveNavigation(answersWithPageData, SetupSection.checkYourSetupAnswersPage.url)
        val answersWithAASetupNav =
          PreAASection.saveNavigation(answersWithSetupNav, PreAASection.checkYourAASetupAnswersPage.url)

        answersWithAASetupNav
      }
    }
  }

  "pre triage" - {

    "given lta is being reported the user has navigated to another page" - {

      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers                                = emptyUserAnswers
        .set(ReportingChangePage, reportingChanges)
        .get

      val answersWithNav = LTASection.saveNavigation(answers, "some-url")

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answersWithNav)
      val ltaGroup                             = taskListViewModel.ltaGroup.get

      "the lta group must be well formed" in {
        ltaGroup.heading       `must` `be`("taskList.lta.groupHeading")
        ltaGroup.isComplete    `must` `be`(false)
        ltaGroup.sections.size `must` `be`(1)
      }

      "the lta section must be in progress with the correct url" in {
        val ltaSection = ltaGroup.sections(0)

        ltaSection.status                `must` `be`(SectionStatus.InProgress)
        urlWithNoContext(ltaSection.url) `must` `be`("some-url")
      }
    }
  }

  "post triage" - {

    "given lta is being reported the user has navigated to another page" - {

      val reportingChanges: Set[ReportingChange] = Set(LifetimeAllowance)
      val answers                                = emptyUserAnswers
        .set(PostTriageFlag, true)
        .get
        .set(ReportingChangePage, reportingChanges)
        .get
        .set(LTAKickOutStatus(), 2)
        .get

      val answersWithNav = LTASection.saveNavigation(answers, "some-url")

      val taskListViewModel: TaskListViewModel = taskListService.taskListViewModel(answersWithNav)
      val ltaGroup                             = taskListViewModel.ltaGroup.get

      "the lta group must be well formed" in {
        ltaGroup.heading       `must` `be`("taskList.lta.groupHeading")
        ltaGroup.isComplete    `must` `be`(false)
        ltaGroup.sections.size `must` `be`(1)
      }

      "the lta section must be in progress with the correct url" in {
        val ltaSection = ltaGroup.sections(0)

        ltaSection.status                `must` `be`(SectionStatus.InProgress)
        urlWithNoContext(ltaSection.url) `must` `be`("some-url")
      }
    }
  }
}
