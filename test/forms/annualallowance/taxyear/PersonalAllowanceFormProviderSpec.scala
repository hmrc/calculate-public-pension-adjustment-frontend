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

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

class PersonalAllowanceFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    val formProvider = new PersonalAllowanceFormProvider()()

    behave like fieldThatBindsValidData(
      formProvider,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      formProvider,
      fieldName,
      nonNumericError = FormError(fieldName, "personalAllowance.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "personalAllowance.error.wholeNumber")
    )

    behave like intFieldWithRange(
      formProvider,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "personalAllowance.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      formProvider,
      fieldName,
      requiredError = FormError(fieldName, "personalAllowance.error.required")
    )
  }
}
