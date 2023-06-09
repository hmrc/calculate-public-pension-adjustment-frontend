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

package viewmodels.checkAnswers.lifetimeallowance

import controllers.lifetimeallowance.routes
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.lifetimeallowance.{ProtectionReferencePage, ReferenceNewProtectionTypeEnhancementPage}
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ReferenceNewProtectionTypeEnhancementSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          ReferenceNewProtectionTypeEnhancementPage,
          "test123"
        )
        .get
      ReferenceNewProtectionTypeEnhancementSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "ReferenceNewProtectionTypeEnhancement.checkYourAnswersLabel",
          value = ValueViewModel("test123"),
          actions = Seq(
            ActionItemViewModel("site.change", routes.ReferenceNewProtectionTypeEnhancementController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("ReferenceNewProtectionTypeEnhancement.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      ReferenceNewProtectionTypeEnhancementSummary.row(userAnswers) shouldBe None
    }
  }

}
