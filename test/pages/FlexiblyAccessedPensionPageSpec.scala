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

package pages

import pages.behaviours.PageBehaviours

import java.time.LocalDate

class FlexiblyAccessedPensionPageSpec extends PageBehaviours {

  "FlexiblyAccessedPensionPage" - {

    beRetrievable[Boolean](FlexiblyAccessedPensionPage)

    beSettable[Boolean](FlexiblyAccessedPensionPage)

    beRemovable[Boolean](FlexiblyAccessedPensionPage)

    "must not remove dependent state when the answer is yes" in {

      val answers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .flatMap(a => a.set(FlexiblyAccessedPensionPage, true))
        .flatMap(a => a.set(FlexibleAccessStartDatePage, LocalDate.now))
        .success
        .value

      val result = answers.set(FlexiblyAccessedPensionPage, true).success.value

      result.get(DefinedContributionPensionSchemePage) must be(defined)
      result.get(FlexiblyAccessedPensionPage)          must be(defined)
      result.get(FlexibleAccessStartDatePage)          must be(defined)
    }

    "must remove dependent state when the answer is no" in {

      val answers = emptyUserAnswers
        .set(DefinedContributionPensionSchemePage, true)
        .flatMap(a => a.set(FlexiblyAccessedPensionPage, true))
        .flatMap(a => a.set(FlexibleAccessStartDatePage, LocalDate.now))
        .success
        .value

      val result = answers.set(FlexiblyAccessedPensionPage, false).success.value

      result.get(DefinedContributionPensionSchemePage) must be(defined)
      result.get(FlexiblyAccessedPensionPage)          must be(defined)
      result.get(FlexibleAccessStartDatePage)          must not be defined
    }
  }
}
