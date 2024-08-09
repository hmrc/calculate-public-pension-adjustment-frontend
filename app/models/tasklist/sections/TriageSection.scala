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

import models.UserAnswers
import models.tasklist.Section
import _root_.pages.Page
import _root_.pages.setupquestions.lifetimeallowance.HadBenefitCrystallisationEventPage
import play.api.libs.json.JsPath

case object TriageSection extends Section {
  def removeAllKickOutStatusUserAnswers(userAnswers: UserAnswers): UserAnswers =
    userAnswers.removePath(JsPath \ "kickoutStatus").get

  def removeAllLTAUserAnswers(userAnswers: UserAnswers): UserAnswers =
    userAnswers.removePath(JsPath \ "setup" \ "lta").get
}
