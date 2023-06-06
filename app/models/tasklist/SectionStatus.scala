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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag

sealed trait SectionStatus {
  def tag(implicit messages: Messages): Tag
}

object SectionStatus {

  case object NotStarted extends SectionStatus {
    override def tag(implicit messages: Messages): Tag =
      Tag(
        content = Text(messages("taskList.status.notStarted"))
      )
  }

  case object InProgress extends SectionStatus {
    override def tag(implicit messages: Messages): Tag =
      Tag(
        content = Text(messages("taskList.status.inProgress"))
      )
  }

  case object Completed extends SectionStatus {
    override def tag(implicit messages: Messages): Tag =
      Tag(
        content = Text(messages("taskList.status.completed"))
      )
  }

}
