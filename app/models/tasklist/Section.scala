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

import models.UserAnswers
import pages.Page
import queries.Settable

trait Section {

  def remove(answers: UserAnswers, forPages: Seq[Page]): UserAnswers =
    if (forPages.nonEmpty) {
      val page: Page = forPages.head

      val updatedAnswers =
        page match {
          case settablePage: Settable[?] => answers.remove(settablePage).get
          case _                         => answers
        }

      remove(updatedAnswers, forPages.tail)
    } else { answers }
}
