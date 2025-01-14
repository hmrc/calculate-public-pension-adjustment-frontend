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

class TotalIncomeFormProviderSpec extends IntFieldBehaviours {

  val startEndDate: String = "6 April 2018 to 5 April 2019"

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      newForm(startEndDate),
      fieldName,
      validDataGenerator
    )

    behave like intField(
      newForm(startEndDate),
      fieldName,
      nonNumericError = FormError(fieldName, "totalIncome.error.nonNumeric", Seq(s"$startEndDate")),
      wholeNumberError = FormError(fieldName, "totalIncome.error.wholeNumber", Seq(s"$startEndDate"))
    )

    behave like intFieldWithMaximum(
      newForm(startEndDate),
      fieldName,
      maximum = maximum,
      expectedError = FormError(fieldName, "totalIncome.error.maximum", Seq(maximum, s"$startEndDate"))
    )

    behave like intFieldWithMinimum(
      newForm(startEndDate),
      fieldName,
      minimum = minimum,
      expectedError = FormError(fieldName, "totalIncome.error.minimum", Seq(minimum, s"$startEndDate"))
    )

    behave like mandatoryField(
      newForm(startEndDate),
      fieldName,
      requiredError = FormError(fieldName, "totalIncome.error.required", Seq(s"$startEndDate"))
    )
  }

  def newForm(startEndDate: String): Form[BigInt] = {
    val formProvider = new TotalIncomeFormProvider()
    formProvider(startEndDate)
  }
}
