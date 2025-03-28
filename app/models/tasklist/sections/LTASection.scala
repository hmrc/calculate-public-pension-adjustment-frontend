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

package models.tasklist.sections

import models.tasklist.SectionStatus.{Completed, InProgress, NotStarted}
import models.tasklist.{Section, SectionStatus}
import models.{SectionNavigation, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object LTASection extends Section {

  def removeAllUserAnswers(userAnswers: UserAnswers): UserAnswers =
    userAnswers.removePath(JsPath \ "lta").get.removePath(JsPath \ "setup" \ "lta").get

  def removeAllUserAnswersAndNavigation(userAnswers: UserAnswers): UserAnswers =
    userAnswers
      .removePath(JsPath \ "lta")
      .get
      .remove(sectionNavigation)
      .get

  val initialPage: Call                     = controllers.lifetimeallowance.routes.WhatYouWillNeedLtaController.onPageLoad()
  val checkYourLTAAnswersPage: Call         = controllers.lifetimeallowance.routes.CheckYourLTAAnswersController.onPageLoad()
  val cannotUseLtaServiceNoChargePage: Call =
    controllers.lifetimeallowance.routes.CannotUseLtaServiceNoChargeController.onPageLoad()

  def status(answers: UserAnswers): SectionStatus =
    navigateTo(answers) match {
      case initialPage.url             => NotStarted
      case checkYourLTAAnswersPage.url => Completed
      case _                           => InProgress
    }

  private val sectionNavigation: SectionNavigation = SectionNavigation("ltaSection")

  def navigateTo(answers: UserAnswers): String = {
    val taskListNavLink = answers.get(sectionNavigation).getOrElse(initialPage.url)
    taskListNavLink match {
      case cannotUseLtaServiceNoChargePage.url => checkYourLTAAnswersPage.url
      case _                                   => taskListNavLink
    }
  }

  def kickoutHasBeenReached(answers: UserAnswers): Boolean = {
    val taskListNavLink: Option[String] = answers.get(sectionNavigation)
    taskListNavLink match {
      case Some(cannotUseLtaServiceNoChargePage.url) => true
      case _                                         => false
    }
  }

  def saveNavigation(answers: UserAnswers, urlFragment: String): UserAnswers =
    answers.set(sectionNavigation, urlFragment).get
}
