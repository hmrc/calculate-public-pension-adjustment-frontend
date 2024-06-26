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
import play.api.data.{Form, FormError}

class MarriageAllowanceAmountFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val minimum = 1
    val maximum = 1260

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      newForm,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      newForm,
      fieldName,
      nonNumericError =
        FormError(fieldName, "marriageAllowanceAmount.error.nonNumeric", Seq("6 April 2022 to 5 April 2023", "1260")),
      wholeNumberError =
        FormError(fieldName, "marriageAllowanceAmount.error.wholeNumber", Seq("6 April 2022 to 5 April 2023", "1260"))
    )

    behave like mandatoryField(
      newForm,
      fieldName,
      requiredError =
        FormError(fieldName, "marriageAllowanceAmount.error.required", Seq("6 April 2022 to 5 April 2023", "1260"))
    )
  }

  private def newForm(): Form[BigInt] = {
    val form = new MarriageAllowanceAmountFormProvider()
    form(Seq("6 April 2022 to 5 April 2023", "1260"))
  }
}
