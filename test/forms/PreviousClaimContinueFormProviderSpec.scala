package forms

import forms.behaviours.OptionFieldBehaviours
import models.PreviousClaimContinue
import play.api.data.FormError

class PreviousClaimContinueFormProviderSpec extends OptionFieldBehaviours {

  val form = new PreviousClaimContinueFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "previousClaimContinue.error.required"

    behave like optionsField[PreviousClaimContinue](
      form,
      fieldName,
      validValues  = PreviousClaimContinue.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
