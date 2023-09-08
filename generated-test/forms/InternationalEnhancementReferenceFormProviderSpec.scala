package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class InternationalEnhancementReferenceFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "internationalEnhancementReference.error.required"
  val lengthKey = "internationalEnhancementReference.error.length"
  val maxLength = 15

  val form = new InternationalEnhancementReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

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
}
