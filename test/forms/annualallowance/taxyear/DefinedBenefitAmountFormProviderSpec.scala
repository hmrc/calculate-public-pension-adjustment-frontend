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
import models.Period
import play.api.data.FormError

class DefinedBenefitAmountFormProviderSpec extends IntFieldBehaviours {

  val formProvider = new DefinedBenefitAmountFormProvider()
  val form         = formProvider(Period._2023)

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
      nonNumericError = FormError(fieldName, "definedBenefitAmount.error.nonNumeric.2023"),
      wholeNumberError = FormError(fieldName, "definedBenefitAmount.error.nonNumeric.2023")
    )

    behave like intFieldWithMaximum(
      form,
      fieldName,
      maximum,
      expectedError = FormError(fieldName, "definedBenefitAmount.error.maximum.2023", Seq(maximum, ""))
    )

    behave like intFieldWithMinimum(
      form,
      fieldName,
      minimum,
      expectedError = FormError(fieldName, "definedBenefitAmount.error.minimum.2023", Seq(minimum, ""))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "definedBenefitAmount.error.required.2023")
    )
  }
}
