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

import play.api.i18n.Messages

final case class TaskListStatus(sectionStatus: SectionStatus, messages: Messages) {

  override def toString(): String =
    sectionStatus match {
      case SectionStatus.NotStarted     => messages("taskList.status.notStarted")
      case SectionStatus.InProgress     => messages("taskList.status.inProgress")
      case SectionStatus.Completed      => messages("taskList.status.completed")
      case SectionStatus.CannotStartYet => messages("taskList.status.cannotStartYet")

    }
}
