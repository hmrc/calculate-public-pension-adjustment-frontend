package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhatNewProtectionTypeEnhancement
import play.api.data.FormError

class WhatNewProtectionTypeEnhancementFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhatNewProtectionTypeEnhancementFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whatNewProtectionTypeEnhancement.error.required"

    behave like optionsField[WhatNewProtectionTypeEnhancement](
      form,
      fieldName,
      validValues  = WhatNewProtectionTypeEnhancement.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
