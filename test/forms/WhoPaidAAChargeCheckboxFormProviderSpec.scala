package forms

import forms.annualallowance.taxyear.WhoPaidAAChargeCheckboxFormProvider
import forms.behaviours.CheckboxFieldBehaviours
import models.WhoPaidAAChargeCheckbox
import play.api.data.FormError

class WhoPaidAAChargeCheckboxFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new WhoPaidAAChargeCheckboxFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whoPaidAAChargeCheckbox.error.required"

    behave like checkboxField[WhoPaidAAChargeCheckbox](
      form,
      fieldName,
      validValues  = WhoPaidAAChargeCheckbox.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
