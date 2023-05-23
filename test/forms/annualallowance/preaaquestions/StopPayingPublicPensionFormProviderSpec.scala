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

package forms.annualallowance.preaaquestions

import java.time.LocalDate

import forms.behaviours.DateBehaviours


class StopPayingPublicPensionFormProviderSpec extends DateBehaviours {

  val form = new StopPayingPublicPensionFormProvider()()

  ".value" - {

    val validData = datesBetween(
      min = LocalDate.of(2015, 4, 6),
      max = LocalDate.of(2022, 4, 5)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "stopPayingPublicPension.error.required.all")
  }
}
