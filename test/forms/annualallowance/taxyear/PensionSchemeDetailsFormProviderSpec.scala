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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PensionSchemeDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new PensionSchemeDetailsFormProvider()()

  ".schemeName" - {

    val fieldName   = "schemeName"
    val requiredKey = "pensionSchemeDetails.error.schemeName.required"
    val lengthKey   = "pensionSchemeDetails.error.schemeName.length"
    val maxLength   = 100

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

  ".schemeTaxRef" - {

    val fieldName   = "schemeTaxRef"
    val requiredKey = "pensionSchemeDetails.error.schemeTaxRef.required"
    val invalidKey  = "pensionSchemeDetails.error.schemeTaxRef.invalid"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validPstrs
    )

    behave like fieldThatDoesNotBindInvalidStrings(
      form,
      fieldName,
      regex = """(\d\s*){8}[A-Za-z]{2}""",
      stringsOfLength(10),
      invalidKey
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq("""(\d\s*){8}[A-Za-z]{2}"""))
    )
  }
}
