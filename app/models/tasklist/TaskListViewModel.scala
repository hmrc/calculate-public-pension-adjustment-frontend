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

final case class TaskListViewModel(
  setupGroup: SectionGroupViewModel,
  aaGroup: Option[SectionGroupViewModel],
  ltaGroup: Option[SectionGroupViewModel],
  adminGroup: Option[SectionGroupViewModel],
  calculationResult: Option[String] = None
) {

  def allGroups: Seq[Option[SectionGroupViewModel]] = Seq(Some(setupGroup), aaGroup, ltaGroup, adminGroup)

  def completedGroupCount: Int =
    allGroups.filter(groupOption => groupOption.isDefined).count(group => group.get.isComplete)

  def groupCount: Int = allGroups.count(groupOption => groupOption.isDefined)
}
