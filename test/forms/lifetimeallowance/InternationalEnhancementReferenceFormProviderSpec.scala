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

class InternationalEnhancementReferenceFormProviderSpec extends StringFieldBehaviours {

  val invalidKey  = "internationalEnhancementReference.error.invalid"
  val requiredKey = "internationalEnhancementReference.error.required"
  val lengthKey   = "internationalEnhancementReference.error.length"
  val maxLength   = 15

  val form = new InternationalEnhancementReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      alphanumericStringWithMaxLength(maxLength)
    )

    behave like fieldThatDoesNotBindInvalidStrings(
      form = form,
      fieldName = fieldName,
      regex = """^[a-z0-9A-Z]*$""",
      gen = stringsOfLength(maxLength),
      invalidKey = invalidKey
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
}
