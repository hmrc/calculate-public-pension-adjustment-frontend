package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AdminFeeDetailsFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "adminFeeDetails.error.required"
  val lengthKey = "adminFeeDetails.error.length"
  val maxLength = 500

  val form = new AdminFeeDetailsFormProvider()()

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
