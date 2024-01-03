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

package pages.setupquestions

import models.{CheckMode, NormalMode}
import pages.behaviours.PageBehaviours

class ResubmittingAdjustmentPageSpec extends PageBehaviours {

  "ResubmittingAdjustmentPage" - {

    beRetrievable[Boolean](ResubmittingAdjustmentPage)

    beSettable[Boolean](ResubmittingAdjustmentPage)

    beRemovable[Boolean](ResubmittingAdjustmentPage)

    "Normal mode" - {

      "must redirect to reason for resubmission page when true" in {

        val ua = emptyUserAnswers
          .set(ResubmittingAdjustmentPage, true)
          .success
          .value

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/change-reason")
      }

      "must redirect to reporting change page when false" in {

        val ua = emptyUserAnswers
          .set(ResubmittingAdjustmentPage, false)
          .success
          .value

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/charges")
      }

      "must redirect to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(NormalMode, ua).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "Check mode" - {

      "must redirect to reason for resubmission page when true" in {

        val ua = emptyUserAnswers
          .set(ResubmittingAdjustmentPage, true)
          .success
          .value

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/change-change-reason")
      }

      "must redirect to CYA page when false" in {

        val ua = emptyUserAnswers
          .set(ResubmittingAdjustmentPage, false)
          .success
          .value

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/check-your-answers-setup")
      }

      "must redirect to journey recovery when no answer" in {

        val ua = emptyUserAnswers

        val nextPageUrl: String = ResubmittingAdjustmentPage.navigate(CheckMode, ua).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")
      }
    }

    "must not remove ReasonForResubmissionPage when the answer is yes" in {

      val answers = emptyUserAnswers.set(ReasonForResubmissionPage, "test data").success.value

      val result = answers.set(ResubmittingAdjustmentPage, true).success.value

      result.get(ResubmittingAdjustmentPage) must be(defined)
      result.get(ReasonForResubmissionPage)  must be(defined)
    }

    "must remove ReasonForResubmissionPage when the answer is no" in {

      val answers = emptyUserAnswers.set(ReasonForResubmissionPage, "test data").success.value

      val result = answers.set(ResubmittingAdjustmentPage, false).success.value

      result.get(ResubmittingAdjustmentPage) must be(defined)
      result.get(ReasonForResubmissionPage)  must not be defined
    }
  }
}
