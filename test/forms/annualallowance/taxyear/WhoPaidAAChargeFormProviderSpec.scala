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
import models.WhoPaidAACharge
import play.api.data.FormError

class WhoPaidAAChargeFormProviderSpec extends OptionFieldBehaviours {

  val schemeName: String   = "Test123"
  val startEndDate: String = "Between 6th April 2018 to 5th April 2019"
  val formProvider         = new WhoPaidAAChargeFormProvider()
  val form                 = formProvider(schemeName, startEndDate)

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "whoPaidAACharge.error.required"

    behave like optionsField[WhoPaidAACharge](
      form,
      fieldName,
      validValues = WhoPaidAACharge.values,
      invalidError = FormError(fieldName, "error.invalid", List(schemeName, startEndDate))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, List(schemeName, startEndDate))
    )
  }
}
