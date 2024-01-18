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
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.FormError
import play.api.i18n.Messages

class HowMuchAAChargeYouPaidFormProviderSpec extends IntFieldBehaviours {

  ".value" - {

    val fieldName = "value"

    val minimum = 0
    val maximum = 999999999

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      formWithMockMessages,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      formWithMockMessages,
      fieldName,
      nonNumericError = FormError(fieldName, "howMuchAAChargeYouPaid.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "howMuchAAChargeYouPaid.error.wholeNumber")
    )

    behave like intFieldWithRange(
      formWithMockMessages,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "howMuchAAChargeYouPaid.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      formWithMockMessages,
      fieldName,
      requiredError = FormError(fieldName, "error message")
    )

    def formWithMockMessages = {
      val messages = mock[Messages]
      when(messages.apply(eqTo("howMuchAAChargeYouPaid.error.required"), any())).thenReturn("error message")

      val formProvider = new HowMuchAAChargeYouPaidFormProvider()
      formProvider("")(messages)
    }
  }
}
