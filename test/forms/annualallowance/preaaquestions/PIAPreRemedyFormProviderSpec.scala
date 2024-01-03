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

package forms.annualallowance.preaaquestions

import forms.behaviours.IntFieldBehaviours
import models.Period
import play.api.data.FormError

class PIAPreRemedyFormProviderSpec extends IntFieldBehaviours {

  val formProvider = new PIAPreRemedyFormProvider()
  val form         = formProvider(Period._2013)

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
      nonNumericError = FormError(fieldName, "pIAPreRemedy.error.nonNumeric.2013"),
      wholeNumberError = FormError(fieldName, "pIAPreRemedy.error.nonNumeric.2013")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "pIAPreRemedy.error.required.2013")
    )

    behave like intFieldWithMaximum(
      form,
      fieldName,
      maximum,
      expectedError = FormError(fieldName, "pIAPreRemedy.error.maximum.2013", Seq(maximum, ""))
    )

    behave like intFieldWithMinimum(
      form,
      fieldName,
      minimum,
      expectedError = FormError(fieldName, "pIAPreRemedy.error.minimum.2013", Seq(minimum, ""))
    )
  }
}
