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
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.FormError
import play.api.i18n.Messages

class AmountSalarySacrificeArrangementsFormProviderSpec extends IntFieldBehaviours {

  val messages             = mock[Messages]
  val startEndDate: String = "Between 6th April 2018 to 5th April 2019"
  val form = new AmountSalarySacrificeArrangementsFormProvider()(startEndDate)(messages)

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
      nonNumericError = FormError(fieldName, "amountSalarySacrificeArrangements.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "amountSalarySacrificeArrangements.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "amountSalarySacrificeArrangements.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "amountSalarySacrificeArrangements.error.required")
    )
  }
}
