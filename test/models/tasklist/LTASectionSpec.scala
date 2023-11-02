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

import base.SpecBase
import models.tasklist.sections.LTASection

class LTASectionSpec extends SpecBase {

  "When user has not answered first page" in {
    val userAnswers = emptyUserAnswers

    val status = LTASection.status(userAnswers)

    status mustBe (SectionStatus.NotStarted)
  }

  "When user has navigated to the not able to use this service kick out" in {
    val answersWithNav = LTASection.saveNavigation(emptyUserAnswers, LTASection.notAbleToUseThisServicePage.url)

    val status = LTASection.status(answersWithNav)

    status mustBe (SectionStatus.Completed)
  }

  "When user has navigated to cannot use lta service no charge page" in {
    val answersWithNav = LTASection.saveNavigation(emptyUserAnswers, LTASection.cannotUseLtaServiceNoChargePage.url)

    val status = LTASection.status(answersWithNav)

    status mustBe (SectionStatus.Completed)
  }

  "When user has navigated to another url" in {
    val answersWithNav = LTASection.saveNavigation(emptyUserAnswers, "some-url")

    val status = LTASection.status(answersWithNav)

    status mustBe (SectionStatus.InProgress)
  }

  "When user has navigated to check your lta answers page" in {
    val answersWithNav = LTASection.saveNavigation(emptyUserAnswers, LTASection.checkYourLTAAnswersPage.url)

    val status = LTASection.status(answersWithNav)

    status mustBe (SectionStatus.Completed)
  }
}
