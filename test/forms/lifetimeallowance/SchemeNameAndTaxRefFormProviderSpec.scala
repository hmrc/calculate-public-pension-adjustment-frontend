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

package forms.lifetimeallowance

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class SchemeNameAndTaxRefFormProviderSpec extends StringFieldBehaviours {

  val form = new SchemeNameAndTaxRefFormProvider()()

  ".name" - {

    val fieldName = "name"
    val requiredKey = "schemeNameAndTaxRef.name.error.required"
    val lengthKey = "schemeNameAndTaxRef.name.error.length"
    val maxLength = 100

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".taxRef" - {

    val fieldName = "taxRef"
    val requiredKey = "schemeNameAndTaxRef.taxRef.error.required"


    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validPstrs
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
