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

class DefinedContribution2016PreAmountFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      newForm(),
      fieldName,
      validDataGenerator
    )

    behave like intField(
      newForm(),
      fieldName,
      nonNumericError = FormError(fieldName, "definedContribution2016PreAmount.error.nonNumeric", Seq("")),
      wholeNumberError = FormError(fieldName, "definedContribution2016PreAmount.error.wholeNumber", Seq(""))
    )

    behave like intFieldWithMaximum(
      newForm(),
      fieldName,
      maximum,
      expectedError = FormError(fieldName, "definedContribution2016PreAmount.error.maximum", Seq(maximum, ""))
    )

    behave like intFieldWithMinimum(
      newForm(),
      fieldName,
      minimum,
      expectedError = FormError(fieldName, "definedContribution2016PreAmount.error.minimum", Seq(minimum, ""))
    )

    behave like mandatoryField(
      newForm(),
      fieldName,
      requiredError = FormError(fieldName, "definedContribution2016PreAmount.error.required", Seq(""))
    )
  }

  private def newForm(): Form[BigInt] = {
    val formProvider = new DefinedContribution2016PreAmountFormProvider()
    formProvider(Seq(""))
  }
}
