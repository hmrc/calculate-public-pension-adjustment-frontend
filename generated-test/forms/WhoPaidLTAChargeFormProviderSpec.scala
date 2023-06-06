package forms

import forms.behaviours.OptionFieldBehaviours
import forms.lifetimeallowance.WhoPaidLTAChargeFormProvider
import models.WhoPaidLTACharge
import play.api.data.FormError

class WhoPaidLTAChargeFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhoPaidLTAChargeFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whoPaidLTACharge.error.required"

    behave like optionsField[WhoPaidLTACharge](
      form,
      fieldName,
      validValues  = WhoPaidLTACharge.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
