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

package pages.setupquestions

import pages.behaviours.PageBehaviours

class ResubmittingAdjustmentPageSpec extends PageBehaviours {

  "ResubmittingAdjustmentPage" - {

    beRetrievable[Boolean](ResubmittingAdjustmentPage)

    beSettable[Boolean](ResubmittingAdjustmentPage)

    beRemovable[Boolean](ResubmittingAdjustmentPage)

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
