package forms

import forms.behaviours.StringFieldBehaviours
import forms.lifetimeallowance.SchemeNameAndTaxRefFormProvider
import play.api.data.FormError

class SchemeNameAndTaxRefFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "schemeNameAndTaxRef.error.required"
  val lengthKey = "schemeNameAndTaxRef.error.length"
  val maxLength = 100

  val form = new SchemeNameAndTaxRefFormProvider()()

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
