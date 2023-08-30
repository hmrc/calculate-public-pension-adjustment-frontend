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

package forms.annualallowance.taxyear

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class FlexiAccessDefinedContributionAmountFormProviderSpec extends IntFieldBehaviours {

  val formProvider = new FlexiAccessDefinedContributionAmountFormProvider()
  val form         = formProvider(Seq(""))

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, "flexiAccessDefinedContributionAmount.error.nonNumeric", Seq("")),
      wholeNumberError = FormError(fieldName, "flexiAccessDefinedContributionAmount.error.nonNumeric", Seq(""))
    )

    behave like intFieldWithMaximum(
      form,
      fieldName,
      maximum,
      expectedError = FormError(fieldName, "flexiAccessDefinedContributionAmount.error.maximum", Seq(maximum, ""))
    )

    behave like intFieldWithMinimum(
      form,
      fieldName,
      minimum,
      expectedError = FormError(fieldName, "flexiAccessDefinedContributionAmount.error.minimum", Seq(minimum, ""))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "flexiAccessDefinedContributionAmount.error.required", Seq(""))
    )
  }
}
