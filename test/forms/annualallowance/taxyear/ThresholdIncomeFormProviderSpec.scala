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

package forms.annualallowance.taxyear

import forms.behaviours.OptionFieldBehaviours
import models.{Period, ThresholdIncome}
import play.api.data.FormError

class ThresholdIncomeFormProviderSpec extends OptionFieldBehaviours {

  val requiredBelowKey = "thresholdIncome.error.required.below2021"
  val requiredAboveKey = "thresholdIncome.error.required.2021AndAbove"
  val invalidKey       = "error.invalid"

  val formBelow = new ThresholdIncomeFormProvider()(Period._2013)

  ".value period below 2021" - {

    val fieldName = "value"

    behave like optionsField[ThresholdIncome](
      formBelow,
      fieldName,
      validValues = ThresholdIncome.values,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      formBelow,
      fieldName,
      requiredError = FormError(fieldName, requiredBelowKey)
    )
  }

  val formAfter = new ThresholdIncomeFormProvider()(Period._2021)

  ".value period after 2021" - {

    val fieldName = "value"

    behave like optionsField[ThresholdIncome](
      formAfter,
      fieldName,
      validValues = ThresholdIncome.values,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      formAfter,
      fieldName,
      requiredError = FormError(fieldName, requiredAboveKey)
    )
  }
}
