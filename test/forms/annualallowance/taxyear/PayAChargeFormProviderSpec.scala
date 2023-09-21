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

import forms.behaviours.BooleanFieldBehaviours
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.FormError
import play.api.i18n.Messages

class PayAChargeFormProviderSpec extends BooleanFieldBehaviours {

  val invalidKey = "error.boolean"

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      formWithMockMessages,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      formWithMockMessages,
      fieldName,
      requiredError = FormError(fieldName, "error message ")
    )

    def formWithMockMessages = {
      val messages = mock[Messages]
      when(messages.apply(eqTo("payACharge.error.required"), any())).thenReturn("error message")

      new PayAChargeFormProvider()("")(messages)
    }
  }
}
