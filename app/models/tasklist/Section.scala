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

import models.UserAnswers
import pages.Page
import queries.Settable

trait Section {

  def pages(): Seq[Page]
  def status(answers: UserAnswers): SectionStatus
  def removeAllUserAnswers(answers: UserAnswers): UserAnswers = remove(answers, pages())
  def checkYourAnswersPage: Page

  def remove(answers: UserAnswers, forPages: Seq[Page]): UserAnswers =
    if (forPages.headOption.isDefined) {
      remove(answers.remove(forPages.head.asInstanceOf[Settable[Any]]).get, forPages.tail)
    } else answers

  def returnTo(answers: UserAnswers): Page =
    if (status(answers) == SectionStatus.Completed) {
      checkYourAnswersPage
    } else {
      pages().findLast(page => answers.containsAnswerFor(page)).getOrElse(pages().head)
    }
}
