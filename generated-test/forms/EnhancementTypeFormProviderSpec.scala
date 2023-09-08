package forms

import forms.behaviours.OptionFieldBehaviours
import models.EnhancementType
import play.api.data.FormError

class EnhancementTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new EnhancementTypeFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "enhancementType.error.required"

    behave like optionsField[EnhancementType](
      form,
      fieldName,
      validValues  = EnhancementType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
