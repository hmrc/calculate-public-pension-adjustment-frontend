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
import models.{Period, UserAnswers}
import pages.annualallowance.preaaquestions.{DefinedContributionPensionSchemePage, PIAPreRemedyPage, PayingPublicPensionSchemePage, ScottishTaxpayerFrom2016Page}
import pages.setupquestions.{ResubmittingAdjustmentPage, SavingsStatementPage}
import play.api.libs.json.Json

class SectionTest extends SpecBase {

  "Removing user answers from a section" - {

    "should succeed when user answers are incomplete" in {

      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.data must equal(Json.obj())
    }

    "should succeed when there are multiple answers" in {

      val userAnswers = emptyUserAnswers.set(SavingsStatementPage, true).get.set(ResubmittingAdjustmentPage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.data must equal(Json.obj())
    }

    "should not remove answers from another section" in {

      val userAnswers =
        emptyUserAnswers.set(SavingsStatementPage, true).get.set(DefinedContributionPensionSchemePage, true).get

      val userAnswersWithRemovals = SetupSection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.get(DefinedContributionPensionSchemePage) mustBe (Some(true))
    }

    "should succeed for a section with dynamically scoped data" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .get
        .set(PIAPreRemedyPage(Period._2013), BigInt(1))
        .get

      val userAnswersWithRemovals = PreAASection.removeAllUserAnswers(userAnswers)
      userAnswersWithRemovals.get(PIAPreRemedyPage(Period._2013)) mustBe None
    }
  }

  "Identifying a page" - {

    "should locate return to" in {

      val userAnswers: UserAnswers =
        emptyUserAnswers.set(ScottishTaxpayerFrom2016Page, false).get.set(PayingPublicPensionSchemePage, true).get

      val returnTo = PreAASection.returnTo(userAnswers)
      returnTo must be(PayingPublicPensionSchemePage)
    }
  }
}
